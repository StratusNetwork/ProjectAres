package tc.oc.lobby.bukkit.gizmos.halloween;

import com.google.common.collect.ImmutableSet;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import tc.oc.commons.bukkit.item.ItemBuilder;

public class HeadlessHorseman {
    private final static ImmutableSet<Material> ARMOR_SET = new ImmutableSet.Builder<Material>()
        .add(Material.PUMPKIN)
        .add(Material.LEATHER_CHESTPLATE)
        .add(Material.LEATHER_LEGGINGS)
        .add(Material.LEATHER_BOOTS)
        .build();

    private final static EntityType HORSE_TYPE = EntityType.SKELETON_HORSE;
    private final Player viewer;
    private final HeadlessHorse headlessHorse;
    private static final Color ARMOR_COLOR = Color.fromRGB(84, 5, 40);

    public HeadlessHorseman(Player viewer) {
        this.viewer = viewer;
        this.headlessHorse = new HeadlessHorse(viewer);
        this.mutate();
    }

    private void mutate() {
        headlessHorse.spawn(viewer.getLocation(), (Class<AbstractHorse>) HORSE_TYPE.getEntityClass());
        ARMOR_SET.forEach(this::colorAndEquip);
        viewer.playSound(viewer.getLocation(), Sound.ENTITY_SKELETON_HORSE_DEATH, 1.5f, 1.5f);
    }

    /**
     * Currently gives armor pieces a deep purple color by RGB
     * Ugly method to equip pumpkin
     *
     * TODO: refactor methods
     */
    private void colorAndEquip(Material material) {
        ItemStack item = new ItemBuilder().material(material)
            .amount(1)
            .unbreakable(true)
            .shareable(false)
            .get();

        if (material != Material.PUMPKIN) {
            LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
            meta.setColor(ARMOR_COLOR);
            item.setItemMeta(meta);

            switch (item.getType()) {
                case LEATHER_CHESTPLATE:
                    viewer.getInventory().setChestplate(item);
                    break;
                case LEATHER_LEGGINGS:
                    viewer.getInventory().setLeggings(item);
                    break;
                case LEATHER_BOOTS:
                    viewer.getInventory().setBoots(item);
                    break;
            }
        } else {
            viewer.getInventory().setHelmet(item);
        }
    }

    public void restore() {
        headlessHorse.despawn();
        viewer.getInventory().armor().clear();
        viewer.playSound(viewer.getLocation(), Sound.ENTITY_ZOMBIE_VILLAGER_CURE, 1f, 1f);
    }

    public HeadlessHorse getHeadlessHorse() {
        return headlessHorse;
    }
}