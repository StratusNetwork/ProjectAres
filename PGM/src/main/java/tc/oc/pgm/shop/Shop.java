package tc.oc.pgm.shop;

import net.md_5.bungee.api.chat.TranslatableComponent;
import tc.oc.pgm.features.FeatureDefinition;
import tc.oc.pgm.features.FeatureInfo;
import tc.oc.pgm.filters.Filter;
import tc.oc.pgm.match.MatchPlayer;
import tc.oc.pgm.shop.purchasable.PurchasableSet;

import javax.annotation.Nullable;
import java.util.Set;

/**
 * A container for {@link PurchasableSet}s where they can be purchased.
 */
@FeatureInfo(name = "shop", plural = "shops")
public abstract class Shop extends FeatureDefinition.Impl {

    @Inspect final PurchaseTracker tracker;
    @Inspect final Set<PurchasableSet> items;
    @Inspect final String title;
    @Inspect final int rows;
    @Inspect final @Nullable Filter openFilter;
    @Inspect final @Nullable String openFailMessage;
    @Inspect final @Nullable boolean multiUse;
    @Inspect boolean inUse;

    final ShopInterface.Factory interfaceFactory;

    public Shop(PurchaseTracker tracker,
                ShopInterface.Factory interfaceFactory,
                Set<PurchasableSet> items,
                String title,
                int rows,
                Filter openFilter,
                String openFailMessage,
                boolean multiUse) {
        this.tracker = tracker;
        this.interfaceFactory = interfaceFactory;
        this.items = items;
        this.title = title;
        this.rows = rows;
        this.openFilter = openFilter;
        this.openFailMessage = openFailMessage;
        this.multiUse = multiUse;
    }

    public Set<PurchasableSet> getItems() {
        return items;
    }

    public String getTitle() {
        return title;
    }

    @Nullable
    public Filter getOpenFilter() {
        return openFilter;
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

    protected void handleOpen(MatchPlayer player) {
        if (isInUse() && !isMultiUse())
            player.sendWarning(new TranslatableComponent("shop.inUse"));

        if (getOpenFilter() != null) {
            if (getOpenFilter().denies(player)) {
                if (openFailMessage != null)
                    player.sendWarning(openFailMessage);
                return;
            }
        }

        setInUse(true);
        interfaceFactory.create(player, this).openWindow(player.getBukkit());
    }
}
