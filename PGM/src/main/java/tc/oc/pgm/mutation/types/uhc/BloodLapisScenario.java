package tc.oc.pgm.mutation.types.uhc;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import tc.oc.pgm.match.Match;
import tc.oc.pgm.mutation.Mutation;
import tc.oc.pgm.mutation.types.UHCMutation;

public class BloodLapisScenario extends UHCMutation.Impl {

    public BloodLapisScenario(Match match, Mutation mutation) {
        super(match, mutation);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onItemSpawn(BlockBreakEvent event) {
        if (event.getBlock().getType().equals(Material.LAPIS_ORE)) {
            damage(event.getPlayer(), event.getPlayer().getHealth() - 1);
        }
    }

    public void disable() {
        super.disable();
    }

}
