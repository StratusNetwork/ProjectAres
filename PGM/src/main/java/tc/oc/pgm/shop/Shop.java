package tc.oc.pgm.shop;

import net.md_5.bungee.api.chat.TranslatableComponent;
import tc.oc.pgm.features.FeatureDefinition;
import tc.oc.pgm.features.FeatureInfo;
import tc.oc.pgm.filters.Filter;
import tc.oc.pgm.shop.purchasable.Purchasable;

import javax.annotation.Nullable;
import java.util.Set;

@FeatureInfo(name = "shop", plural = "shops")
public abstract class Shop extends FeatureDefinition.Impl {

    public enum Type {
        BLOCK,
        ENTITY
    }

    @Inspect final Type type;
    @Inspect final Set<Purchasable> items;
    @Inspect final TranslatableComponent title;
    @Inspect final @Nullable Filter openFilter;
    @Inspect final @Nullable Filter globalPurchaseFilter;
    @Inspect final @Nullable boolean multiUse;
    @Inspect boolean inUse;

    public Shop(Type type, Set<Purchasable> items, TranslatableComponent title, Filter openFilter, Filter globalPurchaseFilter, boolean multiUse) {
        this.type = type;
        this.items = items;
        this.title = title;
        this.openFilter = openFilter;
        this.globalPurchaseFilter = globalPurchaseFilter;
        this.multiUse = multiUse;
    }

    public Type getType() {
        return type;
    }

    public Set<Purchasable> getItems() {
        return items;
    }

    public TranslatableComponent getTitle() {
        return title;
    }

    @Nullable
    public Filter getOpenFilter() {
        return openFilter;
    }

    @Nullable
    public Filter getGlobalPurchaseFilter() {
        return globalPurchaseFilter;
    }

    @Nullable
    public boolean isMultiUse() {
        return multiUse;
    }

    public boolean isInUse() {
        return inUse;
    }

    public void setInUse(boolean inUse) {
        this.inUse = inUse;
    }
}
