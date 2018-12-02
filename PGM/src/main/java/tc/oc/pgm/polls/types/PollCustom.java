package tc.oc.pgm.polls.types;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import tc.oc.api.docs.PlayerId;
import tc.oc.commons.core.chat.Audiences;
import tc.oc.pgm.polls.Poll;
import tc.oc.pgm.polls.PollManager;

public class PollCustom extends Poll {

    public interface Factory {
        PollCustom create(String text, PlayerId initiator);
    }

    private String text;

    @AssistedInject PollCustom(@Assisted String text, @Assisted PlayerId initiator, PollManager pollManager, Audiences audiences) {
        super(pollManager, initiator, audiences);
        this.text = text;
    }

    @Override
    public void executeAction() {
        //I do nothing
    }

    @Override
    public String getActionString() {
        return boldAqua + text.substring(0, 1).toUpperCase() + text.substring(1);
    }

    @Override
    public String getDescriptionMessage() {
        return boldAqua + text;
    }
}