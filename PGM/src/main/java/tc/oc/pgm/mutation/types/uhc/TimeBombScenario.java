package tc.oc.pgm.mutation.types.uhc;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import tc.oc.commons.core.formatting.PeriodFormats;
import tc.oc.pgm.match.Match;
import tc.oc.pgm.match.MatchScope;
import tc.oc.pgm.mutation.Mutation;
import tc.oc.pgm.mutation.types.UHCMutation;

import java.time.Duration;
import java.util.List;

public class TimeBombScenario extends UHCMutation.Impl {

    final private static Duration EXPLODE_AFTER = Duration.ofSeconds(30);

    public TimeBombScenario(Match match, Mutation mutation) {
        super(match, mutation);
    }

    @Override
    public Object[] componentObjects() {
        Object[] list = new Object[1];
        list[0] = PeriodFormats.formatColonsLong(EXPLODE_AFTER).toPlainText();
        return list;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDeath(PlayerDeathEvent event) {
        List<ItemStack> drops = event.getDrops();
        Location killedLocation = event.getEntity().getLocation();

        // Account for golden heads
        killedLocation = killedLocation.add(0, 0, -1);

        killedLocation.getBlock().setType(Material.CHEST);
        Chest chest1 = (Chest) killedLocation.getBlock().getState();

        killedLocation = killedLocation.add(0, 0, -1);

        killedLocation.getBlock().setType(Material.CHEST);

        Chest chest2 = (Chest) killedLocation.getBlock().getState();

        for (ItemStack drop : drops) {
            if (chest1.getBlockInventory().getContents().length >= chest1.getBlockInventory().getSize()) {
                chest1.getBlockInventory().addItem(drop);
            } else {
                chest2.getBlockInventory().addItem(drop);
            }
        }
        drops.clear();

        if (EXPLODE_AFTER != null) {
            match().getScheduler(MatchScope.RUNNING).createDelayedTask(EXPLODE_AFTER, () -> {
                TNTPrimed tnt = (TNTPrimed) match().getWorld().spawnEntity(chest1.getLocation(), EntityType.PRIMED_TNT);
                tnt.setFuseTicks(0);
                tnt.setYield(5);

                //Blow up the stuff as well
                TNTPrimed tnt2 = (TNTPrimed) match().getWorld().spawnEntity(chest2.getLocation(), EntityType.PRIMED_TNT);
                tnt2.setFuseTicks(0);
                tnt2.setYield(5);
            });
        }
    }
}
