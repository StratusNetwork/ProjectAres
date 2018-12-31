package tc.oc.pgm.mutation.types.uhc;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import tc.oc.pgm.match.Match;
import tc.oc.pgm.match.MatchScope;
import tc.oc.pgm.mutation.Mutation;
import tc.oc.pgm.mutation.types.UHCMutation;
import tc.oc.pgm.spawns.events.ParticipantReleaseEvent;

import java.time.Duration;

public class ChickenScenario extends UHCMutation.Impl {

    public ChickenScenario(Match match, Mutation mutation) {
        super(match, mutation);
    }

    @Override
    public ItemStack[] items() {
        ItemStack godApple = new ItemStack(Material.GOLDEN_APPLE, 1, (short) 1);
        return new ItemStack[]{godApple};
    }

    @SuppressWarnings("deprecation")
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void processPlayerPartyChange(ParticipantReleaseEvent event) {
        if (event.getPlayer().isParticipating()) {
            match().getScheduler(MatchScope.RUNNING).createDelayedTask(Duration.ofSeconds(1), () -> {
                event.getPlayer().getBukkit().addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20 * 30, 3));
                event.getPlayer().getBukkit().addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 20 * 30, 1));
                damage(event.getPlayer().getBukkit(), 1, false);
            });
        }

    }
}
