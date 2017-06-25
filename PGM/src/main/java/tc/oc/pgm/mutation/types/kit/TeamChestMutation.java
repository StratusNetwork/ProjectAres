package tc.oc.pgm.mutation.types.kit;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TranslatableComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import tc.oc.commons.bukkit.chat.ComponentRenderContext;
import tc.oc.commons.bukkit.item.RenderedItemBuilder;
import tc.oc.commons.core.chat.Component;
import tc.oc.pgm.ffa.FreeForAllMatchModule;
import tc.oc.pgm.kits.FreeItemKit;
import tc.oc.pgm.kits.Kit;
import tc.oc.pgm.match.Match;
import tc.oc.pgm.match.MatchPlayer;
import tc.oc.pgm.mutation.types.KitMutation;
import tc.oc.pgm.teams.Team;

import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

public class TeamChestMutation extends KitMutation {

    private Map<Team, Inventory> teamInventories = new WeakHashMap<>();

    private ItemStack chest = new ItemStack(Material.CHEST);
    private final RenderedItemBuilder.Factory itemBuilder;
    private final ComponentRenderContext renderer;
    private BaseComponent title = new TranslatableComponent("teamChestMutation.title");

    public TeamChestMutation(Match match, RenderedItemBuilder.Factory itemBuilder, ComponentRenderContext renderer) {
        super(match, false);
        this.itemBuilder = itemBuilder;
        this.renderer = renderer;
    }

    @Override
    public void kits(MatchPlayer player, List<Kit> kits) {
        super.kits(player, kits);
        PlayerInventory playerInventory = player.getInventory();
        if (!(playerInventory.contains(chestAsItem(player.getBukkit())))) {
            kits.add(new FreeItemKit(chestAsItem(player.getBukkit())));
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void blockPlace(PlayerInteractEvent e) {
        match().participant(e.getPlayer())
                .filter(player -> e.getItem().isSimilar(chestAsItem(e.getPlayer())))
                .ifPresent(player -> {
                    e.setCancelled(true);
                    handleInventoryOpen(match().getPlayer(e.getPlayer()));
                });
    }

    @EventHandler(ignoreCancelled = true)
    public void inventoryClickEvent(InventoryClickEvent e) {
        ItemStack clicked = e.getCurrentItem();

        //Accounts for shift click
        if (!(e.getWhoClicked() != null && match().getPlayer(e.getWhoClicked()).canInteract())
                || e.getInventory() == e.getWhoClicked().getInventory()
                || (clicked != null && clicked.getType() == Material.WOOL)) {
            return;
        }
        e.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void inventoryDragEvent(InventoryDragEvent e) {
        ItemStack dragged = e.getOldCursor();

        //Accounts for dragging
        if (!(e.getWhoClicked() != null
                && e.getWhoClicked() instanceof Player
                && dragged.getType() == Material.WOOL
                && match().getPlayer(e.getWhoClicked()).getParty() instanceof Team
                && e.getInventory() == teamInventories.get(match().getPlayer(e.getWhoClicked()).getParty()))) {
            return;
        }
        //reverses the changes in player inventory
        int inventorySize = e.getInventory().getSize();
        for (int i : e.getRawSlots()) {
            if (i < inventorySize) {
                e.setCancelled(true);
            }
        }
    }

    private ItemStack chestAsItem(Player viewer) {
        return this.itemBuilder.create(viewer, chest)
                .flags(ItemFlag.values())
                .name(new Component(title, ChatColor.GOLD))
                .get();
    }

    private Inventory createWindow(Player viewer) {
        return Bukkit.createInventory(null,
                6 * 9,
                renderer.renderLegacy(new Component(title, ChatColor.AQUA, ChatColor.BOLD), viewer)
        );
    }

    private void handleInventoryOpen(MatchPlayer matchPlayer) {
        if (!(matchPlayer.canInteract() && matchPlayer.getParty() instanceof Team) || match().hasMatchModule(FreeForAllMatchModule.class)) {
            return;
        }
        if (!(teamInventories.containsKey(matchPlayer.getParty()))) {
            Inventory inventory = this.createWindow(matchPlayer.getBukkit());
            teamInventories.put((Team) matchPlayer.getParty(), inventory);
            matchPlayer.getBukkit().openInventory(inventory);
        } else {
            matchPlayer.getBukkit().openInventory(teamInventories.get(matchPlayer.getParty()));
        }
    }
}
