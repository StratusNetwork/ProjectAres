package tc.oc.pgm.shop.strategy;

import tc.oc.pgm.filters.Filter;
import tc.oc.pgm.match.MatchPlayer;
import tc.oc.pgm.shop.purchasable.Purchasable;

public class GlobalPaymentStrategy extends PaymentStrategyImpl {
    public GlobalPaymentStrategy(Purchasable purchasable, Filter contributionFilter) {
        super(purchasable, contributionFilter);
    }

    @Override
    public Object getOwner() {
        return null;
    }

    @Override
    public boolean canContribute(MatchPlayer player) {
        return true;
    }
}
