package tc.oc.pgm.shop.currency;

import tc.oc.pgm.match.MatchPlayer;
import tc.oc.pgm.shop.purchasable.Purchasable;

public class ExperienceCurrency implements Currency {

    @Override
    public double getValue() {
        return 1;
    }

    @Override
    public boolean canPurchase(Purchasable purchasable, MatchPlayer player) {
        return player.getBukkit().getLevel() >= purchasable.getCost();
    }

    @Override
    public void subtract(MatchPlayer player, double amount) {
        player.getBukkit().setLevel(Math.max(0, player.getBukkit().getLevel() - (int) Math.round(amount)));
    }

    @Override
    public double getBalance(MatchPlayer player) {
        return player.getBukkit().getLevel();
    }
}