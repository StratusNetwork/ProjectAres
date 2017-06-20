package tc.oc.pgm.shop.purchasable;

import tc.oc.commons.bukkit.util.ItemCreator;
import tc.oc.pgm.filters.Filter;
import tc.oc.pgm.match.MatchPlayer;
import tc.oc.pgm.shop.currency.Currency;

import java.util.Set;

public interface Purchasable {
    ItemCreator getIcon();
    int getSlot();
    Type getType();
    double getCost();
    Currency getCurrency();
    boolean isIncremental();
    Filter getPurchaseFilter();
    void reward(Set<MatchPlayer> players);

    enum Type {
        GLOBAL, PARTY, INDIVIDUAL
    }
}

abstract class PurchasableImpl implements Purchasable {
    final ItemCreator icon;
    final int slot;
    final double cost;
    final Currency currency;
    final Type type;
    final boolean incremental;
    final Filter purchaseFilter;

    public PurchasableImpl(ItemCreator icon,
                           int slot,
                           double cost,
                           Currency currency,
                           Type type,
                           boolean incremental,
                           Filter purchaseFilter) {
        this.icon = icon;
        this.slot = slot;
        this.cost = cost;
        this.currency = currency;
        this.type = type;
        this.incremental = incremental;
        this.purchaseFilter = purchaseFilter;
    }

    @Override
    public ItemCreator getIcon() {
        return icon;
    }

    @Override
    public int getSlot() {
        return slot;
    }

    @Override
    public Filter getPurchaseFilter() {
        return this.purchaseFilter;
    }

    @Override
    public double getCost() {
        return cost;
    }

    @Override
    public Currency getCurrency() {
        return currency;
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public boolean isIncremental() {
        return this.type != Type.INDIVIDUAL || this.incremental;
    }
}
