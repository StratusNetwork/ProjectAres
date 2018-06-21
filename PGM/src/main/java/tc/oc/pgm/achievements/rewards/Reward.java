package tc.oc.pgm.achievements.rewards;

import org.bukkit.Material;

import javax.annotation.Nullable;

public class Reward {
    public RewardTypes types;
    public @Nullable Material material;
    public int amount;

    public Reward(RewardTypes types, @Nullable Material material, int amount) {
        this.types = types;
        this.material = material;
        this.amount = amount;
    }

    public RewardTypes getTypes() {
        return types;
    }

    public @Nullable Material getMaterial() {
        return material;
    }

    public int getAmount() {
        return amount;
    }
}
