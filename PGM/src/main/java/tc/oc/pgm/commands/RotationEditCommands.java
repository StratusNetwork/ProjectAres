package tc.oc.pgm.commands;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import com.sk89q.minecraft.util.commands.NestedCommand;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;

import tc.oc.commons.core.chat.Component;
import tc.oc.commons.core.commands.NestedCommands;
import tc.oc.commons.core.commands.TranslatableCommandException;
import tc.oc.pgm.map.PGMMap;
import tc.oc.pgm.match.MatchManager;
import tc.oc.pgm.rotation.AppendTransformation;
import tc.oc.pgm.rotation.InsertTransformation;
import tc.oc.pgm.rotation.RemoveAllTransformation;
import tc.oc.pgm.rotation.RemoveIndexTransformation;
import tc.oc.pgm.rotation.RotationManager;
import tc.oc.pgm.rotation.RotationState;
import tc.oc.pgm.rotation.RotationTransformation;

import javax.inject.Inject;

public class RotationEditCommands implements NestedCommands {
    public static class RotationEditParent {
        @Command(
            aliases = {"rotationedit", "rotedit", "roted", "editrotation", "editrot", "erot"},
            desc = "Commands for editing the rotation and reloading it",
            min = 1,
            max = -1
        )
        @NestedCommand({RotationEditCommands.class})
        public static void editrot() {
        }
    }

    private final MatchManager matchManager;

    @Inject RotationEditCommands(MatchManager matchManager) {
        this.matchManager = matchManager;
    }

    @Command(
        aliases = {"reload"},
        desc = "Reload the map rotation from it's provider",
        min = 0,
        max = 0
    )
    @CommandPermissions("pgm.rotation.reload")
    public void reload(CommandContext args, CommandSender sender) throws CommandException {
        boolean success = matchManager.loadRotations();
        if(success) {
            sender.sendMessage(new Component(ChatColor.GREEN).translate("command.rotation.reload.success"));
        } else {
            throw new TranslatableCommandException("command.rotation.reload.failed");
        }
    }

    @Command(
        aliases = {"append", "a"},
        desc = "Append a map to the end of the rotation",
        usage = "[map name]",
        min = 1,
        max = -1
    )
    @CommandPermissions("pgm.rotation.append")
    public void append(CommandContext args, CommandSender sender) throws CommandException {
        PGMMap map = CommandUtils.getMap(args.getJoinedStrings(0), sender);

        apply(new AppendTransformation(map));
        // TODO: rewrite
        sender.sendMessage(new Component(ChatColor.DARK_PURPLE).translate("command.rotation.append.success", new Component(map.getInfo().name).color(ChatColor.GOLD)));
    }

    @Command(
        aliases = {"insert", "i"},
        desc = "Insert a map into the rotation at a certain place",
        usage = "[index] [map name]",
        min = 2,
        max = -1
    )
    @CommandPermissions("pgm.rotation.insert")
    public void insert(CommandContext args, CommandSender sender) throws CommandException {
        int index = args.getInteger(0);
        PGMMap map = CommandUtils.getMap(args.getJoinedStrings(1), sender);

        apply(new InsertTransformation(map, index - 1));
        // ChatColor.GOLD + map.getInfo().name + ChatColor.DARK_PURPLE + " inserted at index " + index
        sender.sendMessage(new Component(ChatColor.DARK_PURPLE)
            .translate("command.rotation.insert.success",
                new Component(map.getInfo().name).color(ChatColor.GOLD),
                String.valueOf(index)));
    }

    @Command(
        aliases = {"remove", "r"},
        desc = "Removes all instances of a given map from the rotation",
        usage = "[map name]",
        min = 1,
        max = -1
    )
    @CommandPermissions("pgm.rotation.remove")
    public void remove(CommandContext args, CommandSender sender) throws CommandException {
        PGMMap map = CommandUtils.getMap(args.getJoinedStrings(0), sender);

        apply(new RemoveAllTransformation(map));
        // TODO: rewrite
        sender.sendMessage(new Component(ChatColor.DARK_PURPLE).translate("command.rotation.remove.success", new Component(map.getInfo().name).color(ChatColor.GOLD)));
    }

    @Command(
        aliases = {"removeat", "ra"},
        desc = "Removes the map at a specific index from the rotation",
        usage = "[index]",
        min = 1,
        max = 1
    )
    @CommandPermissions("pgm.rotation.removeat")
    public void removeat(CommandContext args, CommandSender sender) throws CommandException {
        int index = args.getInteger(0);

        apply(new RemoveIndexTransformation(index - 1));
        // TODO: rewrite
        sender.sendMessage(new Component(ChatColor.DARK_PURPLE).translate("command.rotation.removeat.success", String.valueOf(index)));
    }

    private void apply(RotationTransformation transform) throws CommandException {
        RotationManager manager = matchManager.getRotationManager();
        RotationState rotation = transform.apply(manager.getRotation());
        manager.setRotation(rotation);
    }
}
