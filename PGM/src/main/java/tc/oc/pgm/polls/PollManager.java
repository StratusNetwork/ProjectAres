package tc.oc.pgm.polls;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.sk89q.minecraft.util.commands.CommandException;
import java.time.Duration;
import org.bukkit.Bukkit;
import org.bukkit.event.EventBus;
import tc.oc.commons.core.chat.Audiences;
import tc.oc.commons.core.commands.TranslatableCommandException;
import tc.oc.commons.core.plugin.PluginFacet;
import tc.oc.commons.core.scheduler.Scheduler;
import tc.oc.commons.core.scheduler.Task;
import tc.oc.pgm.polls.event.PollEndEvent;
import tc.oc.pgm.polls.event.PollStartEvent;

@Singleton
public class PollManager implements PluginFacet {

    private final Scheduler scheduler;
    private final EventBus eventBus;
    private final Audiences audiences;

    @Inject
    PollManager(Scheduler scheduler, EventBus eventBus, Audiences audiences) {
        this.scheduler = scheduler;
        this.eventBus = eventBus;
        this.audiences = audiences;
    }

    private Poll currentPoll = null;
    private Task task = null;

    public Poll getPoll() {
        return currentPoll;
    }

    public boolean isPollRunning() {
        return this.currentPoll != null;
    }

    public void startPoll(Poll poll) throws CommandException {
        if(!isPollRunning()) {
            task = scheduler.createRepeatingTask(Duration.ZERO, Duration.ofSeconds(5), poll);
            currentPoll = poll;
            eventBus.callEvent(new PollStartEvent(poll));
            audiences.localServer().sendMessage(Poll.boldAqua + poll.getInitiator().username() + Poll.normalize + " has started a poll " + poll.getDescriptionMessage());
            Bukkit.broadcastMessage(Poll.tutorialMessage());
        } else {
            throw new TranslatableCommandException("poll.already.running");
        }
    }

    public void endPoll(PollEndReason reason) {
        if(this.isPollRunning()) {
            eventBus.callEvent(new PollEndEvent(this.currentPoll, reason));
            task.cancel();
            task = null;
            currentPoll = null;
        }
    }
}
