package tc.oc.pgm.shop.currency;

import net.md_5.bungee.api.chat.BaseComponent;
import tc.oc.pgm.features.FeatureDefinition;
import tc.oc.pgm.features.FeatureInfo;
import tc.oc.pgm.match.MatchPlayer;
import tc.oc.pgm.shop.purchasable.Purchasable;

/**
 * Represents something that can be used to buy a {@link Purchasable}.
 */
@FeatureInfo(name = "currency")
public interface Currency extends FeatureDefinition {
    /**
     * @return the value of each piece of currency.
     */
    double getValue();

    /**
     * Check if a player has enough of the currency to purchase a {@link Purchasable}.
     * @param purchasable to compare price against
     * @param player to get the balance of
     */
    boolean canPurchase(Purchasable purchasable, MatchPlayer player);

    /**
     * Check if a player has any of the currency.
     * @param player to check
     */
    boolean hasCurrency(MatchPlayer player);

    /**
     * Subtract a certain amount of valued currency from a player.
     * @param player to subtract from
     * @param amount to subtract
     *
     * @ImplNote Implementors should take care to convert the amount back to the currency value before subtracting.
     */
    void subtract(MatchPlayer player, double amount);

    /**
     * Get a player's valued balance based on the amount of currency they have.
     * @param player to get balance for
     */
    double getBalance(MatchPlayer player);

    /**
     * @return name that should be used in UI (singular)
     */
    BaseComponent getSingularName();

    /**
     * @return name that should be used in UI (plural)
     */
    BaseComponent getPluralizedName();
}
