package tc.oc.pgm.rotation;

import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.TranslatableComponent;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
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
     * Returns MINI if players < 16
     * Returns MEDIUM if 16 < players < 40
     * Returns MEGA if players > 40
     *
     * @param players Current participant player count.
     * @param rotationManager The {@link RotationManager}
     * @return any of {@link RotationCategory}
     */
    private RotationCategory getAppropriateRotationCategory(int players, RotationManager rotationManager) {
        if (players <= 16 && rotationManager.getRotation("mini") != null) return RotationCategory.MINI;
        if (players > 16 && players <= 40 && rotationManager.getRotation("medium") != null) return RotationCategory.MEDIUM;
        if (players > 40 && rotationManager.getRotation("mega") != null) return RotationCategory.MEGA;

        return null;
    }
}
