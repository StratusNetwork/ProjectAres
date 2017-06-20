package tc.oc.pgm.shop.purchasable;

import tc.oc.commons.bukkit.util.ItemCreator;
import tc.oc.pgm.filters.Filter;
import tc.oc.pgm.kits.Kit;
import tc.oc.pgm.match.MatchPlayer;
import tc.oc.pgm.shop.currency.Currency;

import java.util.Set;

public class PurchasableKit extends PurchasableImpl {
    final Kit kit;

    public PurchasableKit(ItemCreator icon,
                          int slot,
                          double cost,
                          Currency currency,
                          Type type,
                          boolean gradual,
                          Filter purchaseFilter,
                          Kit kit) {
        super(icon, slot, cost, currency, type, gradual, purchaseFilter);
        this.kit = kit;
    }

    @Override
    public void reward(Set<MatchPlayer> players) {
        players.forEach(kit::apply);
    }
}
