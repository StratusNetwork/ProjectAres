package tc.oc.pgm.shop.purchasable;

import tc.oc.pgm.features.FeatureDefinition;

import java.util.Set;

public class PurchasableSet implements FeatureDefinition {
    private final Set<Purchasable> items;

    public PurchasableSet(Set<Purchasable> items) {
        this.items = items;
    }
}
