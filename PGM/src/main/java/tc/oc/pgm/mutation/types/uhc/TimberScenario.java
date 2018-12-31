package tc.oc.pgm.mutation.types.uhc;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import tc.oc.pgm.match.Match;
import tc.oc.pgm.mutation.Mutation;
import tc.oc.pgm.mutation.types.UHCMutation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TimberScenario extends UHCMutation.Impl {
    private List<Material> wood;
    private List<Material> leaves;

    public TimberScenario(Match match, Mutation mutation) {
        super(match, mutation);
        fillReplacements();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBrea(BlockBreakEvent event) {
        if (wood.contains(event.getBlock().getType())) {
            breakBlock(event.getBlock().getLocation(), event.getBlock().getLocation(), new HashSet<Location>());
        }
    }

    private void breakBlock(Location originalLocation, Location location, Set<Location> previousLocations) {
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    if (x == 0 && y == 0 && z == 0) {
                        continue;
                    }

                    Location newLocation = location.clone().add(x, y, z);
                    if (originalLocation.distance(newLocation) <= 5 && (wood.contains(newLocation.getBlock().getType()) || leaves.contains(newLocation.getBlock().getType())) && !previousLocations.contains(newLocation)) {
                        previousLocations.add(newLocation);
                        location.getBlock().breakNaturally();
                        breakBlock(originalLocation, newLocation, previousLocations);
                    }
                }
            }
        }
        location.getBlock().breakNaturally();
    }

    private void fillReplacements() {
        wood = new ArrayList<>();

        wood.add(Material.LOG);
        wood.add(Material.LOG_2);

        leaves = new ArrayList<>();

        leaves.add(Material.LEAVES);
        leaves.add(Material.LEAVES_2);
    }
}
