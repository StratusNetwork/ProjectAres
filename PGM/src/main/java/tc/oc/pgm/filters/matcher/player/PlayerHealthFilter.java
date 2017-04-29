package tc.oc.pgm.filters.matcher.player;

import tc.oc.pgm.filters.query.IPlayerQuery;
import tc.oc.pgm.match.MatchPlayer;

public class PlayerHealthFilter extends SpawnedPlayerFilter {

    private int healthAmount;
    private String checkType;

    public PlayerHealthFilter(int health, String whichCase) {
        healthAmount = health;
        checkType = whichCase;
    }

    @Override
    protected boolean matches(IPlayerQuery query, MatchPlayer player) {
        switch (checkType) {
            case "above":
                return Math.ceil(player.getBukkit().getHealth()) > healthAmount;
            case "below":
                return Math.ceil(player.getBukkit().getHealth()) < healthAmount;
            default:
                return Math.ceil(player.getBukkit().getHealth()) == healthAmount;
        }
    }
}
