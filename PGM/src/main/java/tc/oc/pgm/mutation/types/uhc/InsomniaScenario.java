package tc.oc.pgm.mutation.types.uhc;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import tc.oc.commons.core.scheduler.Task;
import tc.oc.pgm.events.MatchBeginEvent;
import tc.oc.pgm.match.Match;
import tc.oc.pgm.match.MatchPlayer;
import tc.oc.pgm.match.MatchScope;
import tc.oc.pgm.mutation.Mutation;
import tc.oc.pgm.mutation.types.UHCMutation;

import java.time.Duration;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class InsomniaScenario extends UHCMutation.Impl {
    final private static int DAMAGE = 6; //3 hearts

    public InsomniaScenario(Match match, Mutation mutation) {
        super(match, mutation);
    }

    private Set<UUID> sleptPlayers;
    private boolean night;
    private Task warningTask;
    private Task changeNightTask;

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBedInteract(PlayerInteractEvent event) {
        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK) &&
                event.getClickedBlock().getType().equals(Material.BED_BLOCK) &&
                night &&
                !sleptPlayers.contains(event.getPlayer().getUniqueId())) {
            if (sleptPlayers.size() == 0) {
                damage(event.getPlayer(), event.getPlayer().getHealth() - DAMAGE);
                MatchPlayer player = match().getPlayer(event.getPlayer());
                if (player != null) {
                    player.sendMessage(message("mutation.type.paranoia.damage"));
                }
            }
            sleptPlayers.add(event.getPlayer().getUniqueId());
            if (sleptPlayers.size() == match().getParticipatingPlayers().size() - 1) {
                for (MatchPlayer player : match().getParticipatingPlayers()) {
                    if (sleptPlayers.contains(player.getUniqueId())) {
                        continue;
                    }
                    damage(player.getBukkit(), 0);
                    match().sendMessage(message("mutation.type.paranoia.last"));
                }
            } else {
                match().sendMessage(message("mutation.type.paranoia.safe"));
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onMatchBegin(MatchBeginEvent event) {
        night = false;
        sleptPlayers = new HashSet<>();
        match().getWorld().setGameRuleValue("doDaylightCycle", "true");
        match().getWorld().setTime(0);
        warningTask = match().getScheduler(MatchScope.RUNNING).createRepeatingTask(Duration.ofMinutes(19), Duration.ofMinutes(10), () -> {
            match().sendMessage(message("mutation.type.paranoia.warning"));
        });
        changeNightTask = match().getScheduler(MatchScope.RUNNING).createRepeatingTask(Duration.ofMinutes(10), Duration.ofMinutes(10), () -> {
            night = !night;
            if (!night) {
                for (MatchPlayer player : match().getParticipatingPlayers()) {
                    if (!sleptPlayers.contains(player.getUniqueId())) {
                        damage(player.getBukkit(), 0);
                    }
                }
                sleptPlayers.clear();
            }
            match().sendMessage(message("mutation.type.paranoia." + (night ? "night" : "morning")));
        });
    }

    @Override
    public void disable() {
        sleptPlayers.clear();

        if (warningTask != null) {
            warningTask.cancel();
        }

        if (changeNightTask != null) {
            changeNightTask.cancel();
        }
        super.disable();
    }

}
