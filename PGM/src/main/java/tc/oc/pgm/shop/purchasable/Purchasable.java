package tc.oc.pgm.shop.purchasable;

import org.bukkit.material.MaterialData;
import tc.oc.pgm.match.MatchPlayer;
import tc.oc.pgm.shop.currency.Currency;

import java.util.Set;

public interface Purchasable {
    MaterialData getIcon();
    double getCost();
    Currency getCurrency();
    void reward(Set<MatchPlayer> players);
}

abstract class PurchasableImpl implements Purchasable {
    final MaterialData data;
    final double cost;
    final Currency currency;

    public PurchasableImpl(MaterialData data, double cost, Currency currency) {
        this.data = data;
        this.cost = cost;
        this.currency = currency;
    }

    @Override
    public MaterialData getIcon() {
        return data;
    }

    @Override
    public double getCost() {
        return cost;
    }

    @Override
    public Currency getCurrency() {
        return currency;
    }
}
