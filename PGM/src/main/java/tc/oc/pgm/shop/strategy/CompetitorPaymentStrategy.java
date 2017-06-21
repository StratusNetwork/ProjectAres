package tc.oc.pgm.shop.strategy;

import tc.oc.pgm.filters.Filter;
import tc.oc.pgm.match.Competitor;
import tc.oc.pgm.match.MatchPlayer;
import tc.oc.pgm.shop.purchasable.Purchasable;

/**
 * Strategy which allows all players in a {@link Competitor} to contribute.
 */
public class CompetitorPaymentStrategy extends PaymentStrategyImpl<Competitor> {
    final Competitor competitor;

    public CompetitorPaymentStrategy(Purchasable purchasable, Filter contributionFilter, Competitor competitor) {
        super(purchasable, contributionFilter);
        this.competitor = competitor;
    }

    @Override
    public boolean canContribute(MatchPlayer player) {
        return competitor.getPlayers().contains(player);
    }

    @Override
    public Competitor getOwner() {
        return this.competitor;
    }
}
