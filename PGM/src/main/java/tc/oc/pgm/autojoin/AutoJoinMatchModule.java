package tc.oc.pgm.autojoin;

import java.util.HashSet;
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
        this.joiningPlayers = new HashSet<>();
        this.settingManagerProvider = settingManagerProvider;
        this.joinMatchModule = joinMatchModule;
    }

    @Override
    public void disable() {
        joiningPlayers.clear();
    }

    // Checks if the player is eligible when they join
    @EventHandler(priority = EventPriority.MONITOR)
    public void playerJoin(final PlayerChangePartyEvent event) {
        MatchPlayer player = event.getPlayer();

        // Ignore if the match has started
        if(match.hasStarted()) return;

        // Remove the player if the player is leaving
        if(event.getNewParty() == null) {
            joiningPlayers.remove(player);
            return;
        }

        //Ignore if player is going to participate in a match
        if(event.getNewParty().isParticipatingType()) return;

        /* Ignore if player is already known; case:
         * Player joined a participating team
         * Player left a participating team
         */
        // Check exists to only handle cases where Match has not started
        if(joiningPlayers.contains(player)) return;

        // Ignore if player explicitly chooses the legacy join feature
        if(!settingManagerProvider.getManager(player.getBukkit()).getValue(AutoJoinSetting.get(), Boolean.class, true)) return;

        joiningPlayers.add(player);
    }

    // Public accessor methods

    public boolean shouldAutoJoin(MatchPlayer player) {
        return joiningPlayers.contains(player);
    }

    // Checks if the player is in participating team when match starts
    public boolean shouldAlert(MatchPlayer player) {
        return shouldAutoJoin(player) && player.getParty().isParticipatingType();
    }

    // Player left clicks hat
    public void cancelAutojoin(MatchPlayer player) {
        joiningPlayers.remove(player);
    }

    public void requestJoin(MatchPlayer player) {
        joinMatchModule.requestJoin(player, JoinMethod.USER);
    }

    // StartCountdown needs this
    public void enterAllPlayers() {
        if(!joiningPlayers.isEmpty()) joiningPlayers.forEach(this::requestJoin);
    }

    public Stream<MatchPlayer> joiningPlayers() {
        return joiningPlayers.stream();
    }
}
