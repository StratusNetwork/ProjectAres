package tc.oc.pgm.shop;

import com.google.api.client.util.Maps;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.bukkit.event.EventHandler;
import tc.oc.api.docs.User;
import tc.oc.pgm.events.PlayerChangePartyEvent;
import tc.oc.pgm.events.PlayerJoinMatchEvent;
import tc.oc.pgm.filters.Filter;
import tc.oc.pgm.match.MatchPlayer;
import tc.oc.pgm.match.Party;
import tc.oc.pgm.shop.purchasable.Purchasable;
import tc.oc.pgm.shop.strategy.GlobalPaymentStrategy;
import tc.oc.pgm.shop.strategy.PartyPaymentStrategy;
import tc.oc.pgm.shop.strategy.PaymentStrategy;
import tc.oc.pgm.shop.strategy.PlayerPaymentStrategy;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

class SimplePurchaseTracker implements PurchaseTracker {
    private final Map<MatchPlayer, PaymentStrategy> individualStrategies;
    private final Map<Party, PaymentStrategy> partyStrategies;
    private final Set<PaymentStrategy> globalStrategies;

    private final boolean crossParty;
    private final boolean persistent;

    public SimplePurchaseTracker(boolean crossParty, boolean persistent) {
        this.individualStrategies = Maps.newHashMap();
        this.partyStrategies = Maps.newHashMap();
        this.globalStrategies = Sets.newHashSet();
        this.crossParty = crossParty;
        this.persistent = persistent;
    }

    @Override
    public void startIndividualStrategy(MatchPlayer player, Purchasable purchasable) {
        startIndividualStrategy(player, purchasable, purchasable.getPurchaseFilter());
    }

    @Override
    public void startPartyStrategy(Party party, MatchPlayer player, Purchasable purchasable) {
        startPartyStrategy(party, player, purchasable, purchasable.getPurchaseFilter());
    }

    @Override
    public void startGlobalStrategy(Purchasable purchasable, MatchPlayer player) {
        startGlobalStrategy(purchasable, player, purchasable.getPurchaseFilter());
    }

    @Override
    public void startIndividualStrategy(MatchPlayer player, Purchasable purchasable, Filter filter) {
        PlayerPaymentStrategy strategy = new PlayerPaymentStrategy(purchasable, filter, player, crossParty, persistent);
        individualStrategies.put(player, strategy);
        contribute(strategy, player);
    }

    @Override
    public void startPartyStrategy(Party party, MatchPlayer player, Purchasable purchasable, Filter filter) {
        PartyPaymentStrategy strategy = new PartyPaymentStrategy(purchasable, filter, party);
        partyStrategies.put(party, strategy);
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
    public Optional<PaymentStrategy> getOngoingStrategy(Party party, Purchasable purchasable) {
        if (!(party instanceof Party))
            return Optional.empty();
        return partyStrategies.values().stream().filter(e ->
                e.getOwner().equals(party) && e.getPurchasable().equals(purchasable)
        ).findFirst();
    }

    @Override
    public Optional<PaymentStrategy> getOngoingStrategy(Purchasable purchasable) {
        return partyStrategies.values().stream().filter(e ->
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
                case PARTY:
                    partyStrategies.remove(strategy.getOwner(), strategy);
                    strategy.getPurchasable().reward(((Party)strategy.getOwner()).getPlayers());
                    break;
            }
        }
    }

    @EventHandler
    public void onChange(PlayerChangePartyEvent event) {
        if (event.isParticipating())
            loadFromAPI(event.getPlayer().getDocument());
        else
            updateAPI(event.getPlayer().getDocument());
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
