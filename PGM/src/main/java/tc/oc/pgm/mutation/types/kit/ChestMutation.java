package tc.oc.pgm.mutation.types.kit;

import com.google.common.collect.Maps;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.PlayerInventory;
import tc.oc.pgm.kits.FreeItemKit;
import tc.oc.pgm.kits.Kit;
import tc.oc.pgm.match.Match;
import tc.oc.pgm.match.MatchPlayer;
import tc.oc.pgm.mutation.types.KitMutation;
import tc.oc.pgm.teams.Team;

import java.util.List;
import java.util.Map;


public class ChestMutation extends KitMutation {

    private final static FreeItemKit TEAM_CHEST = new FreeItemKit(item(Material.ENDER_CHEST));

    private final static Map<Team, Inventory> TEAM_INVENTORY_MAP = Maps.newHashMap();


    public ChestMutation(Match match) {
        super(match, true, TEAM_CHEST);
    }


    @Override
    public void kits(MatchPlayer player, List<Kit> kits) {
        super.kits(player, kits);
        PlayerInventory playerInventory = player.getInventory();
        if (!playerInventory.contains(Material.ENDER_CHEST)) this.kits.add(TEAM_CHEST);

    }

    @Override
    public void remove(MatchPlayer player) {
        player.getInventory().remove(Material.ENDER_CHEST);
        super.remove(player);
    }

    @SuppressWarnings("SuspiciousMethodCalls")
    @EventHandler(priority = EventPriority.MONITOR)
    public void blockPlaceEvent(BlockPlaceEvent e) {
        if (e.getBlock().getType() != Material.ENDER_CHEST) {
            return;
        }
        e.setCancelled(true);
        MatchPlayer player = (MatchPlayer) e.getPlayer();
        if (TEAM_INVENTORY_MAP.containsKey(player.getParty())) {
            e.getPlayer().openInventory(TEAM_INVENTORY_MAP.get(player.getParty()));
        } else {
            Inventory inventory = Bukkit.createInventory(null, InventoryType.ENDER_CHEST);
            TEAM_INVENTORY_MAP.put((Team) player.getParty(), inventory);
            e.getPlayer().openInventory(inventory);
        }
    }
}