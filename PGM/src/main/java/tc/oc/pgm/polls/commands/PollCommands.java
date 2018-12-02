package tc.oc.pgm.polls.commands;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import com.sk89q.minecraft.util.commands.NestedCommand;
import javax.inject.Inject;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;
import tc.oc.api.docs.PlayerId;
import tc.oc.commons.bukkit.chat.PlayerComponent;
import tc.oc.commons.bukkit.nick.IdentityProvider;
import tc.oc.commons.core.chat.Audiences;
import tc.oc.commons.core.chat.Component;
import tc.oc.commons.core.commands.TranslatableCommandException;
import tc.oc.pgm.commands.CommandUtils;
import tc.oc.pgm.polls.Poll;
import tc.oc.pgm.polls.PollEndReason;
import tc.oc.pgm.polls.PollManager;

public class PollCommands {

    private final PollManager pollManager;
    private final IdentityProvider identityProvider;
    private final Audiences audiences;

    @Inject PollCommands(PollManager pollManager, IdentityProvider identityProvider, Audiences audiences){
        this.pollManager = pollManager;
        this.identityProvider = identityProvider;
        this.audiences = audiences;
    }

    @Command(
        aliases = {"poll"},
        desc = "Poll commands",
        min = 1,
        max = -1
    )
    @NestedCommand({PollSubCommands.class})
    public void pollCommand() {
    }

    @Command(
        aliases = {"vote"},
        desc = "Vote in a running poll.",
        usage = "[yes|no]",
        min = 1,
        max = 1
    )
    @CommandPermissions("poll.vote")
    public void vote(CommandContext args, CommandSender sender) throws CommandException {
        PlayerId voter = CommandUtils.senderToMatchPlayer(sender).getPlayerId();
        Poll currentPoll = pollManager.getPoll();
        if(currentPoll != null) {
            if(args.getString(0).equalsIgnoreCase("yes")) {
                currentPoll.voteFor(voter);
                sender.sendMessage(new Component(ChatColor.GREEN).translate("poll.vote.for"));
            } else if (args.getString(0).equalsIgnoreCase("no")) {
                currentPoll.voteAgainst(voter);
                sender.sendMessage(new Component(ChatColor.RED).translate("poll.vote.against"));
            } else {
                throw new TranslatableCommandException("poll.vote.invalidValue");
            }
        } else {
            throw new TranslatableCommandException("poll.noPollRunning");
        }
    }

    @Command(
       aliases = {"veto"},
       desc = "Veto the current poll.",
       min = 0,
       max = 0
    )
    @CommandPermissions("poll.veto")
    public void veto(CommandContext args, CommandSender sender) throws CommandException {
        if(pollManager.isPollRunning()) {
            pollManager.endPoll(PollEndReason.Cancelled);
            audiences.all().sendMessage(new Component(ChatColor.RED).translate("poll.veto", new PlayerComponent(identityProvider.currentIdentity(sender))));
        } else {
            throw new TranslatableCommandException("poll.noPollRunning");
        }
    }

}
