package tc.oc.pgm.shop.currency;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import tc.oc.pgm.features.FeatureDefinition;
import tc.oc.pgm.match.MatchPlayer;
import tc.oc.pgm.shop.purchasable.Purchasable;

/**
 * A currency based on a {@link org.bukkit.entity.Player}'s XP level.
 */
public class ExperienceCurrency extends FeatureDefinition.Impl implements Currency {

    @Override
    public double getValue() {
        return 1;
    }

    @Override
    public BaseComponent getSingularName() {
        return new TextComponent("XP Level");
    }

    @Override
    public BaseComponent getPluralizedName() {
        return new TextComponent("XP Levels");
    }

    @Override
    public boolean canPurchase(Purchasable purchasable, MatchPlayer player) {
        return hasCurrency(player) && player.getBukkit().getLevel() >= purchasable.getCost();
    }

    @Override
    public boolean hasCurrency(MatchPlayer player) {
        return player.getBukkit().getLevel() >= 1;
    }

    @Override
    public void subtract(MatchPlayer player, double amount) {
        player.getBukkit().setLevel(Math.max(0, player.getBukkit().getLevel() - (int) Math.round(amount)));
    }

    @Override
    public double getBalance(MatchPlayer player) {
        return player.getBukkit().getLevel();
    }
}