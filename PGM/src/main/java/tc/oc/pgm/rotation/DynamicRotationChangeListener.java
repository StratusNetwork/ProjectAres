package tc.oc.pgm.rotation;

import net.md_5.bungee.api.chat.TranslatableComponent;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import tc.oc.pgm.PGM;
import tc.oc.pgm.cycle.CycleMatchModule;
import tc.oc.pgm.events.MatchEndEvent;

import java.util.Map;

public class DynamicRotationChangeListener implements Listener {

    @EventHandler
    public void onMatchEnd(MatchEndEvent event) {
        RotationManager rotationManager = PGM.getMatchManager().getRotationManager();

        // Ignore if there is only one rotation available
        if (rotationManager.getRotations().size() == 1) return;

        int participatingPlayers = event.getMatch().getParticipatingPlayers().size();
        int bestCandidateCount = Integer.MAX_VALUE;
        RotationState bestCandidate = null;

        for (Map.Entry<String, RotationState> entry : rotationManager.getRotations().entrySet()) {
            int playerDifference = Math.abs(entry.getValue().getAverageNeededPlayers() - participatingPlayers);
            if (playerDifference < bestCandidateCount) {
                bestCandidateCount = playerDifference;
                bestCandidate = entry.getValue();
            }
        }

        // Return if current rotation is suitable for current player count.
        if (bestCandidate == rotationManager.getRotation() || bestCandidate == null) return;

        // Change rotation to accommodate players & announce
        rotationManager.setRotation(bestCandidate);
        CycleMatchModule cmm = event.getMatch().needMatchModule(CycleMatchModule.class);
        cmm.startCountdown(cmm.getConfig().countdown());

        event.getMatch().getServer().broadcastMessage(ChatColor.RED + "" + ChatColor.STRIKETHROUGH + "-----------------------------------");
        event.getMatch().getServer().broadcastMessage(ChatColor.GOLD + "           " + ChatColor.BOLD + "" + new TranslatableComponent("rotation.change.broadcast.title").getTranslate());
        event.getMatch().getServer().broadcastMessage(ChatColor.YELLOW + "  " + new TranslatableComponent("rotation.change.broadcast.info").getTranslate());
        event.getMatch().getServer().broadcastMessage(ChatColor.RED + "" + ChatColor.STRIKETHROUGH + "-----------------------------------");
    }
}
