package tc.oc.pgm.shop;

import org.bukkit.event.Listener;
import tc.oc.api.docs.User;
import tc.oc.pgm.filters.Filter;
import tc.oc.pgm.match.MatchPlayer;
import tc.oc.pgm.match.Party;
import tc.oc.pgm.shop.purchasable.Purchasable;
import tc.oc.pgm.shop.strategy.PaymentStrategy;
import tc.oc.pgm.shop.strategy.PlayerPaymentStrategy;

import java.util.List;
import java.util.Optional;

public interface PurchaseTracker extends Listener {
    void startIndividualStrategy(MatchPlayer player, Purchasable purchasable);
    void startPartyStrategy(Party party, MatchPlayer player, Purchasable purchasable);
    void startGlobalStrategy(Purchasable purchasable, MatchPlayer player);
    void startIndividualStrategy(MatchPlayer player, Purchasable purchasable, Filter filter);
    void startPartyStrategy(Party party, MatchPlayer player, Purchasable purchasable, Filter filter);
    void startGlobalStrategy(Purchasable purchasable, MatchPlayer player, Filter filter);

    void contribute(PaymentStrategy strategy, MatchPlayer player);

    Optional<PaymentStrategy> getOngoingStrategy(MatchPlayer player, Purchasable purchasable);
    Optional<PaymentStrategy> getOngoingStrategy(Party party, Purchasable purchasable);
    Optional<PaymentStrategy> getOngoingStrategy(Purchasable purchasable);

    List<PlayerPaymentStrategy> loadFromAPI(User user);
    void updateAPI(User user);
}
