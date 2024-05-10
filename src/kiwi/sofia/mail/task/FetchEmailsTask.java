package kiwi.sofia.mail.task;

import javafx.concurrent.Task;

import jakarta.mail.Message;
import kiwi.sofia.mail.common.ImapManager;

public class FetchEmailsTask extends Task<Message[]> {
    @Override
    protected Message[] call() throws Exception {
        return ImapManager.getCachedInbox().getMessages();
    }
}
