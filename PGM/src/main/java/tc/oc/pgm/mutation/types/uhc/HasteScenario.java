package tc.oc.pgm.mutation.types.uhc;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;
import tc.oc.pgm.match.Match;
import tc.oc.pgm.mutation.Mutation;
import tc.oc.pgm.mutation.types.UHCMutation;

import java.util.HashSet;
import java.util.Set;

public class HasteScenario extends UHCMutation.Impl {
    public HasteScenario(Match match, Mutation mutation) {
        super(match, mutation);
        addTools();
    }

    private Set<Material> tools = new HashSet<>();

    private void addTools() {
        tools.add(Material.DIAMOND_AXE);
        tools.add(Material.GOLD_AXE);
        tools.add(Material.IRON_AXE);
        tools.add(Material.STONE_AXE);
        tools.add(Material.WOOD_AXE);

        tools.add(Material.DIAMOND_PICKAXE);
        tools.add(Material.GOLD_PICKAXE);
        tools.add(Material.IRON_PICKAXE);
        tools.add(Material.STONE_PICKAXE);
        tools.add(Material.WOOD_PICKAXE);

        tools.add(Material.DIAMOND_SPADE);
        tools.add(Material.GOLD_SPADE);
        tools.add(Material.IRON_SPADE);
        tools.add(Material.STONE_SPADE);
        tools.add(Material.WOOD_SPADE);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onItemCraft(CraftItemEvent event) {
        if (event.getInventory().getResult() == null) {
            return;
        }

        if (!tools.contains(event.getInventory().getResult().getType())) {
            return;
        }

        ItemStack result = event.getInventory().getResult();
        result.addUnsafeEnchantment(Enchantment.DIG_SPEED, 3);
        result.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
    }
}
