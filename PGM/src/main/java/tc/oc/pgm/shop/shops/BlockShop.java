package tc.oc.pgm.shop.shops;

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
import tc.oc.pgm.shop.purchasable.PurchasableSet;

import java.util.Set;

/**
 * Represents a shop activated by right clicking on a {@link org.bukkit.block.Block}.
 */
@ListenerScope(MatchScope.RUNNING)
public class BlockShop extends Shop implements Listener {
    final Vector location;

    Match match;

    public BlockShop(PurchaseTracker tracker,
                     Set<PurchasableSet> items,
                     String title,
                     int rows,
                     Filter openFilter,
                     String openFailMessage,
                     boolean multiUse,
                     Vector location) {
        super(tracker, items, title, rows, openFilter, openFailMessage, multiUse);
        this.location = location;
    }

    @Override
    public void load(Match match) {
        this.match = match;
        match.registerEvents(this);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onClick(PlayerInteractEvent event) {
        if (!event.getClickedBlock().getLocation().toVector().equals(this.location))
            return;
        MatchPlayer player = match.getPlayer(event.getPlayer());
        if (player == null || player.isObserving())
            return;

        event.setCancelled(true); // Have to do this for containers
        this.handleOpen(player);
    }
}
