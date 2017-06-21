package tc.oc.pgm.shop;

import com.google.api.client.util.Maps;
import com.google.inject.assistedinject.Assisted;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.TranslatableComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import tc.oc.commons.bukkit.chat.ComponentRenderContext;
import tc.oc.commons.bukkit.inventory.Slot;
import tc.oc.commons.bukkit.item.RenderedItemBuilder;
import tc.oc.commons.bukkit.listeners.ButtonListener;
import tc.oc.commons.bukkit.listeners.ButtonManager;
import tc.oc.commons.bukkit.listeners.WindowListener;
import tc.oc.commons.bukkit.listeners.WindowManager;
import tc.oc.commons.core.chat.Component;
import tc.oc.pgm.match.Competitor;
import tc.oc.pgm.match.Match;
import tc.oc.pgm.match.MatchPlayer;
import tc.oc.pgm.shop.purchasable.Purchasable;
import tc.oc.pgm.shop.strategy.PaymentStrategy;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
* GUI that is created from the items in a {@link Shop} that can be interacted with.
*/
public class ShopInterface {
    final MatchPlayer player;
    final Shop shop;
    private final ButtonManager buttonManager;
    private final WindowManager windowManager;
    private final RenderedItemBuilder.Factory itemBuilders;
    private final ComponentRenderContext renderer;    final PurchaseTracker tracker;
    final Match match;

    private Map<Slot, ShopInterface.Button> buttons = Maps.newHashMap();

    @Inject public ShopInterface(@Assisted MatchPlayer player,
                                 @Assisted Shop shop,
                                 ButtonManager buttonManager,
                                 RenderedItemBuilder.Factory itemBuilders,
                                 WindowManager windowManager,
                                 ComponentRenderContext renderer) {
        this.player = player;
        this.shop = shop;
        this.buttonManager = buttonManager;
        this.itemBuilders = itemBuilders;
        this.windowManager = windowManager;
        this.renderer = renderer;
        this.tracker = shop.tracker;
        this.match = player.getMatch();
        shop.getItems().forEach(set -> set.getItems().forEach(i -> buttons.put(i.getSlot(), new Button(i))));
    }

    public interface Factory {
        ShopInterface create(MatchPlayer player, Shop shop);
    }

    private final WindowListener windowListener = new WindowListener() {
        @Override public void windowOpened(InventoryView window) {
            shop.setInUse(true);
        }

        @Override public void windowClosed(InventoryView window) {
            shop.setInUse(false);
        }

        @Override
        public boolean windowClicked(InventoryView window, Inventory inventory, ClickType clickType, InventoryType.SlotType slotType, int slotIndex, @Nullable ItemStack item) {
            return true;
        }
    };

    private Inventory createWindow(Player player) {
        final Inventory inventory = Bukkit.createInventory(
                player,
                shop.rows * 9,
                renderer.renderLegacy(new Component(shop.getTitle(), ChatColor.DARK_GREEN, ChatColor.BOLD), player)
        );
        buttons.values().forEach(handler -> handler.updateWindow(inventory));
        return inventory;
    }

    public void openWindow(Player player) {
        if(!buttons.isEmpty()) {
            windowManager.openWindow(windowListener, player, createWindow(player));
        }
    }

    private class Button implements ButtonListener {
        final Purchasable purchasable;

        public Button(Purchasable purchasable) {
            this.purchasable = purchasable;
        }

        Optional<PaymentStrategy> getStrategy() {
            Optional<PaymentStrategy> strategy = Optional.empty();
            switch (purchasable.getType()) {
                case COMPETITOR:
                    strategy = tracker.getOngoingStrategy((Competitor) player.getParty(), purchasable);
                    break;
                case INDIVIDUAL:
                    strategy = tracker.getOngoingStrategy(player, purchasable);
                    break;
                case GLOBAL:
                    strategy = tracker.getOngoingStrategy(purchasable);
                    break;
            }
            return strategy;
        }

        ItemStack createButton() {
            if (player.isObserving())
                throw new UnsupportedOperationException("Tried to create button for observer.");

            RenderedItemBuilder itemBuilder = itemBuilders.create(player.getBukkit());
            itemBuilder.name(purchasable.getName());
            if (purchasable.getDescription() != null)
                itemBuilder.lore(purchasable.getDescription());
            itemBuilder.material(purchasable.getIcon());
            Optional<PaymentStrategy> strategy = getStrategy();
            if (strategy.isPresent()) {
                int cost = (int) Math.round(strategy.get().getRemainingOwed() / purchasable.getCurrency().getValue());
                int payed = (int) Math.round(strategy.get().getContribution() / purchasable.getCurrency().getValue());
                BaseComponent currencyName = cost == 1 ? purchasable.getCurrency().getSingularName() : purchasable.getCurrency().getPluralizedName();
                currencyName.setColor(ChatColor.AQUA);
                itemBuilder.lore(
                        new TranslatableComponent(
                                ChatColor.AQUA.toString() +
                                        "shop.purchase.ongoing",
                                player.getBukkit(),
                                cost,
                                currencyName,
                                payed
                        ));
            } else {
                int cost = (int) Math.round(purchasable.getCost() / purchasable.getCurrency().getValue());
                BaseComponent currencyName = cost == 1 ? purchasable.getCurrency().getSingularName() : purchasable.getCurrency().getPluralizedName();
                currencyName.setColor(ChatColor.AQUA);
                itemBuilder.lore(
                        new TextComponent(new TextComponent(ChatColor.GOLD.toString()), new TranslatableComponent("shop.purchase.cost"),
                                new TextComponent(ChatColor.DARK_GRAY.toString()), new TextComponent(": "),
                                new TextComponent(ChatColor.GREEN.toString()), new TranslatableComponent("shop.purchase.new", cost, currencyName)
                        )
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
                    case COMPETITOR:
                        translationKey = "shop.purchasable.category.team";
                }
                itemBuilder.lore(new TranslatableComponent(translationKey));
            }
            return buttonManager.createButton(this, itemBuilder.get());
        }

        @Override
        public boolean buttonClicked(ItemStack button, Player clicker, ClickType clickType, Event event) {
            Optional<PaymentStrategy> strategy = getStrategy();
            if (strategy.isPresent()) {
                if (strategy.get().getContributionFilter().denies(player)) {
                    player.sendWarning(new TranslatableComponent("shop.contribute.fail"), true);
                    return true;
                }
                tracker.contribute(strategy.get(), player);
            } else {
                if (purchasable.getPurchaseFilter().denies(player)) {
                    player.sendWarning(new TranslatableComponent("shop.purchase.fail"), true);
                    return true;
                }

                if (!purchasable.getCurrency().hasCurrency(player) || (!purchasable.getCurrency().canPurchase(purchasable, player) && !purchasable.isIncremental())) {
                    player.sendWarning(new TranslatableComponent("shop.purchase.failPoor"), true);
                    return true;
                }

                switch (purchasable.getType()) {
                    case COMPETITOR:
                        tracker.startCompetitorStrategy((Competitor) player.getParty(), player, purchasable);
                        break;
                    case INDIVIDUAL:
                        tracker.startIndividualStrategy(player, purchasable);
                        break;
                    case GLOBAL:
                        tracker.startGlobalStrategy(purchasable, player);
                }
            }
            return true;
        }

        void updateWindow(Inventory inventory) {
            final ItemStack stack = createButton();
            if(!Objects.equals(stack, purchasable.getSlot().getItem(inventory))) {
                purchasable.getSlot().putItem(inventory, stack);
            }
        }
    }
}