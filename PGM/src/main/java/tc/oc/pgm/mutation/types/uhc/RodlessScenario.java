package tc.oc.pgm.mutation.types.uhc;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import tc.oc.pgm.match.Match;
import tc.oc.pgm.mutation.Mutation;
import tc.oc.pgm.mutation.types.UHCMutation;

public class RodlessScenario extends UHCMutation.Impl {
    public RodlessScenario(Match match, Mutation mutation) {
        super(match, mutation);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onItemCraft(CraftItemEvent event) {
        CraftingInventory inventory = event.getInventory();

        if (inventory.getResult() != null && inventory.getResult().getType().equals(Material.FISHING_ROD)) {
            inventory.setResult(new ItemStack(Material.AIR));
        }
    }
}
