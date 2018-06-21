package tc.oc.pgm.achievements;

import org.bukkit.event.Listener;
import tc.oc.pgm.events.ListenerScope;
import tc.oc.pgm.match.MatchScope;
import tc.oc.pgm.match.inject.MatchScoped;

import javax.inject.Singleton;

@Singleton @MatchScoped
@ListenerScope(MatchScope.RUNNING)
public class AchievementsManager implements Listener{

}
