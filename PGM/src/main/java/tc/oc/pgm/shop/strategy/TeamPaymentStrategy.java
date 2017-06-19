package tc.oc.pgm.shop.strategy;

import tc.oc.pgm.filters.Filter;
import tc.oc.pgm.match.MatchPlayer;
import tc.oc.pgm.shop.purchasable.Purchasable;
import tc.oc.pgm.teams.Team;

public class TeamPaymentStrategy extends PaymentStrategy.Impl {
    final Team team;

    public TeamPaymentStrategy(Purchasable purchasable, Filter contributionFilter, Team team) {
        super(purchasable, contributionFilter);
        this.team = team;
    }

    @Override
    public boolean canContribute(MatchPlayer player) {
        return team.getPlayers().contains(player);
    }
}
