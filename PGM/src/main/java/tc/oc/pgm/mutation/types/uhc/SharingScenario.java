package tc.oc.pgm.mutation.types.uhc;

import com.google.common.collect.Range;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import tc.oc.pgm.match.Match;
import tc.oc.pgm.match.MatchPlayer;
import tc.oc.pgm.mutation.Mutation;
import tc.oc.pgm.mutation.MutationMatchModule;
import tc.oc.pgm.mutation.types.UHCMutation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class SharingScenario extends UHCMutation.Impl {

    final static Range<Integer> LAPIS_RANGE = Range.closed(4, 8);

    private Map<Material, Material> shared;

    public SharingScenario(Match match, Mutation mutation) {
        super(match, mutation);
        fillReplacements();
    }

    private void fillReplacements() {
        shared = new HashMap<>();
        shared.put(Material.DIAMOND_ORE, Material.DIAMOND);
        shared.put(Material.GOLD_ORE, Material.GOLD_INGOT);
        shared.put(Material.LAPIS_ORE, Material.INK_SACK);
        shared.put(Material.IRON_ORE, Material.IRON_INGOT);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onItemSpawn(BlockBreakEvent event) {
        if (!shared.containsKey(event.getBlock().getType())) {
            return;
        }
        if (match().getParticipatingPlayers().size() < 2) {
            return;
        }
        Collection<ItemStack> stack = event.getBlock().getDrops();

        MutationMatchModule mmm = match().getMatchModule(MutationMatchModule.class);
        boolean cutclean = mmm != null && mmm.scenariosActive().contains(Mutation.CUTCLEAN);
        List<ItemStack> drops = new ArrayList<>();
        for (ItemStack drop : stack) {
            if (cutclean && CutCleanScenario.replacements.containsKey(drop.getType())) {
                drop.setType(CutCleanScenario.replacements.get(drop.getType()));
            }
            drops.add(drop);
        }

        List<MatchPlayer> players = new ArrayList<>(match().getParticipatingPlayers());
        Random random = new Random();
        MatchPlayer player = players.get(random.nextInt(players.size()));
        while (player.equals(match().getPlayer(event.getPlayer()))) {
            player = players.get(random.nextInt(players.size()));
        }
        final MatchPlayer finalPlayer = player;

        drops.forEach(drop -> finalPlayer.getBukkit().getInventory().addItem(drop));

        if (event.getBlock().getType().equals(Material.DIAMOND_ORE)) {
            player.sendMessage(message("mutation.type.sharing.received", ChatColor.GREEN, "a diamond", event.getPlayer().getDisplayName(player.getBukkit())));
        }
    }
}
