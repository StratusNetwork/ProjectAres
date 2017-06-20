package tc.oc.pgm.shop.strategy;

import tc.oc.pgm.filters.Filter;
import tc.oc.pgm.match.MatchPlayer;
import tc.oc.pgm.shop.purchasable.Purchasable;

/**
 * Any track a T can take to purchasing a {@link Purchasable}.
 * @param <T> owner of the strategy.
 */
public interface PaymentStrategy<T> {
    /**
     * Check if a player is allowed to contribute to the strategy.
     * @ImplNote the {@link #getContributionFilter()} is queried before this is called.
     * @param player to check
     */
    boolean canContribute(MatchPlayer player);

    /**
     * Get remaining {@link tc.oc.pgm.shop.currency.Currency} owed before the {@link Purchasable} can be purchased.
     */
    double getRemainingOwed();

    /**
     * Get the amount of {@link tc.oc.pgm.shop.currency.Currency} already contributed toward the purchase.
     */
    double getContribution();

    /**
     * Get the owner of the strategy.
     */
    T getOwner();

    /**
     * Get the {@link Purchasable} that this strategy is attempting to purchase.
     */
    Purchasable getPurchasable();

    /**
     * Check if this strategy has completed a successful purchase.
     */
    boolean isComplete();

    /**
     * Contribute all of a {@link MatchPlayer} remaining {@link tc.oc.pgm.shop.currency.Currency} toward the purchase goal.
     */
    void contribute(MatchPlayer player);

    /**
     * Get the filter queried before a {@link MatchPlayer} is allowed to contribute.
     */
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
