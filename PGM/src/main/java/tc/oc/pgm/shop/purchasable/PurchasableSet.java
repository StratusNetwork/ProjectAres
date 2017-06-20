package tc.oc.pgm.shop.purchasable;

import tc.oc.pgm.features.FeatureDefinition;

import java.util.Set;

/**
 * Represents a group of {@link Purchasable}s which are loaded into {@link tc.oc.pgm.shop.Shop}s together.
 */
public interface PurchasableSet extends FeatureDefinition {
    Set<Purchasable> getItems();

    class Impl extends FeatureDefinition.Impl implements PurchasableSet {
        private final Set<Purchasable> items;

        public Impl(Set<Purchasable> items) {
            this.items = items;
        }

        public Set<Purchasable> getItems() {
            return items;
        }
    }
}
