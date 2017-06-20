package tc.oc.pgm.shop.strategy;

import tc.oc.pgm.filters.Filter;
import tc.oc.pgm.match.MatchPlayer;
import tc.oc.pgm.shop.purchasable.Purchasable;

public interface PaymentStrategy<T> {
    boolean canContribute(MatchPlayer player);
    double getRemainingOwed();
    double getContribution();
    T getOwner();
    Purchasable getPurchasable();
    boolean isComplete();
    void contribute(MatchPlayer player);
    Filter getContributionFilter();
}

abstract class PaymentStrategyImpl<T> implements PaymentStrategy {
    final Purchasable purchasable;
    final Filter contributionFilter;

    double contribution = 0;

    public PaymentStrategyImpl(Purchasable purchasable, Filter contributionFilter) {
        this.purchasable = purchasable;
        this.contributionFilter = contributionFilter;
    }

    @Override
    public double getRemainingOwed() {
        return Math.max(0, this.purchasable.getCost() - this.contribution);
    }

    @Override
    public double getContribution() {
        return this.contribution;
    }

    @Override
    public boolean isComplete() {
        return this.contribution >= this.purchasable.getCost();
    }

    @Override
    public Filter getContributionFilter() {
        return this.contributionFilter;
    }

    @Override
    public void contribute(MatchPlayer player) {
        double bal = purchasable.getCurrency().getBalance(player);
        double before = contribution;
        double toSubtract = bal;
        contribution += bal;
        if (isComplete()) { // Check if payed more than worth
            if (before + bal > purchasable.getCost())
                toSubtract = purchasable.getCost();
        }
        purchasable.getCurrency().subtract(player, toSubtract);
    }

    @Override
    public Purchasable getPurchasable() {
        return this.purchasable;
    }
}
