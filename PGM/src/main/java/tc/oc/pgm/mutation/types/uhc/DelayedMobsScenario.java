package tc.oc.pgm.mutation.types.uhc;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.CreatureSpawnEvent;
import tc.oc.commons.core.formatting.PeriodFormats;
import tc.oc.commons.core.util.Comparables;
import tc.oc.pgm.match.Match;
import tc.oc.pgm.mutation.Mutation;
import tc.oc.pgm.mutation.types.UHCMutation;

import java.time.Duration;

public class DelayedMobsScenario extends UHCMutation.Impl {

    final private static Duration DELAY = Duration.ofMinutes(10);

    public DelayedMobsScenario(Match match, Mutation mutation) {
        super(match, mutation);
    }

    @Override
    public Object[] componentObjects() {
        return new Object[]{PeriodFormats.formatColonsLong(DELAY).toPlainText()};
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDeath(CreatureSpawnEvent event) {
        if (Comparables.lessOrEqual(match().runningTime(), DELAY)) {
            event.setCancelled(true);
        }
    }
}
