package tc.oc.pgm.killreward;

import com.google.common.collect.ImmutableList;
import org.bukkit.inventory.ItemStack;
import tc.oc.pgm.filters.Filter;
import tc.oc.pgm.filters.matcher.StaticFilter;
import tc.oc.pgm.kits.ItemKit;
import tc.oc.pgm.kits.Kit;

public class KillReward {
    public final ImmutableList<ItemStack> items;
    public final Filter filter;
    public final Kit kit;
    public final boolean drop;

    public KillReward(ImmutableList<ItemStack> items, Filter filter, Kit kit, boolean drop) {
        this.items = items;
        this.filter = filter;
        this.kit = kit;
        this.drop = drop;
    }

    public KillReward(ItemKit kit) {
        this(ImmutableList.of(kit.item()), StaticFilter.ALLOW, kit, false);
    }
}
