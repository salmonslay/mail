package kiwi.sofia.mail.task;

import jakarta.mail.Address;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Button;
import kiwi.sofia.mail.common.BodyParser;

/**
 * Task that gets the attachment count and recipient count of an email,
 * and sets the corresponding buttons' text and disable state.
 */
public class SetEmailButtonsTask extends Task<Void> {
    private final Message msg;
    private final Button attachmentsButton;
    private final Button replyAllButton;

    public SetEmailButtonsTask(Message msg, Button attachmentsButton, Button replyAllButton) {
        this.msg = msg;
        this.attachmentsButton = attachmentsButton;
        this.replyAllButton = replyAllButton;
    }

    @Override
    protected Void call() {
        Platform.runLater(() -> {
            try {
                int attachmentCount = BodyParser.attachmentCount(msg);
                attachmentsButton.setDisable(attachmentCount == 0); // disable if no attachments
                attachmentsButton.setText("Download and display " + attachmentCount + " attachment" + (attachmentCount == 1 ? "" : "s")); // set button text

                Address[] recipients = msg.getAllRecipients();
                replyAllButton.setDisable(recipients != null && recipients.length < 2); // disable if less than 2 recipients
            } catch (MessagingException e) {
                throw new RuntimeException(e);
            }
        });
        return null;
    }
}
