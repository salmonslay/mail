package kiwi.sofia.mail.task;

import javafx.concurrent.Task;

import jakarta.mail.Message;
import kiwi.sofia.mail.common.ImapManager;

/**
 * A task that fetches all emails from a folder.
 */
public class FetchEmailsTask extends Task<Message[]> {
    private final String folderName;

    public FetchEmailsTask(String folderName) {
        this.folderName = folderName;
    }

    @Override
    protected Message[] call() throws Exception {
        updateMessage("Fetching emails...");
        return ImapManager.getCachedInbox(folderName).getMessages();
    }
}
