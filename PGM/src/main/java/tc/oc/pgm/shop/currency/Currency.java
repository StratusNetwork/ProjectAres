package tc.oc.pgm.shop.currency;

import tc.oc.pgm.features.FeatureDefinition;
import tc.oc.pgm.features.FeatureInfo;
import tc.oc.pgm.match.MatchPlayer;
import tc.oc.pgm.shop.purchasable.Purchasable;

@FeatureInfo(name = "currency")
public interface Currency extends FeatureDefinition {
    double getValue();
    boolean canPurchase(Purchasable purchasable, MatchPlayer player);
    void subtract(MatchPlayer player, double amount);
    double getBalance(MatchPlayer player);
}
