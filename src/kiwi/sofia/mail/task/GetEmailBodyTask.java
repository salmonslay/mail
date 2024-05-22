package kiwi.sofia.mail.task;

import jakarta.mail.Flags;
import jakarta.mail.Message;
import javafx.concurrent.Task;
import kiwi.sofia.mail.common.BodyParser;
import kiwi.sofia.mail.common.Pair;

/**
 * Basic task to set the content of an email, and marks it as read.
 */
public class GetEmailBodyTask extends Task<Pair<String, String>> {
    private final Message msg;

    public GetEmailBodyTask(Message msg) {
        this.msg = msg;
    }

    @Override
    protected Pair<String, String> call() throws Exception {
        msg.setFlag(Flags.Flag.SEEN, true);

        Object body = msg.getContent();
        return BodyParser.parse(body, true);
    }
}
