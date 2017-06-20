package tc.oc.pgm.shop.currency;

import net.md_5.bungee.api.chat.BaseComponent;
import tc.oc.pgm.features.FeatureDefinition;
import tc.oc.pgm.features.FeatureInfo;
import tc.oc.pgm.match.MatchPlayer;
import tc.oc.pgm.shop.purchasable.Purchasable;

@FeatureInfo(name = "currency")
public interface Currency extends FeatureDefinition {
    double getValue();
    boolean canPurchase(Purchasable purchasable, MatchPlayer player);
    boolean hasCurrency(MatchPlayer player);
    void subtract(MatchPlayer player, double amount);
    double getBalance(MatchPlayer player);
    BaseComponent getSingularName();
    BaseComponent getPluralizedName();
}
