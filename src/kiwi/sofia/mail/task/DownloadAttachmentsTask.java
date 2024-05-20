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
        boolean result = BodyParser.downloadAttachments(content, path);

        if (result)
            Runtime.getRuntime().exec("explorer.exe /open," + path); // open the folder in explorer if successful

        return result;
    }
}
