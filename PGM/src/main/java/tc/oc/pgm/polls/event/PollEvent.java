package tc.oc.pgm.polls.event;

import org.bukkit.event.Event;
import tc.oc.pgm.polls.Poll;

/**
 * Represents an event related to a poll.
 */
public abstract class PollEvent extends Event {
    protected final Poll poll;

    public PollEvent(Poll poll) {
        this.poll = poll;
    }

    /** Gets the poll instance this event is related to. */
    public Poll getPoll() {
        return this.poll;
    }
}
