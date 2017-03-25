package tc.oc.pgm.rotation;

import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.TranslatableComponent;
import org.bukkit.ChatColor;
import org.bukkit.configuration.Configuration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import tc.oc.pgm.Config;
import tc.oc.pgm.PGM;
import tc.oc.pgm.cycle.CycleMatchModule;
import tc.oc.pgm.events.MatchEndEvent;

public class DynamicRotationChangeListener implements Listener {

    @EventHandler
    public void onMatchEnd(MatchEndEvent event) {
        RotationManager rotationManager = PGM.getMatchManager().getRotationManager();

        // Ignore if there is only one rotation available
        if (rotationManager.getRotations().size() == 1) return;

        // Number of players we can assume is active
        int participatingPlayers = event.getMatch().getParticipatingPlayers().size();

        RotationCategory appr = getAppropriateRotationCategory(participatingPlayers, rotationManager);
        if (appr != null && !appr.toString().toLowerCase().equals(rotationManager.getCurrentRotationName().toLowerCase())) {
            rotationManager.setRotation(rotationManager.getRotation(appr.toString().toLowerCase()));
            CycleMatchModule cmm = event.getMatch().needMatchModule(CycleMatchModule.class);
            cmm.startCountdown(cmm.getConfig().countdown());

            event.getMatch().sendMessage(new TextComponent(ChatColor.RED + "" + ChatColor.STRIKETHROUGH + "-----------------------------------"));
            event.getMatch().sendMessage(new TextComponent(ChatColor.GOLD + "           " + ChatColor.BOLD + "" + new TranslatableComponent("rotation.change.broadcast.title").getTranslate()));
            event.getMatch().sendMessage(new TextComponent(ChatColor.YELLOW + "  " + new TranslatableComponent("rotation.change.broadcast.info").getTranslate()));
            event.getMatch().sendMessage(new TextComponent(ChatColor.RED + "" + ChatColor.STRIKETHROUGH + "-----------------------------------"));
        }
    }

    /**
     * Returns appropriate rotation looking at how many players (participating) are online.
     *
     * @param players Current participant player count.
     * @param rotationManager The {@link RotationManager}
     * @return any of {@link RotationCategory}
     */
    private RotationCategory getAppropriateRotationCategory(int players, RotationManager rotationManager) {
        Configuration config = Config.getConfiguration();
        int medium = config.getInt("rotation.providers.file.medium.count");
        int mega = config.getInt("rotation.providers.file.mega.count");

        if (players > medium && players <= mega && rotationManager.getRotation("medium") != null) return RotationCategory.MEDIUM;
        if (players > mega && rotationManager.getRotation("mega") != null) return RotationCategory.MEGA;

        return RotationCategory.MINI;
    }
}
