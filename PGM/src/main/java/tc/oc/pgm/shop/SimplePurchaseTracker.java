package tc.oc.pgm.shop;

import com.google.api.client.util.Maps;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import tc.oc.api.docs.User;
import tc.oc.pgm.filters.Filter;
import tc.oc.pgm.match.Competitor;
import tc.oc.pgm.match.MatchPlayer;
import tc.oc.pgm.shop.purchasable.Purchasable;
import tc.oc.pgm.shop.strategy.CompetitorPaymentStrategy;
import tc.oc.pgm.shop.strategy.GlobalPaymentStrategy;
import tc.oc.pgm.shop.strategy.PaymentStrategy;
import tc.oc.pgm.shop.strategy.PlayerPaymentStrategy;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Simple implementation of the {@link PurchaseTracker}.
 */
class SimplePurchaseTracker implements PurchaseTracker {
    private final Map<MatchPlayer, PaymentStrategy> individualStrategies;
    private final Map<Competitor, PaymentStrategy> competitorStrategies;
    private final Set<PaymentStrategy> globalStrategies;

    private final boolean crossParty;
    private final boolean persistent;

    public SimplePurchaseTracker(boolean crossParty, boolean persistent) {
        this.individualStrategies = Maps.newHashMap();
        this.competitorStrategies = Maps.newHashMap();
        this.globalStrategies = Sets.newHashSet();
        this.crossParty = crossParty;
        this.persistent = persistent;
    }

    @Override
    public Filter defaultFilter(Purchasable purchasable) {
        return purchasable.getPurchaseFilter();
    }

    @Override
    public void startIndividualStrategy(MatchPlayer player, Purchasable purchasable, Filter filter) {
        PlayerPaymentStrategy strategy = new PlayerPaymentStrategy(purchasable, filter, player, crossParty, persistent);
        individualStrategies.put(player, strategy);
        contribute(strategy, player);
    }

    @Override
    public void startCompetitorStrategy(Competitor competitor, MatchPlayer player, Purchasable purchasable, Filter filter) {
        CompetitorPaymentStrategy strategy = new CompetitorPaymentStrategy(purchasable, filter, competitor);
        competitorStrategies.put(competitor, strategy);
        contribute(strategy, player);
    }

    @Override
    public void startGlobalStrategy(Purchasable purchasable, MatchPlayer player, Filter filter) {
        GlobalPaymentStrategy strategy = new GlobalPaymentStrategy(purchasable, filter);
        globalStrategies.add(strategy);
        contribute(strategy, player);
    }

    @Override
    public Optional<PaymentStrategy> getOngoingStrategy(MatchPlayer player, Purchasable purchasable) {
        return individualStrategies.values().stream().filter(e ->
            e.getOwner().equals(player) && e.getPurchasable().equals(purchasable)
        ).findFirst();
    }

    @Override
    public Optional<PaymentStrategy> getOngoingStrategy(Competitor competitor, Purchasable purchasable) {
        return competitorStrategies.values().stream().filter(e ->
                e.getOwner().equals(competitor) && e.getPurchasable().equals(purchasable)
        ).findFirst();
    }

    @Override
    public Optional<PaymentStrategy> getOngoingStrategy(Purchasable purchasable) {
        return competitorStrategies.values().stream().filter(e ->
                e.getPurchasable().equals(purchasable)
        ).findFirst();
    }

    @Override
    public void contribute(PaymentStrategy strategy, MatchPlayer player) {
        strategy.contribute(player);
        if (strategy.isComplete()) {
            switch (strategy.getPurchasable().getType()) {
                case GLOBAL:
                    globalStrategies.remove(strategy);
                    strategy.getPurchasable().reward(player.getMatch().getPlayers());
                    break;
                case INDIVIDUAL:
                    individualStrategies.remove(player, strategy);
                    strategy.getPurchasable().reward(Sets.newHashSet(player));
                    break;
                case COMPETITOR:
                    competitorStrategies.remove(strategy.getOwner(), strategy);
                    strategy.getPurchasable().reward(((Competitor)strategy.getOwner()).getPlayers());
                    break;
            }
        }
    }

    @Override
    public List<PlayerPaymentStrategy> loadFromAPI(User player) {
        // TODO
        return Lists.newArrayList();
    }

    @Override
    public void updateAPI(User user) {
        // TODO
    }
}
