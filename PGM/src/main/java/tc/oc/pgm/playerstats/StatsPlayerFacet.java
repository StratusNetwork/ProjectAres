package tc.oc.pgm.playerstats;

import java.text.DecimalFormat;
import javax.inject.Inject;

import me.anxuiz.settings.SettingManager;
import me.anxuiz.settings.bukkit.PlayerSettings;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TranslatableComponent;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import tc.oc.commons.bukkit.event.targeted.TargetedEventHandler;
import tc.oc.commons.core.chat.Component;
import tc.oc.commons.core.scheduler.Task;
import tc.oc.pgm.events.ListenerScope;
import tc.oc.pgm.events.MatchPlayerDeathEvent;
import tc.oc.pgm.match.*;
import tc.oc.pgm.match.inject.ForRunningMatch;

@ListenerScope(MatchScope.RUNNING)
public class StatsPlayerFacet extends MatchModule implements MatchPlayerFacet, Listener {

    private static final int DISPLAY_TICKS = 60;
    private static final DecimalFormat FORMAT = new DecimalFormat("0.00");

    private final MatchScheduler scheduler;
    private final StatsUserFacet statsUserFacet;
    private final MatchPlayer player;
    private final SettingManager settings;
    private Task task = null;

    @Inject
    private StatsPlayerFacet(@ForRunningMatch MatchScheduler scheduler, StatsUserFacet statsUserFacet, MatchPlayer player, SettingManager settings) {
        this.scheduler = scheduler;
        this.statsUserFacet = statsUserFacet;
        this.player = player;
        this.settings = settings;
    }

    @TargetedEventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerDeath(final MatchPlayerDeathEvent event) {
        if (event.isVictim(this.player) || event.isKiller(this.player)) update();
    }

    private ActionBarSettings.Options settings(MatchPlayer matchPlayer) {
        return PlayerSettings.getManager(matchPlayer.getBukkit()).getValue(ActionBarSettings.get(), ActionBarSettings.Options.class);
    }

    private void update() {
        if (!settings.getValue(StatSettings.STATS, Boolean.class)) return;
        if (task != null) {
            task.cancel();
        }
        task = scheduler.createRepeatingTask(1, 1, new Runnable() {
            int ticks = DISPLAY_TICKS;
            @Override
            public void run() {
                //Added to handle ActionBarSettings - NEVER, DEATH , ALL
                if (--ticks > 0 && settings(player).equals(ActionBarSettings.Options.DEATH) && settings(player).equals(ActionBarSettings.Options.ALL)) {
                    player.sendHotbarMessage(getMessage());
                } else if (--ticks <= 0){
                    delete();
                }
            }
        });
    }

    protected TranslatableComponent getMessage() {
        TranslatableComponent component = new TranslatableComponent("stats.hotbar",
                new Component(statsUserFacet.matchKills(), ChatColor.GREEN),
                new Component(statsUserFacet.lifeKills(), ChatColor.GREEN),
                new Component(statsUserFacet.deaths(), ChatColor.RED),
                new Component(FORMAT.format((double) statsUserFacet.matchKills() / Math.max(statsUserFacet.deaths(), 1)), ChatColor.AQUA));
        component.setBold(true);
        return component;
    }

    protected void delete() {
        if (task != null) {
            task.cancel();
            task = null;
        }
    }

    @Override
    public void disable() {
        delete();
    }

}
