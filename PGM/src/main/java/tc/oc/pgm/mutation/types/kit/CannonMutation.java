package tc.oc.pgm.mutation.types.kit;

import org.bukkit.Material;
import tc.oc.pgm.kits.FreeItemKit;
import tc.oc.pgm.match.Match;
import tc.oc.pgm.mutation.types.KitMutation;

public class CannonMutation extends KitMutation {

    final static FreeItemKit[] SUPPLIES = new FreeItemKit[] {
            new FreeItemKit(item(Material.TNT, 256)),
            new FreeItemKit(item(Material.WOOD, 64)),
            new FreeItemKit(item(Material.STONE_BUTTON, 16)),
            new FreeItemKit(item(Material.STONE_PLATE, 32)),
            new FreeItemKit(item(Material.WATER_BUCKET, 2)),
            new FreeItemKit(item(Material.NETHER_FENCE, 64)),
            new FreeItemKit(item(Material.WOOD_STEP, 32)),
            new FreeItemKit(item(Material.LADDER, 16)),
            new FreeItemKit(item(Material.REDSTONE, 64)),
    };

    public CannonMutation(Match match) {
        super(match, true, SUPPLIES);
    }
}
