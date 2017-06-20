package tc.oc.pgm.shop.strategy;

import tc.oc.pgm.filters.Filter;
import tc.oc.pgm.match.MatchPlayer;
import tc.oc.pgm.match.Party;
import tc.oc.pgm.shop.purchasable.Purchasable;

public class PartyPaymentStrategy extends PaymentStrategyImpl<Party> {
    final Party party;

    public PartyPaymentStrategy(Purchasable purchasable, Filter contributionFilter, Party party) {
        super(purchasable, contributionFilter);
        this.party = party;
    }

    @Override
    public boolean canContribute(MatchPlayer player) {
        return party.getPlayers().contains(player);
    }

    @Override
    public Party getOwner() {
        return this.party;
    }
}
