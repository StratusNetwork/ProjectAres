package tc.oc.pgm.polls.event;

import org.bukkit.event.HandlerList;
import tc.oc.pgm.polls.Poll;
import tc.oc.pgm.polls.PollEndReason;

/**
 * Called when a poll ends.
 */
public class PollEndEvent extends PollEvent {
    private static final HandlerList handlers = new HandlerList();

    private final PollEndReason reason;

    public PollEndEvent(Poll poll, PollEndReason reason) {
        super(poll);
        this.reason = reason;
    }

    public PollEndReason getReason() {
        return this.reason;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
