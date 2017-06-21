package tc.oc.pgm.shop;

import tc.oc.api.docs.User;
import tc.oc.pgm.filters.Filter;
import tc.oc.pgm.match.Competitor;
import tc.oc.pgm.match.MatchPlayer;
import tc.oc.pgm.shop.purchasable.Purchasable;
import tc.oc.pgm.shop.strategy.PaymentStrategy;
import tc.oc.pgm.shop.strategy.PlayerPaymentStrategy;

import java.util.List;
import java.util.Optional;

/**
 * Class which is used to track all {@link PaymentStrategy PaymentStrategies}.
 */
public interface PurchaseTracker {
    /**
     * Start a strategy for an individual player.
     * @param player who initiated the strategy.
     * @param purchasable that the strategy is working towards.
     */
    default void startIndividualStrategy(MatchPlayer player, Purchasable purchasable) {
        startIndividualStrategy(player, purchasable, defaultFilter(purchasable));
    }

    /**
     * Start a strategy for a competitor.
     * @param competitor that the strategy is based upon.
     * @param player who initiated the strategy.
     * @param purchasable that the strategy is working towards.
     */
    default void startCompetitorStrategy(Competitor competitor, MatchPlayer player, Purchasable purchasable) {
        startCompetitorStrategy(competitor, player, purchasable, defaultFilter(purchasable));
    }

    /**
     * Start a strategy that anyone in the {@link tc.oc.pgm.match.Match} can contribute to.
     * @param player who initiated the strategy.
     * @param purchasable that the strategy is working towards.
     */
    default void startGlobalStrategy(Purchasable purchasable, MatchPlayer player) {
        startGlobalStrategy(purchasable, player, defaultFilter(purchasable));
    }

    /**
     * {@link #startIndividualStrategy(MatchPlayer, Purchasable)} with a contribution filter.
     */
    void startIndividualStrategy(MatchPlayer player, Purchasable purchasable, Filter filter);

    /**
     * {@link #startCompetitorStrategy(Competitor, MatchPlayer, Purchasable)} with a contribution filter.
     */
    void startCompetitorStrategy(Competitor competitor, MatchPlayer player, Purchasable purchasable, Filter filter);

    /**
     * {@link #startGlobalStrategy(Purchasable, MatchPlayer)} with a contribution filter.
     */
    void startGlobalStrategy(Purchasable purchasable, MatchPlayer player, Filter filter);

    /**
     * Called when a {@link MatchPlayer} contributes to a {@link PaymentStrategy} after all preconditions are met.
     * @param strategy that is being contributed to
     * @param player that is contributing
     */
    void contribute(PaymentStrategy strategy, MatchPlayer player);

    /**
     * Get an individual strategy that is ongoing for the supplied {@link Purchasable}.
     * @param player that owns the strategy.
     * @param purchasable that the strategy is working towards.
     * @return
     */
    Optional<PaymentStrategy> getOngoingStrategy(MatchPlayer player, Purchasable purchasable);

    /**
     * Get a competitor strategy that is ongoing for the supplied {@link Purchasable}.
     * @param competitor that owns the strategy.
     * @param purchasable that the strategy is working towards.
     * @return
     */
    Optional<PaymentStrategy> getOngoingStrategy(Competitor competitor, Purchasable purchasable);

    /**
     * Get a global strategy that is ongoing for the supplied {@link Purchasable}.
     * @param purchasable that the strategy is working towards.
     */
    Optional<PaymentStrategy> getOngoingStrategy(Purchasable purchasable);

    /**
     * Load all individual strategies from the API that have not been completed are persistent across matches.
     * @param user that should be loaded
     */
    List<PlayerPaymentStrategy> loadFromAPI(User user);

    /**
     * Helper method used to derive the default filter that should be used if none is supplied.
     * @param purchasable to define the filter for
     */
    Filter defaultFilter(Purchasable purchasable);

    /**
     * Save any ongoing strategies to the API, while removing all old strategies for the map.
     * @param user to update
     */
    void updateAPI(User user);
}
