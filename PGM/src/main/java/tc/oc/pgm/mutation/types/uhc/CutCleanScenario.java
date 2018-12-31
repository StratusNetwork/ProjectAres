package tc.oc.pgm.mutation.types.uhc;

import com.google.common.collect.Range;
import org.bukkit.Material;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Cow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.inventory.ItemStack;
import tc.oc.commons.bukkit.util.BlockUtils;
import tc.oc.pgm.match.Match;
import tc.oc.pgm.mutation.Mutation;
import tc.oc.pgm.mutation.types.UHCMutation;

import java.util.HashMap;
import java.util.Map;

public class CutCleanScenario extends UHCMutation.Impl {

    final static Range<Integer> FEATHER_RANGE = Range.closed(1, 3);
    final static Range<Integer> LEATHER_RANGE = Range.closed(1, 2);
    final static Range<Integer> BEEF_RANGE = Range.closed(1, 3);

    protected static Map<Material, Material> replacements;
    private Map<Material, Integer> xp;


    public CutCleanScenario(Match match, Mutation mutation) {
        super(match, mutation);
        fillReplacements();
    }

    private void fillReplacements() {
        replacements = new HashMap<>();
        replacements.put(Material.RAW_BEEF, Material.COOKED_BEEF);
        replacements.put(Material.RAW_CHICKEN, Material.COOKED_CHICKEN);
        replacements.put(Material.RAW_FISH, Material.COOKED_FISH);
        replacements.put(Material.PORK, Material.GRILLED_PORK);
        replacements.put(Material.MUTTON, Material.COOKED_MUTTON);
        replacements.put(Material.RABBIT, Material.COOKED_RABBIT);
        replacements.put(Material.IRON_ORE, Material.IRON_INGOT);
        replacements.put(Material.GOLD_ORE, Material.GOLD_INGOT);

        xp = new HashMap<>();
        xp.put(Material.IRON_ORE, 1);
        xp.put(Material.GOLD_ORE, 1);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onItemSpawn(ItemSpawnEvent event) {
        ItemStack stack = event.getEntity().getItemStack();

        if (xp.containsKey(stack.getType())) {
            ExperienceOrb orb = (ExperienceOrb) event.getLocation().getWorld().spawnEntity(BlockUtils.center(event.getLocation()), EntityType.EXPERIENCE_ORB);
            orb.setExperience(xp.get(stack.getType()));
        }

        if (replacements.containsKey(stack.getType())) {
            stack.setType(replacements.get(stack.getType()));
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDeath(EntityDeathEvent event) {
        if (event.getEntity() instanceof Chicken) {
            event.getDrops().clear();

            for (int i = 0; i < entropy().randomInt(FEATHER_RANGE); i++) {
                event.getDrops().add(new ItemStack(Material.FEATHER));
            }

            event.getDrops().add(new ItemStack(Material.COOKED_CHICKEN));
        }

        if (event.getEntity() instanceof Cow) {
            event.getDrops().clear();

            for (int i = 0; i < entropy().randomInt(LEATHER_RANGE); i++) {
                event.getDrops().add(new ItemStack(Material.LEATHER));
            }

            for (int i = 0; i < entropy().randomInt(BEEF_RANGE); i++) {
                event.getDrops().add(new ItemStack(Material.COOKED_BEEF));
            }
        }
    }
}
