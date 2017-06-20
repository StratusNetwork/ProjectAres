package tc.oc.pgm.shop.currency;

import com.google.common.util.concurrent.AtomicDouble;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Material;
import tc.oc.pgm.features.FeatureDefinition;
import tc.oc.pgm.match.MatchPlayer;
import tc.oc.pgm.shop.purchasable.Purchasable;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * A currency based on a {@link Material}.
 */
public class MaterialCurrency extends FeatureDefinition.Impl implements Currency {

    private final Material material;
    private final BaseComponent nameSingle;
    private final BaseComponent namePlural;
    private final double value;

    public MaterialCurrency(Material material, BaseComponent nameSingle, BaseComponent namePlural, double value) {
        this.material = material;
        this.nameSingle = nameSingle;
        this.namePlural = namePlural;
        this.value = value;
    }

    @Override
    public BaseComponent getSingularName() {
        return nameSingle;
    }

    @Override
    public BaseComponent getPluralizedName() {
        return namePlural;
    }

    @Override
    public double getValue() {
        return this.value;
    }

    @Override
    public boolean canPurchase(Purchasable purchasable, MatchPlayer player) {
        return hasCurrency(player) && getBalance(player) >= purchasable.getCost();
    }

    @Override
    public boolean hasCurrency(MatchPlayer player) {
        return player.getInventory().contains(this.material);
    }

    @Override
    public double getBalance(MatchPlayer player) {
        AtomicDouble balance = new AtomicDouble();
        player.getInventory().forEach(i -> {
            if (i != null && i.getType() == this.material)
                balance.addAndGet(this.value * i.getAmount());
        });
        return balance.get();
    }

    @Override
    public void subtract(MatchPlayer player, double amount) {
        AtomicInteger toSubtract = new AtomicInteger((int) (amount / value));
        player.getInventory().forEach(i -> {
            if (toSubtract.get() <= 0)
                return;

            if (i != null && i.getType() == this.material) {
                if (i.getAmount() <= toSubtract.get()) {
                    toSubtract.set(toSubtract.get() - i.getAmount());
                    player.getInventory().remove(i);
                }
                else {
                    i.setAmount(i.getAmount() - toSubtract.get());
                    toSubtract.set(0);
                }
            }
        });
    }
}