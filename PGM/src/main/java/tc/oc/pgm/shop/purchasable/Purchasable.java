package tc.oc.pgm.shop.purchasable;

import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Material;
import tc.oc.commons.bukkit.inventory.Slot;
import tc.oc.pgm.filters.Filter;
import tc.oc.pgm.match.MatchPlayer;
import tc.oc.pgm.shop.currency.Currency;

import javax.annotation.Nullable;
import java.util.Set;

/**
 * Represents anything that can be purchased with {@link Currency}.
 */
public interface Purchasable {
    /**
     * Name of the item.
     */
    BaseComponent getName();

    /**
     * Description of the item.
     */
    @Nullable
    BaseComponent getDescription();

    /**
     * Get the icon that should be used for UI display.
     */
    Material getIcon();

    /**
     * Get the slot that this item should be placed in inside of a {@link tc.oc.pgm.shop.ShopInterface}.
     */
    Slot getSlot();

    /**
     * Get the type of {@link tc.oc.pgm.shop.strategy.PaymentStrategy} that should be used for this item.
     */
    Type getType();

    /**
     * Get the cost of this item after currency conversion.
     */
    double getCost();

    /**
     * Get the currency that can be used to purchase this item.
     */
    Currency getCurrency();

    /**
     * Determine if {@link Currency} can be put toward this item in stages.
     */
    boolean isIncremental();

    /**
     * Get the filter that will be checked against the {@link MatchPlayer} before they are allowed to purchase this item.
     */
    Filter getPurchaseFilter();

    /**
     * Reward a set of players after a successful purchase.
     */
    void reward(Set<MatchPlayer> players);

    enum Type {
        /**
         * Anyone in the {@link tc.oc.pgm.match.Match} can contribute to the eventual purchase of this item.
         */
        GLOBAL,
        /**
         * Anyone in the {@link tc.oc.pgm.match.Party} can contribute to the eventual purchase of this item.
         */
        COMPETITOR,
        /**
         * Only the initiating {@link MatchPlayer} can contribute to the purchase of this item.
         */
        INDIVIDUAL
    }
}

abstract class PurchasableImpl implements Purchasable {
    final BaseComponent name;
    @Nullable final BaseComponent description;
    final Material icon;
    final Slot slot;
    final double cost;
    final Currency currency;
    final Type type;
    final boolean incremental;
    final Filter purchaseFilter;

    public PurchasableImpl(BaseComponent name,
                           @Nullable BaseComponent description,
                           Material icon,
                           Slot slot,
                           double cost,
                           Currency currency,
                           Type type,
                           boolean incremental,
                           Filter purchaseFilter) {
        this.name = name;
        this.description = description;
        this.icon = icon;
        this.slot = slot;
        this.cost = cost;
        this.currency = currency;
        this.type = type;
        this.incremental = incremental;
        this.purchaseFilter = purchaseFilter;
    }

    @Override
    public BaseComponent getName() {
        return name;
    }

    @Override
    @Nullable
    public BaseComponent getDescription() {
        return description;
    }

    @Override
    public Material getIcon() {
        return icon;
    }

    @Override
    public Slot getSlot() {
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
