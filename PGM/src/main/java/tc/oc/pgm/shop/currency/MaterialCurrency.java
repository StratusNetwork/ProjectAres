package tc.oc.pgm.shop.currency;

import com.google.common.util.concurrent.AtomicDouble;
import org.bukkit.Material;
import tc.oc.pgm.match.MatchPlayer;
import tc.oc.pgm.shop.purchasable.Purchasable;

import java.util.concurrent.atomic.AtomicInteger;

public class MaterialCurrency implements Currency {

    private final Material material;
    private final double value;

    public MaterialCurrency(Material material, double value) {
        this.material = material;
        this.value = value;
    }

    @Override
    public double getValue() {
        return this.value;
    }

    @Override
    public boolean canPurchase(Purchasable purchasable, MatchPlayer player) {
        if (!player.getInventory().contains(this.material))
            return false;
        else
            return getValue() >= purchasable.getCost();
    }

    @Override
    public double getBalance(MatchPlayer player) {
        AtomicDouble balance = new AtomicDouble();
        player.getInventory().forEach(i -> {
            if (i.getType() == this.material)
                balance.addAndGet(this.value);
        });
        return balance.get();
    }

    @Override
    public void subtract(MatchPlayer player, double amount) {
        AtomicInteger toSubtract = new AtomicInteger((int) (amount / value));
        player.getInventory().forEach(i -> {
            if (toSubtract.get() <= 0)
                return;

            if (i.getType() == this.material) {
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