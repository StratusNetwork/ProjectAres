package tc.oc.pgm.mutation.types.uhc;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.ItemStack;
import tc.oc.pgm.match.Match;
import tc.oc.pgm.mutation.Mutation;
import tc.oc.pgm.mutation.types.UHCMutation;

public class MoblessScenario extends UHCMutation.Impl {

    public MoblessScenario(Match match, Mutation mutation) {
        super(match, mutation);
    }

    @Override
    public ItemStack[] items() {
        return new ItemStack[]{
                new ItemStack(Material.COOKED_BEEF, 30),
                new ItemStack(Material.FEATHER, 16),
                new ItemStack(Material.LEATHER, 3),
                new ItemStack(Material.STRING, 3)
        };
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDeath(CreatureSpawnEvent event) {
        event.setCancelled(true);
    }
}
