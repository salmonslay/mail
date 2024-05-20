package kiwi.sofia.mail.task;

import javafx.concurrent.Task;
import kiwi.sofia.mail.common.BodyParser;

import java.util.Map;

/**
 * Task for downloading attachments from an email.
 */
public class DownloadAttachmentsTask extends Task<Map<String, String>> {
    private final Object content;
    private final String path;
    private final Integer messageHashCode;

    public DownloadAttachmentsTask(Object content, String path, Integer messageHashCode) {
        this.content = content;
        this.path = path;
        this.messageHashCode = messageHashCode;
    }

    @Override
    protected Map<String, String> call() throws Exception {
        return BodyParser.downloadAttachments(content, path, messageHashCode);
    }
}
