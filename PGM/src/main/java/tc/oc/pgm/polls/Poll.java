package tc.oc.pgm.polls;

import com.google.inject.Inject;
import java.util.HashMap;
import java.util.Map;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import tc.oc.api.docs.PlayerId;
import tc.oc.commons.core.chat.Audiences;
import tc.oc.commons.core.chat.Components;

public abstract class Poll implements Runnable {

    public static final String boldAqua = ChatColor.BOLD + "" + ChatColor.AQUA;
    public static final String normalize = ChatColor.RESET + "" + ChatColor.DARK_AQUA;
    public static final String separator = ChatColor.RESET + " | ";

    protected final PollManager pollManager;
    protected final PlayerId initiator;
    protected final Audiences audiences;

    @Inject public Poll(PollManager pollManager, PlayerId initiator, Audiences audiences) {
        this.pollManager = pollManager;
        this.initiator = initiator;
        this.audiences = audiences;
        this.voteFor(initiator);
    }

    protected final Map<PlayerId, Boolean> votes = new HashMap<>();
    protected int timeLeftSeconds = 60;

    public PlayerId getInitiator() {
        return this.initiator;
    }

    public int getTotalVotes() {
        return this.votes.size();
    }

    public int getVotesFor() {
        return this.getVotes(true);
    }

    public int getVotesAgainst() {
        return this.getVotes(false);
    }

    private int getVotes(boolean filterValue) {
        int total = 0;
        for(boolean vote : this.votes.values()) {
            if(vote == filterValue) total += 1;
        }
        return total;
    }


    public int getTimeLeftSeconds() {
        return timeLeftSeconds;
    }

    private void decrementTimeLeft() {
        timeLeftSeconds -= 5;
    }

    public boolean isSuccessful() {
        return this.getVotesFor() > this.getVotesAgainst();
    }

    public abstract void executeAction();

    public abstract String getActionString();

    public abstract String getDescriptionMessage();

    public String getStatusMessage() {
        String message = boldAqua + "[Poll] " + this.getTimeLeftSeconds() + normalize + " seconds left" + separator;
        message += getActionString() + separator + formatForAgainst();

        return message;
    }

    protected String formatForAgainst() {
        return normalize + "Yes: " + boldAqua + this.getVotesFor() + " " + normalize + "No: " + boldAqua + this.getVotesAgainst();
    }

    public static BaseComponent tutorialMessage() {
        BaseComponent yes = Components.clickEvent(new TextComponent("YES"), ClickEvent.Action.RUN_COMMAND, "/vote yes");
        yes = Components.hoverEvent(yes, HoverEvent.Action.SHOW_TEXT, new TextComponent("Vote in favor of the poll."));
        yes.setColor(net.md_5.bungee.api.ChatColor.GREEN);

        BaseComponent no = Components.clickEvent(new TextComponent("NO"), ClickEvent.Action.RUN_COMMAND, "/vote no");
        no = Components.hoverEvent(no, HoverEvent.Action.SHOW_TEXT, new TextComponent("Vote against the poll."));
        no.setColor(net.md_5.bungee.api.ChatColor.RED);

        return new TextComponent(
                Components.color(new TextComponent("Click "), net.md_5.bungee.api.ChatColor.DARK_AQUA),
                yes,
                Components.color(new TextComponent(" or "), net.md_5.bungee.api.ChatColor.DARK_AQUA),
                no,
                Components.color(new TextComponent(" to vote!"), net.md_5.bungee.api.ChatColor.DARK_AQUA)
        );
    }

    public boolean hasVoted(PlayerId playerId) {
        return this.votes.containsKey(playerId);
    }

    public void voteFor(PlayerId playerId) {
        this.votes.put(playerId, true);
    }

    public void voteAgainst(PlayerId playerId) {
        this.votes.put(playerId, false);
    }

    @Override
    public void run() {
        int timeLeftSeconds = this.getTimeLeftSeconds();
        if(timeLeftSeconds <= 0) {
            this.pollManager.endPoll(PollEndReason.Completed);
        } else if(timeLeftSeconds % 15 == 0 || (timeLeftSeconds < 15 && timeLeftSeconds % 5 == 0)) {
            this.audiences.all().sendMessage(this.getStatusMessage());
            this.audiences.all().sendMessage(tutorialMessage());
        }
        this.decrementTimeLeft();
    }
}
