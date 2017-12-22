package tc.oc.pgm.mutation.types.kit;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import tc.oc.commons.bukkit.chat.ComponentRenderContext;
import tc.oc.commons.bukkit.item.RenderedItemBuilder;
import tc.oc.commons.core.chat.Component;
import tc.oc.pgm.kits.FreeItemKit;
import tc.oc.pgm.kits.Kit;
import tc.oc.pgm.match.Match;
import tc.oc.pgm.match.MatchPlayer;
import tc.oc.pgm.mutation.Mutation;
import tc.oc.pgm.mutation.types.KitMutation;
import tc.oc.pgm.teams.Team;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

public class TeamChestMutation extends KitMutation {
    private String title = Mutation.TEAMCHEST.getName();

    private Map<Team, Inventory> teamInventories = new WeakHashMap<>();

    @Inject private static RenderedItemBuilder.Factory itemBuilders;
    @Inject private static ComponentRenderContext componentRenderContext;
    private ItemStack chestItem = new ItemStack(Material.CHEST);

    public TeamChestMutation(Match match) {
        super(match, false);
    }

    @Override
    public void kits(MatchPlayer player, List<Kit> kits) {
        super.kits(player, kits);
        PlayerInventory playerInventory = player.getInventory();
        if (!playerInventory.contains(createChestItem(player.getBukkit()))) {
            kits.add(new FreeItemKit(createChestItem(player.getBukkit())));
        }
    }

    @EventHandler
    public void blockPlace(PlayerInteractEvent e) {
        match().participant(e.getPlayer())
                .filter(player -> e.getItem().getItemMeta().hasDisplayName() && ChatColor.stripColor(e.getItem().getItemMeta().getDisplayName()).equalsIgnoreCase(title))
                .ifPresent(player -> {
                    e.setCancelled(true);
                    handleInventoryOpen(match().getPlayer(e.getPlayer()));
                });
    }

    private void handleInventoryOpen(MatchPlayer player) {
        if (!(player.canInteract() && player.getParty() instanceof Team)) {
            return;
        }
        if (!(teamInventories.containsKey((Team) player.getParty()))) {
            Inventory inventory = Bukkit.createInventory(player,
                    6 * 9,
                    componentRenderContext.renderLegacy(new Component(title, ChatColor.GOLD, ChatColor.BOLD), player.getBukkit()));
            teamInventories.put((Team) player.getParty(), inventory);
            player.getBukkit().openInventory(inventory);
        } else {
            player.getBukkit().openInventory(teamInventories.get((Team) player.getParty()));
        }
    }

    private ItemStack createChestItem(Player player) {
        return itemBuilders.create(player, chestItem)
                .name(new Component(title, ChatColor.GOLD, ChatColor.BOLD))
                .get();
    }
}