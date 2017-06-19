package tc.oc.pgm.shop.purchasable;

import org.bukkit.material.MaterialData;
import tc.oc.pgm.kits.Kit;
import tc.oc.pgm.match.MatchPlayer;
import tc.oc.pgm.shop.currency.Currency;

import java.util.Set;

public class PurchasableKit extends PurchasableImpl {
    final Kit kit;

    public PurchasableKit(MaterialData data, double cost, Currency currency, Kit kit) {
        super(data, cost, currency);
        this.kit = kit;
    }

    @Override
    public void reward(Set<MatchPlayer> players) {
        players.forEach(kit::apply);
    }
}
