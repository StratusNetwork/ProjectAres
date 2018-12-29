package tc.oc.pgm.mutation.types.uhc;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.event.entity.PlayerDeathEvent;
import tc.oc.pgm.match.Match;
import tc.oc.pgm.mutation.Mutation;
import tc.oc.pgm.mutation.types.UHCMutation;

public class EnchantlessScenario extends UHCMutation.Impl {
    public EnchantlessScenario(Match match, Mutation mutation) {
        super(match, mutation);
    }

    // Disable drops for ores that are tiered above iron
    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();

        if (block.getType() == Material.REDSTONE_ORE) {
            event.getItemDrop().remove();
            block.getState().update();
            event.getBlock().setType(Material.AIR);
            player.sendMessage(message("mutation.type.barebones.disabled", ChatColor.RED));
        }

        if (block.getType() == Material.LAPIS_ORE) {
            event.getItemDrop().remove();
            block.getState().update();
            event.getBlock().setType(Material.AIR);
            player.sendMessage(message("mutation.type.barebones.disabled", ChatColor.RED));
        }

        if (block.getType() == Material.EMERALD_ORE) {
            event.getItemDrop().remove();
            block.getState().update();
            event.getBlock().setType(Material.AIR);
            player.sendMessage(message("mutation.type.barebones.disabled", ChatColor.RED));
        }

        if (block.getType() == Material.GOLD_ORE) {
            event.getItemDrop().remove();
            block.getState().update();
            event.getBlock().setType(Material.AIR);
            player.sendMessage(message("mutation.type.barebones.disabled", ChatColor.RED));
        }

        if (block.getType() == Material.DIAMOND_ORE) {
            event.getItemDrop().remove();
            block.getState().update();
            event.getBlock().setType(Material.AIR);
            player.sendMessage(message("mutation.type.barebones.disabled", ChatColor.RED));
        }
    }

    // Disable anvil crafting
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onItemCraft(CraftItemEvent event) {
        CraftingInventory inventory = event.getInventory();

        if (inventory.getResult() != null && inventory.getResult().getType().equals(Material.ANVIL)) {
            inventory.setResult(new ItemStack(Material.AIR));
        }
    }

    // Disable enchantment table crafting
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onItemCraft(CraftItemEvent event) {
        CraftingInventory inventory = event.getInventory();

        if (inventory.getResult() != null && inventory.getResult().getType().equals(Material.ENCHANTMENT_TABLE)) {
            inventory.setResult(new ItemStack(Material.AIR));
        }
    }

    // Drop certain items on player death
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDeath(PlayerDeathEvent event) {
        event.getDrops().add(new ItemStack (Material.DIAMOND, 1));
        event.getDrops().add(new ItemStack (Material.GOLDEN_APPLE, 1));
        event.getDrops().add(new ItemStack (Material.STRING, 2));
        event.getDrops().add(new ItemStack (Material.ARROW, 32));
    }

    public void disable() {
        super.disable();
    }

}
