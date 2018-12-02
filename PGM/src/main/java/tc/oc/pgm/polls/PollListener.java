package tc.oc.pgm.polls;

import com.google.inject.Inject;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import tc.oc.commons.core.chat.Audience;
import tc.oc.commons.core.chat.Audiences;
import tc.oc.pgm.polls.event.PollEndEvent;

public class PollListener implements Listener {

    private final Audiences audiences;

    @Inject PollListener(Audiences audiences) {
        this.audiences = audiences;
    }

    @EventHandler
    public void onPollEnd(PollEndEvent event) {
        if(event.getReason() == PollEndReason.Completed) {
            Audience audience = audiences.localServer();
            if(event.getPoll().isSuccessful()) {
                audience.sendMessage(Poll.normalize + "The poll " + event.getPoll().getDescriptionMessage()
                        + Poll.normalize + " has succeeded" + Poll.separator);
                audience.sendMessage(event.getPoll().formatForAgainst());
                event.getPoll().executeAction();
            } else {
                audience.sendMessage(Poll.normalize + "The poll " + event.getPoll().getDescriptionMessage()
                        + Poll.normalize + " has failed" + Poll.separator);
                audience.sendMessage(event.getPoll().formatForAgainst());
            }
        }
    }
}
