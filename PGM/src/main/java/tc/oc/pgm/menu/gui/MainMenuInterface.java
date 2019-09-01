package tc.oc.pgm.menu.gui;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import tc.oc.commons.bukkit.chat.PlayerComponent;
import tc.oc.commons.bukkit.gui.buttons.Button;
import tc.oc.commons.bukkit.gui.interfaces.ChestInterface;
import tc.oc.commons.bukkit.localization.CommonsTranslations;
import tc.oc.commons.bukkit.stats.StatsUtil;
import tc.oc.commons.bukkit.tokens.TokenUtil;
import tc.oc.commons.bukkit.util.Constants;
import tc.oc.commons.bukkit.util.ItemCreator;
import tc.oc.commons.core.chat.Component;
import tc.oc.pgm.tokens.gui.MainTokenButton;
import tc.oc.pgm.tokens.gui.MutationTokenInterface;
import tc.oc.pgm.tokens.gui.TokenPurchaseInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainMenuInterface extends ChestInterface {

    public MainMenuInterface(Player player) {
        super(player, new ArrayList<>(), 27, "Main Menu");
        updateButtons();
    }

    @Override
    public void updateButtons() {
        List<Button> buttons = new ArrayList<>();

        MainTokenButton button = new MainTokenButton();

        button.setSlot(11);
        buttons.add(button);

        HashMap<String, Double> stats = StatsUtil.getStats(getPlayer());

        buttons.add(new Button(
                new ItemCreator(Material.GOLDEN_APPLE)
                        .setData(1)
                        .setName(CommonsTranslations.get().t("stats.list", getPlayer(), Constants.PREFIX + getPlayer().getDisplayName()))
                        .addLore(ChatColor.AQUA + CommonsTranslations.get().t("stats.kills", getPlayer(), ChatColor.BLUE + String.format("%,d", (int)(double)stats.get("kills"))))
                        .addLore(ChatColor.AQUA + CommonsTranslations.get().t("stats.deaths", getPlayer(), ChatColor.BLUE + String.format("%,d", (int)(double)stats.get("deaths"))))
                        .addLore(ChatColor.AQUA + CommonsTranslations.get().t("stats.kd", getPlayer(), ChatColor.BLUE + String.format("%.2f", stats.get("kd"))))
                        .addLore(ChatColor.AQUA + CommonsTranslations.get().t("stats.wools", getPlayer(), ChatColor.BLUE + String.format("%,d", (int)(double)stats.get("wool_placed"))))
                        .addLore(ChatColor.AQUA + CommonsTranslations.get().t("stats.cores", getPlayer(), ChatColor.BLUE + String.format("%,d", (int)(double)stats.get("cores_leaked"))))
                        .addLore(ChatColor.AQUA + CommonsTranslations.get().t("stats.monuments", getPlayer(), ChatColor.BLUE + String.format("%,d", (int)(double)stats.get("destroyables_destroyed"))))
                , 13));

        buttons.add(new Button(
                new ItemCreator(Material.BOOK_AND_QUILL)
                        .setName(Constants.PREFIX + "Settings")
                , 15) {
            @Override
            public void function(Player player) {
                player.openInventory(new SettingsTypeInterface(player).getInventory());
            }
        });

        setButtons(buttons);
        updateInventory();
    }
}
