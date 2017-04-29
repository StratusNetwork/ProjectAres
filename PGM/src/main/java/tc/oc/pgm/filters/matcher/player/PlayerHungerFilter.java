package tc.oc.pgm.filters.matcher.player;

import tc.oc.pgm.filters.query.IPlayerQuery;
import tc.oc.pgm.match.MatchPlayer;

public class PlayerHungerFilter extends SpawnedPlayerFilter {

    private int foodLevel;
    private String checkType;

    public PlayerHungerFilter(int hunger, String whichCase) {
        foodLevel = hunger;
        checkType = whichCase;
    }

    @Override
    protected boolean matches(IPlayerQuery query, MatchPlayer player) {
        switch (checkType) {
            case "above":
                return player.getBukkit().getFoodLevel() > foodLevel;
            case "below":
                return player.getBukkit().getFoodLevel() < foodLevel;
            default:
                return player.getBukkit().getFoodLevel() == foodLevel;
        }
    }
}
