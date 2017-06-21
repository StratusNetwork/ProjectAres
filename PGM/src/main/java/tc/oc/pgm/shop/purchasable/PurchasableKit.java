package tc.oc.pgm.shop.purchasable;

import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Material;
import tc.oc.commons.bukkit.inventory.Slot;
import tc.oc.pgm.filters.Filter;
import tc.oc.pgm.kits.Kit;
import tc.oc.pgm.match.MatchPlayer;
import tc.oc.pgm.shop.currency.Currency;

import javax.annotation.Nullable;
import java.util.Set;

/**
 * Represents a {@link Kit} that can be purchased.
 */
public class PurchasableKit extends PurchasableImpl {
    final Kit kit;

    public PurchasableKit(BaseComponent name,
                          @Nullable BaseComponent description,
                          Material icon,
                          Slot slot,
                          double cost,
                          Currency currency,
                          Type type,
                          boolean incremental,
                          Filter purchaseFilter,
                          Kit kit) {
        super(name, description, icon, slot, cost, currency, type, incremental, purchaseFilter);
        this.kit = kit;
    }

    @Override
    public void reward(Set<MatchPlayer> players) {
        players.forEach(kit::apply);
    }
}
