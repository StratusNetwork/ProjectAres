package tc.oc.pgm.shop.strategy;

import tc.oc.pgm.filters.Filter;
import tc.oc.pgm.match.Competitor;
import tc.oc.pgm.match.MatchPlayer;
import tc.oc.pgm.shop.purchasable.Purchasable;

/**
 * Strategy which only allows the initiating {@link MatchPlayer} to contribute.
 */
public class PlayerPaymentStrategy extends PaymentStrategyImpl<MatchPlayer> {
    final MatchPlayer player;
    /**
     * If this strategy can be carried with the player when they switch {@link tc.oc.pgm.match.Party Parties}.
     */
    final boolean crossTeam;
    /**
     * If this strategy is persistent across player logins (during the same match).
     */
    final boolean persistent;

    // Data about player when the strategy was (re)started.
    final Competitor competitor;
    boolean hasLeft;

    public PlayerPaymentStrategy(Purchasable purchasable, Filter contributionFilter, MatchPlayer player, boolean crossTeam, boolean persistent) {
        super(purchasable, contributionFilter);
        this.player = player;
        this.crossTeam = crossTeam;
        this.persistent = persistent;
        this.competitor = player.getCompetitor();
    }

    public void setHasLeft(boolean hasLeft) {
        this.hasLeft = hasLeft;
    }

    @Override
    public boolean canContribute(MatchPlayer player) {
        if (!crossTeam)
            return competitor.getPlayers().contains(player);
        return persistent || !hasLeft;
    }

    @Override
    public MatchPlayer getOwner() {
        return this.player;
    }
}
