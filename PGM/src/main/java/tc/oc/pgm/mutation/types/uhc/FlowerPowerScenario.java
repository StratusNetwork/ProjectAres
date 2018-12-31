package tc.oc.pgm.mutation.types.uhc;

import com.google.common.collect.Range;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.ItemSpawnEvent;
import tc.oc.pgm.match.Match;
import tc.oc.pgm.mutation.Mutation;
import tc.oc.pgm.mutation.types.UHCMutation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class FlowerPowerScenario extends UHCMutation.Impl {
    private List<Material> flowers;
    private List<Material> bannedItems;

    private static final List<Material> materials = Collections.unmodifiableList(Arrays.asList(Material.values()));
    static Range<Integer> SIZE;

    public FlowerPowerScenario(Match match, Mutation mutation) {
        super(match, mutation);
        SIZE = Range.openClosed(0, materials.size() - 1);
        fillFlowers();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onItemSpawn(ItemSpawnEvent event) {
        if (flowers.contains(event.getEntity().getItemStack().getType())) {
            Material item = materials.get(entropy().randomInt(SIZE));
            while (bannedItems.contains(item)) {
                item = materials.get(entropy().randomInt(SIZE));
            }
            event.getEntity().getItemStack().setType(item);
        }
    }

    private void fillFlowers() {
        flowers = new ArrayList<>();
        flowers.add(Material.YELLOW_FLOWER);
        flowers.add(Material.RED_ROSE);

        bannedItems = new ArrayList<>();
        //too op
        bannedItems.add(Material.TOTEM);

        //too common
        bannedItems.add(Material.BLACK_GLAZED_TERRACOTTA);
        bannedItems.add(Material.BLUE_GLAZED_TERRACOTTA);
        bannedItems.add(Material.BROWN_GLAZED_TERRACOTTA);
        bannedItems.add(Material.CYAN_GLAZED_TERRACOTTA);
        bannedItems.add(Material.GRAY_GLAZED_TERRACOTTA);
        bannedItems.add(Material.GREEN_GLAZED_TERRACOTTA);
        bannedItems.add(Material.LIGHT_BLUE_GLAZED_TERRACOTTA);
        bannedItems.add(Material.LIME_GLAZED_TERRACOTTA);
        bannedItems.add(Material.MAGENTA_GLAZED_TERRACOTTA);
        bannedItems.add(Material.ORANGE_GLAZED_TERRACOTTA);
        bannedItems.add(Material.PINK_GLAZED_TERRACOTTA);
        bannedItems.add(Material.PURPLE_GLAZED_TERRACOTTA);
        bannedItems.add(Material.RED_GLAZED_TERRACOTTA);
        bannedItems.add(Material.SILVER_GLAZED_TERRACOTTA);
        bannedItems.add(Material.YELLOW_GLAZED_TERRACOTTA);
        bannedItems.add(Material.BLACK_SHULKER_BOX);
        bannedItems.add(Material.BLUE_SHULKER_BOX);
        bannedItems.add(Material.BROWN_SHULKER_BOX);
        bannedItems.add(Material.CYAN_SHULKER_BOX);
        bannedItems.add(Material.GRAY_SHULKER_BOX);
        bannedItems.add(Material.GREEN_SHULKER_BOX);
        bannedItems.add(Material.LIGHT_BLUE_SHULKER_BOX);
        bannedItems.add(Material.LIME_SHULKER_BOX);
        bannedItems.add(Material.MAGENTA_SHULKER_BOX);
        bannedItems.add(Material.ORANGE_SHULKER_BOX);
        bannedItems.add(Material.PINK_SHULKER_BOX);
        bannedItems.add(Material.PURPLE_SHULKER_BOX);
        bannedItems.add(Material.RED_SHULKER_BOX);
        bannedItems.add(Material.SILVER_SHULKER_BOX);
        bannedItems.add(Material.YELLOW_SHULKER_BOX);

        //scenario stuff
        bannedItems.add(Material.FISHING_ROD);
        bannedItems.add(Material.SHIELD);
    }
}
