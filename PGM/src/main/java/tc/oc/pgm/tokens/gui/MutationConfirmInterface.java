package tc.oc.pgm.tokens.gui;

import com.google.inject.Inject;
import com.sk89q.minecraft.util.commands.CommandException;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import tc.oc.commons.bukkit.gui.buttons.Button;
import tc.oc.commons.bukkit.gui.interfaces.ChestInterface;
import tc.oc.commons.bukkit.util.ItemCreator;
import tc.oc.pgm.commands.CommandUtils;
import tc.oc.pgm.mutation.Mutation;
import tc.oc.pgm.polls.PollManager;
import tc.oc.pgm.polls.types.PollMutation;

import java.util.ArrayList;
import java.util.List;

public class MutationConfirmInterface extends ChestInterface {
    private Mutation mutation;

    @Inject PollManager pollManager;
    @Inject PollMutation.Factory pollMutationFactory;

    public MutationConfirmInterface(Player player, Mutation mutation) {
        super(player, new ArrayList<Button>(), 27, "Confirmation Menu");
        updateButtons();
        this.mutation = mutation;
    }

    @Override
    public void updateButtons() {
        List<Button> buttons = new ArrayList<>();

        buttons.add(new Button(new ItemCreator(Material.WOOL)
                .setData(5)
                .setName( ChatColor.GREEN + "Confirm" ), 12){
            @Override
            public void function(Player player) {
                try {
                    pollManager.startPoll(pollMutationFactory.create(player, mutation, CommandUtils.senderToMatchPlayer(player).getPlayerId()));
                    player.closeInventory();
                } catch (CommandException e) {
                    player.sendMessage(ChatColor.RED + "Another poll is already running.");
                    player.closeInventory();
                }
            }
        });

        buttons.add(new Button(new ItemCreator(Material.WOOL)
                .setData(14)
                .setName( ChatColor.GREEN + "Cancel" ), 14){
            @Override
            public void function(Player player) {
                player.openInventory(new MutationTokenInterface(player).getInventory());
            }
        });

        setButtons(buttons);
        updateInventory();
    }

}
