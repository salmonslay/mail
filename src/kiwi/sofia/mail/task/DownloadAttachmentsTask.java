package kiwi.sofia.mail.task;

import jakarta.mail.Message;
import javafx.concurrent.Task;
import kiwi.sofia.mail.common.BodyParser;

import java.util.Map;

/**
 * Task for downloading attachments from an email.
 */
public class DownloadAttachmentsTask extends Task<Map<String, String>> {
    private final Message message;
    private final String path;
    private final Integer messageHashCode;

    public DownloadAttachmentsTask(Message message, String path, Integer messageHashCode) {
        this.message = message;
        this.path = path;
        this.messageHashCode = messageHashCode;
    }

    @Override
    protected Map<String, String> call() throws Exception {
        return BodyParser.downloadAttachments(message.getContent(), path, messageHashCode);
    }
}
