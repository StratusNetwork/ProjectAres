package tc.oc.pgm.mutation.types.uhc;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.event.entity.PlayerDeathEvent;
import tc.oc.pgm.match.Match;
import tc.oc.pgm.mutation.Mutation;
import tc.oc.pgm.mutation.types.UHCMutation;

public class BarebonesScenario extends UHCMutation.Impl {
    public BarebonesScenario(Match match, Mutation mutation) {
        super(match, mutation);
    }

    // Disable drops for ores that are tiered above iron
    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        Player player = event.getActor();
        Material type = block.getType();

        if (type == Material.REDSTONE_ORE || type == Material.LAPIS_ORE || type == Material.EMERALD_ORE || type == Material.DIAMOND_ORE || type == Material.GOLD_ORE) {
            event.setDropItems(false);
            player.sendMessage(message("mutation.type.barebones.disabledOre", ChatColor.RED));
        }
    }

    // Disable anvil interaction
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInteract(PlayerInteractEvent event) {
        if(event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Block block = event.getClickedBlock();
            if(block.getType() == Material.ANVIL) {
                event.setCancelled(true);
                event.getPlayer().sendMessage(message("mutation.type.barebones.disabled", ChatColor.RED));
            }
        }
    }

    // Disable enchantment table crafting
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onItemCraft(CraftItemEvent event) {
        CraftingInventory inventory = event.getInventory();

        if (inventory.getResult() != null && inventory.getResult().getType().equals(Material.ENCHANTMENT_TABLE)) {
            inventory.setResult(new ItemStack(Material.AIR));
            event.getActor().sendMessage(message("mutation.type.barebones.disabled", ChatColor.RED));
        }
    }

    // Drop certain items on player death
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDeath(PlayerDeathEvent event) {
        event.getDrops().add(new ItemStack (Material.DIAMOND, 1));
        event.getDrops().add(new ItemStack (Material.GOLDEN_APPLE, 1));
        event.getDrops().add(new ItemStack (Material.STRING, 2));
        event.getDrops().add(new ItemStack(Material.ARROW, 32));
    }

}
