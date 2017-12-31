package tc.oc.pgm.autojoin;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Stream;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import tc.oc.commons.bukkit.settings.SettingManagerProvider;
import tc.oc.pgm.events.ListenerScope;
import tc.oc.pgm.events.PlayerChangePartyEvent;
import tc.oc.pgm.join.JoinMatchModule;
import tc.oc.pgm.join.JoinMethod;
import tc.oc.pgm.match.MatchModule;
import tc.oc.pgm.match.MatchPlayer;
import tc.oc.pgm.match.MatchScope;

import javax.inject.Inject;

/**
 * New join feature that allows players to join without interfacing with GUI
 * with an AutoJoinSetting that allows players to use the legacy join feature
 * instead.
 */
@ListenerScope(MatchScope.LOADED)
public class AutoJoinMatchModule extends MatchModule implements Listener {
    private Set<MatchPlayer> joiningPlayers;
    private final SettingManagerProvider settingManagerProvider;
    private final JoinMatchModule joinMatchModule;

    @Inject public AutoJoinMatchModule(SettingManagerProvider settingManagerProvider, JoinMatchModule joinMatchModule) {
        this.joiningPlayers = new LinkedHashSet<>();
        this.settingManagerProvider = settingManagerProvider;
        this.joinMatchModule = joinMatchModule;
    }

    @Override
    public void disable() {
        joiningPlayers.clear();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void playerJoin(final PlayerChangePartyEvent event) {
        MatchPlayer player = event.getPlayer();

        if(match.hasStarted()) return;

        if(event.getNewParty() == null) {
            joiningPlayers.remove(player);
            return;
        }

        if(event.getNewParty().isParticipatingType()) return;

        if(joiningPlayers.contains(player)) return;

        if(!settingManagerProvider.getManager(player.getBukkit()).getValue(AutoJoinSetting.get(), Boolean.class, true)) return;

        joiningPlayers.add(player);
    }

    public boolean shouldAutoJoin(MatchPlayer player) {
        return joiningPlayers.contains(player);
    }

    public void cancelAutojoin(MatchPlayer player) {
        joiningPlayers.remove(player);
    }

    public void requestJoin(MatchPlayer player) {
        joinMatchModule.requestJoin(player, JoinMethod.USER);
    }

    public void enterAllPlayers() {
        if(!joiningPlayers.isEmpty()) joiningPlayers.forEach(this::requestJoin);
    }

    public Stream<MatchPlayer> joiningPlayers() {
        return joiningPlayers.stream();
    }
}
