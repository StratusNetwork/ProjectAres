package tc.oc.pgm.achievements.modules;

import tc.oc.pgm.match.MatchModule;

public abstract class AbstractAchievement extends MatchModule {
    @Override
    public boolean shouldLoad() {
        return false;
    }
}
