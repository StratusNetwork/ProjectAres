package tc.oc.pgm.shop.strategy;

import tc.oc.pgm.filters.Filter;
import tc.oc.pgm.match.Competitor;
import tc.oc.pgm.match.MatchPlayer;
import tc.oc.pgm.shop.purchasable.Purchasable;

public class PlayerPaymentStrategy extends PaymentStrategy.Impl {
    final MatchPlayer player;
    final boolean crossTeam;
    final boolean persistent;

    // Data about player when the strategy was started.
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
}
