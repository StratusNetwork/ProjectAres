package tc.oc.pgm.mutation.types.other;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import tc.oc.commons.bukkit.item.ItemUtils;
import tc.oc.pgm.killreward.KillReward;
import tc.oc.pgm.kits.FreeItemKit;
import tc.oc.pgm.kits.ItemKitApplicator;
import tc.oc.pgm.match.Match;
import tc.oc.pgm.match.MatchPlayer;
import tc.oc.pgm.match.MatchState;
import tc.oc.pgm.mutation.types.KitMutation;
import tc.oc.pgm.rage.RageMatchModule;

import java.util.ArrayList;
import java.util.List;
import java.util.WeakHashMap;

public class RageMutation extends KitMutation {

    RageMatchModule rage;

    final WeakHashMap<MatchPlayer, List<ItemStack>> itemsRemoved;

    public RageMutation(Match match) {
        super(match, true);
        addRageItems();
        itemsRemoved = new WeakHashMap<>();
        this.rage = match.module(RageMatchModule.class).orElse(new RageMatchModule(match));
        this.rewards.add(new KillReward(new FreeItemKit(item(Material.ARROW))));
    }

    private void addRageItems() {
        ItemStack rageSword = item(Material.IRON_SWORD);
        rageSword.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 10);
        this.kits.add(new FreeItemKit(rageSword));
        ItemStack rageBow = item(Material.BOW);
        rageBow.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 10);
        this.kits.add(new FreeItemKit(rageBow));
        this.kits.add(new FreeItemKit(item(Material.ARROW)));
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerDamage(EntityDamageByEntityEvent event) {
        rage.handlePlayerDamage(event);
    }

    @Override
    public void apply(MatchPlayer player) {
        // Find the player's first weapon and bow
        ItemStack[] playerInventory = player.getInventory().getStorageContents();
        List<ItemStack> itemsSaved = new ArrayList<ItemStack>();
        boolean foundWeapon = false;
        boolean foundBow = false;
        int numArrows = 0;
        for(ItemStack item : playerInventory) {
            if(!foundWeapon && item != null && ItemUtils.isWeapon(item)) {
                itemsSaved.add(item);
                foundWeapon = true;
                player.getInventory().remove(item);
            } else if(!foundBow && item != null && Material.BOW.equals(item.getType())) {
                itemsSaved.add(item);
                foundBow = true;
                player.getInventory().remove(item);
            }
            if (item != null && Material.ARROW.equals(item.getType())) {
                numArrows += item.getAmount();
                player.getInventory().remove(item);
            }
        }
        if (numArrows > 0) {
            itemsSaved.add(item(Material.ARROW, numArrows));
        }
        if (!itemsSaved.isEmpty()) {
            itemsRemoved.put(player, itemsSaved);
        }
        super.apply(player);
    }

    @Override
    public void remove(MatchPlayer player) {
        super.remove(player);
        // Restore the player's old items
        List<ItemStack> tools = itemsRemoved.remove(player);
        if (tools != null && !player.isObserving() && !player.getMatch().inState(MatchState.Finished)) {
            for (ItemStack tool : tools) {
                ItemKitApplicator applicator = new ItemKitApplicator();
                applicator.add(tool);
                applicator.apply(player);
            }
            tools.clear();
        }
    }

    @Override
    public void disable() {
        super.disable();
        rage = null;
    }

}
