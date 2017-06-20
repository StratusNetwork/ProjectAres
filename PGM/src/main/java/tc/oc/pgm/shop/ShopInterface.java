package tc.oc.pgm.shop;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TranslatableComponent;
import org.bukkit.entity.Player;
import tc.oc.commons.bukkit.gui.Interface;
import tc.oc.commons.bukkit.gui.buttons.Button;
import tc.oc.commons.bukkit.gui.interfaces.ChestInterface;
import tc.oc.commons.bukkit.util.ItemCreator;
import tc.oc.pgm.PGMTranslations;
import tc.oc.pgm.match.Match;
import tc.oc.pgm.match.MatchPlayer;
import tc.oc.pgm.shop.purchasable.Purchasable;
import tc.oc.pgm.shop.purchasable.PurchasableSet;
import tc.oc.pgm.shop.strategy.PaymentStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ShopInterface extends ChestInterface {
    private static ShopInterface instance;

    final MatchPlayer player;
    final Shop shop;
    final PurchaseTracker tracker;
    final Match match;

    public ShopInterface(MatchPlayer player, Shop shop) {
        super(player.getBukkit(), new ArrayList<>(), shop.rows * 9, shop.getTitle(), getInstance());
        this.player = player;
        this.shop = shop;
        this.tracker = shop.tracker;
        this.match = player.getMatch();
        updateButtons();
    }

    public static ShopInterface getInstance() {
        return instance;
    }

    @Override
    public Interface getParent() {
        return getInstance();
    }

    @Override
    public void onClose() {
        shop.setInUse(false);
    }

    @Override
    public void updateButtons() {
        List<Button> buttons = new ArrayList<>();
        for (PurchasableSet set : shop.getItems()) {
            for (Purchasable purchasable : set.getItems()) {
                buttons.add(createPurchaseButton(purchasable, player));
            }
        }
        setButtons(buttons);
        updateInventory();
    }

    private Button createPurchaseButton(Purchasable purchasable, final MatchPlayer player) {
        Optional<PaymentStrategy> strategy = Optional.empty();
        switch (purchasable.getType()) {
            case PARTY:
                strategy = tracker.getOngoingStrategy(player.getParty(), purchasable);
                break;
            case INDIVIDUAL:
                strategy = tracker.getOngoingStrategy(player, purchasable);
                break;
            case GLOBAL:
                strategy = tracker.getOngoingStrategy(purchasable);
                break;
        }
        ItemCreator creator = purchasable.getIcon();
        creator.clearLore();
        if (strategy.isPresent()) {
            int cost = (int) Math.round(strategy.get().getRemainingOwed() / purchasable.getCurrency().getValue());
            int payed = (int) Math.round(strategy.get().getContribution() / purchasable.getCurrency().getValue());
            BaseComponent currencyName = cost == 1 ? purchasable.getCurrency().getSingularName() : purchasable.getCurrency().getPluralizedName();
            creator.addLore(
                    PGMTranslations.get().t(
                            ChatColor.AQUA.toString(),
                            "shop.purchase.ongoing",
                            player.getBukkit(),
                            cost,
                            currencyName,
                            payed
                    ));
        } else {
            int cost = (int) Math.round(purchasable.getCost() / purchasable.getCurrency().getValue());
            BaseComponent currencyName = cost == 1 ? purchasable.getCurrency().getSingularName() : purchasable.getCurrency().getPluralizedName();
            creator.addLore(
                    PGMTranslations.get().t(ChatColor.AQUA.toString(), "shop.purchase.cost", player.getBukkit()) +
                            ChatColor.GRAY + ": " +
                            PGMTranslations.get().t(ChatColor.AQUA.toString(), "shop.purchase.new", player.getBukkit(), cost, currencyName)
            );
        }
        if (purchasable.isIncremental()) {
            String translationKey = "";
            switch (purchasable.getType()) {
                case GLOBAL:
                    translationKey = "shop.purchasable.category.global";
                    break;
                case INDIVIDUAL:
                    translationKey = "shop.purchasable.category.individual";
                    break;
                case PARTY:
                    translationKey = "shop.purchasable.category.team";
            }
            creator.addLore(PGMTranslations.get().t(ChatColor.BLUE.toString(), translationKey, player.getBukkit()));
        }
        Optional<PaymentStrategy> finalStrat = strategy;
        return new Button(creator, purchasable.getSlot()){
            @Override
            public void function(Player player1) {
                if (finalStrat.isPresent()) {
                    if (finalStrat.get().getContributionFilter().denies(player)) {
                        player.sendWarning(new TranslatableComponent("shop.contribute.fail"), true);
                        return;
                    }
                    tracker.contribute(finalStrat.get(), player);
                } else {
                    if (purchasable.getPurchaseFilter().denies(player)) {
                        player.sendWarning(new TranslatableComponent("shop.purchase.fail"), true);
                        return;
                    }

                    if (!purchasable.getCurrency().hasCurrency(player) || (!purchasable.getCurrency().canPurchase(purchasable, player) && !purchasable.isIncremental())) {
                        player.sendWarning(PGMTranslations.t("shop.purchase.failPoor", player), true);
                        return;
                    }

                    switch (purchasable.getType()) {
                        case PARTY:
                            tracker.startPartyStrategy(player.getParty(), player, purchasable);
                            break;
                        case INDIVIDUAL:
                            tracker.startIndividualStrategy(player, purchasable);
                            break;
                        case GLOBAL:
                            tracker.startGlobalStrategy(purchasable, player);
                    }
                }
                getButtons().remove(this);
                getButtons().add(createPurchaseButton(purchasable, player));
                updateInventory();
            }
        };
    }
}
