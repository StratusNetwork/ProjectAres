package tc.oc.pgm.rotation;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import com.sk89q.minecraft.util.commands.NestedCommand;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;

import tc.oc.commons.core.chat.Audience;
import tc.oc.commons.bukkit.chat.Audiences;
import tc.oc.commons.core.chat.Component;
import tc.oc.pgm.commands.CommandUtils;
import tc.oc.pgm.match.MatchManager;


import javax.inject.Inject;

// TODO: properly bind commands
public class RotationControlCommands {
    public static class RotationControlParent {
        @Command(
            aliases = {"rotationcontrol", "rotcontrol", "rotcon", "controlrotation", "controlrot", "crot"},
            desc = "Commands for controlling the rotation",
            min = 1,
            max = -1
        )
        @NestedCommand({RotationControlCommands.class})
        public static void rotationcontrol() {
        }
    }

    private final MatchManager matchManager;
    private final Audiences audiences;

    @Inject RotationControlCommands(MatchManager matchManager, Audiences audiences) {
        this.matchManager = matchManager;
        this.audiences = audiences;
    }

    @Command(
        aliases = {"set", "s"},
        desc = "Sets the current rotation",
        min = 1,
        max = -1
    )
    @CommandPermissions("pgm.rotation.set")
    public void info(final CommandContext args, final CommandSender sender) throws CommandException {
        final Audience audience = audiences.get(sender);
        RotationManager manager = matchManager.getRotationManager();

        String name = args.getJoinedStrings(0);
        CommandUtils.getRotation(name, sender);

        manager.setCurrentRotationName(name);
        audience.sendMessage(new Component(ChatColor.GRAY).translate("command.rotation.set.success", new Component(name).color(ChatColor.AQUA)));
    }
}
