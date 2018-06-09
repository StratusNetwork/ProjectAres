package tc.oc.pgm.mutation.types.kit;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
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

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.WeakHashMap;

public class TeamChestMutation extends KitMutation {
    final static Material TOOL_TYPE = Material.ENDER_CHEST;
    final static int CHEST_SIZE = 27;

    final Map<Party, Inventory> teamChests = new WeakHashMap<>();

    final Optional<WoolMatchModule> optWools;

    public TeamChestMutation(Match match) {
        super(match, false);
        optWools = match().module(WoolMatchModule.class);
    }

    @Override
    public void enable() {
        super.enable();
        for (Party party : match().getParties()) {
            if (party.isParticipatingType()) {
                // Could the chest title be localized properly?
                teamChests.put(party, match().getServer().createInventory(null, CHEST_SIZE));
            }
        }
    }

    @Override
    public void disable() {
        teamChests.clear();
    }

    @Override
    public void kits(MatchPlayer player, List<Kit> kits) {
        super.kits(player, kits);
        kits.add(getKitForPlayer(player));
    }

    // Open shared inventory instead of placing the chest
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onChestUse(PlayerInteractEvent event) {
        Player bukkitPlayer = event.getPlayer();
        Optional<MatchPlayer> optPlayer = match().participant((Entity) bukkitPlayer);
        if (optPlayer.isPresent() ||
                !(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) ||
                event.getItem() == null ||
                event.getItem().getType() != TOOL_TYPE) {
            return;
        }

        Optional<Inventory> optTeamInventory = getTeamsInventory(bukkitPlayer);
        optTeamInventory.ifPresent(teamInventory -> {
            event.setCancelled(true);
            // If the item is in the off-hand slot, it wont get put back visually for the player without this.
            if(event.getHand() == EquipmentSlot.OFF_HAND) event.getActor().updateInventory();
            bukkitPlayer.openInventory(teamInventory);
        });
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = event.getActor();
        if (event.getCurrentItem() == null) return;

        // No putting blacklisted items (ender chest, possibly wool) into the chest
        Optional<Inventory> teamChest = getTeamsInventory(event.getActor());
        if (teamChest.map(teamInventory -> teamInventory.equals(event.getView().getTopInventory())).orElse(false) &&
                isBlacklistedItem(event.getCurrentItem())) {
            event.setCancelled(true);
            return;
        }

        // If normal right click, in their inventory, on the chest, then open shared inventory.
        getTeamsInventory(player).ifPresent(teamInventory -> {
            if (event.getInventory().getType() == InventoryType.CRAFTING &&
                    event.getCurrentItem().getType() == TOOL_TYPE &&
                    event.getAction() == InventoryAction.PICKUP_HALF) {
                event.setCancelled(true);
                // This resets their mouse position annoyingly, but without it items can get stuck in places.
                player.closeInventory();
                // Prevent visual inconsistencies, specifically off-hand slot
                if (event.getSlotType() == InventoryType.SlotType.QUICKBAR) {
                    player.updateInventory();
                }
                player.openInventory(teamInventory);
            }
        });
    }

    private boolean isBlacklistedItem(ItemStack item) {
        return item.getType() == TOOL_TYPE ||
                optWools.map(w -> w.isObjectiveWool(item)).orElse(false);
    }

    private Optional<Inventory> getTeamsInventory(Player bukkitPlayer) {
        return match().participant((Entity) bukkitPlayer)
                .map(matchPlayer -> teamChests.get(matchPlayer.getParty()));
    }

    private Kit getKitForPlayer(MatchPlayer player) {
        ItemStack stack = new ItemBuilder(item(TOOL_TYPE))
                .name(ChatColor.DARK_PURPLE + PGMTranslations.t("mutation.type.teamchest.item_name", player))
                .lore(ChatColor.DARK_AQUA + PGMTranslations.t("mutation.type.teamchest.item_lore", player))
                .get();

        ItemKit kit = new SlotItemKit(stack, Slot.Player.forIndex(17));
        return kit;
    }
}
