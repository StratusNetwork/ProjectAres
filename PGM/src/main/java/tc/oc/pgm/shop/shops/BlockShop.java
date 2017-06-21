package tc.oc.pgm.shop.shops;

import com.google.inject.assistedinject.Assisted;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;
import tc.oc.pgm.events.ListenerScope;
import tc.oc.pgm.filters.Filter;
import tc.oc.pgm.match.Match;
import tc.oc.pgm.match.MatchPlayer;
import tc.oc.pgm.match.MatchScope;
import tc.oc.pgm.shop.PurchaseTracker;
import tc.oc.pgm.shop.Shop;
import tc.oc.pgm.shop.ShopInterface;
import tc.oc.pgm.shop.purchasable.PurchasableSet;

import javax.inject.Inject;
import java.util.Set;

/**
 * Represents a shop activated by right clicking on a {@link org.bukkit.block.Block}.
 */
@ListenerScope(MatchScope.RUNNING)
public class BlockShop extends Shop implements Listener {
    final Vector location;
    Match match;

    @Inject public BlockShop(@Assisted PurchaseTracker tracker,
                     ShopInterface.Factory interfaceFactory,
                     @Assisted Set<PurchasableSet> items,
                     String title,
                     @Assisted int rows,
                     @Assisted Filter openFilter,
                     @Assisted BaseComponent openFailMessage,
                     @Assisted boolean multiUse,
                     @Assisted Vector location) {
        super(tracker, interfaceFactory, items, title, rows, openFilter, openFailMessage, multiUse);
        this.location = location;
    }

    public interface Factory {
        BlockShop create(PurchaseTracker tracker,
                    Set<PurchasableSet> items,
                    String title,
                    int rows,
                    Filter openFilter,
                    BaseComponent openFailMessage,
                    boolean multiUse,
                    Vector location);
    }

    @Override
    public void load(Match match) {
        this.match = match;
        match.registerEvents(this);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onClick(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null || !event.getClickedBlock().getLocation().toVector().equals(this.location))
            return;
        MatchPlayer player = match.getPlayer(event.getPlayer());
        if (player == null || player.isObserving())
            return;

        event.setCancelled(true); // Have to do this for containers
        this.handleOpen(player);
    }
}
