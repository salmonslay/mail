package kiwi.sofia.mail.task;

import javafx.concurrent.Task;
import kiwi.sofia.mail.common.BodyParser;

/**
 * Task for downloading attachments from an email.
 */
public class DownloadAttachmentsTask extends Task<Boolean> {
    private final Object content;
    private final String path;

    public DownloadAttachmentsTask(Object content, String path) {
        this.content = content;
        this.path = path;
    }

    @Override
    protected Boolean call() throws Exception {
        return BodyParser.downloadAttachments(content, path);
    }
}
