package tc.oc.pgm.mutation.types.kit;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import tc.oc.commons.bukkit.inventory.Slot;
import tc.oc.commons.bukkit.item.ItemBuilder;
import tc.oc.pgm.PGMTranslations;
import tc.oc.pgm.kits.ItemKit;
import tc.oc.pgm.kits.Kit;
import tc.oc.pgm.kits.SlotItemKit;
import tc.oc.pgm.match.Match;
import tc.oc.pgm.match.MatchPlayer;
import tc.oc.pgm.match.Party;
import tc.oc.pgm.mutation.types.KitMutation;
import tc.oc.pgm.wool.WoolMatchModule;

import java.util.*;

public class TeamChestMutation extends KitMutation {
    final static int SLOT_ID = 17; // Top right
    final static Material TOOL_TYPE = Material.ENDER_CHEST;
    final static String ITEM_NAME_KEY = "mutation.type.teamchest.item_name";
    final static String ITEM_LORE_KEY = "mutation.type.teamchest.item_lore";
    final static int CHEST_SIZE = 27;

    final Map<Party, Inventory> teamChests = new WeakHashMap<>();

    final Optional<WoolMatchModule> oWmm;

    public TeamChestMutation(Match match) {
        super(match, false);
        oWmm = Optional.ofNullable(match().getMatchModule(WoolMatchModule.class));
        for (Party party : match().getParties()) {
            if (party.isParticipatingType()) {
                // Could the chest title be localized properly?
                teamChests.put(party, match().getServer().createInventory(null, CHEST_SIZE));
            }
        }
    }

    @Override
    public void kits(MatchPlayer player, List<Kit> kits) {
        super.kits(player, kits);
        kits.add(getKitForPlayer(player));
    }

    // Open shared inventory instead of placing the chest
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChestUse(PlayerInteractEvent event) {
        if (event.getItem() == null) return;

        if (!(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)) return;
        if (event.getItem().getType() != TOOL_TYPE) return;

        Player bukkitPlayer = event.getPlayer();
        if (bukkitPlayer == null) return;
        Optional<Inventory> oTeamInventory = getTeamsInventory(bukkitPlayer);

        oTeamInventory.ifPresent(teamInventory -> {
            event.setCancelled(true);
            // If the item is in the off-hand slot, it wont get put back visually for the player without this.
            if(event.getHand() == EquipmentSlot.OFF_HAND) event.getActor().updateInventory();
            bukkitPlayer.openInventory(teamInventory);

        });
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getCurrentItem() == null) return;

        // No putting evil items (ender chest, possibly wool) into the chest
        Optional<Inventory> teamChest = getTeamsInventory(event.getActor());
        if (teamChest.filter(teamInventory -> {
            return teamInventory.equals(event.getView().getTopInventory());
        }).isPresent() &&
                isItemEvil(event.getCurrentItem())) {
            event.setCancelled(true);
            return;
        }

        // If normal right click, in their inventory, on the chest, then open shared inventory.
        getTeamsInventory(event.getActor()).ifPresent(teamInventory -> {
            if (event.getInventory().getType() == InventoryType.CRAFTING &&
                    event.getCurrentItem().getType() == TOOL_TYPE &&
                    event.getAction() == InventoryAction.PICKUP_HALF) {
                event.setCancelled(true);
                // This resets their mouse position annoyingly, but without it items can get stuck in places.
                event.getActor().closeInventory();
                // Prevent visual inconsistencies, specifically off-hand slot
                if (event.getSlotType() == InventoryType.SlotType.QUICKBAR) {
                    event.getActor().updateInventory();
                }
                event.getActor().openInventory(teamInventory);
            }
        });
    }

    private boolean isItemEvil(ItemStack item) {
        if(item.getType() == TOOL_TYPE) return true;
        if(oWmm.filter(wmm -> wmm.isObjectiveWool(item)).isPresent()) return true;

        return false;
    }

    private Optional<Inventory> getTeamsInventory(Player bukkitPlayer) {
        MatchPlayer player = match().getPlayer(bukkitPlayer);
        Party team = player.getParty();

        if (!team.isParticipating()) return Optional.empty();

        return Optional.of(teamChests.get(team));
    }

    private Kit getKitForPlayer(MatchPlayer player) {
        ItemStack stack = new ItemBuilder(item(TOOL_TYPE))
                .name(ChatColor.DARK_PURPLE + PGMTranslations.t(ITEM_NAME_KEY, player))
                .lore(ChatColor.DARK_AQUA + PGMTranslations.t(ITEM_LORE_KEY, player))
                .get();

        ItemKit kit = new SlotItemKit(stack, Slot.Player.forIndex(SLOT_ID));
        return kit;
    }
}
