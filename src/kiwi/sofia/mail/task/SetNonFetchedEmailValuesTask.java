package kiwi.sofia.mail.task;

import jakarta.mail.Address;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import kiwi.sofia.mail.common.BodyParser;

/**
 * Task that gets the attachment count and recipient count of an email,
 * and sets the corresponding buttons' text and disable state. Also manages
 * the "to" label.
 * <p>
 * Things here are not pre-fetched, which is why we need to do it here.
 */
public class SetNonFetchedEmailValuesTask extends Task<Void> {
    private final Message msg;
    private final Button attachmentsButton;
    private final Button replyAllButton;
    private final Label toLabel;

    public SetNonFetchedEmailValuesTask(Message msg, Button attachmentsButton, Button replyAllButton, Label toLabel) {
        this.msg = msg;
        this.attachmentsButton = attachmentsButton;
        this.replyAllButton = replyAllButton;
        this.toLabel = toLabel;
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

                // Set the to-field
                String toText = getToText(recipients);
                toLabel.setText(toText);
                toLabel.setTooltip(new Tooltip(toText)); // in case it's too long to display the user can hover as a fallback

            } catch (MessagingException e) {
                throw new RuntimeException(e);
            }
        });
        return null;
    }

    /**
     * Gets the recipients of the email.
     *
     * @return The recipients of the email in the format "to email1, email2, ...", or "to me" if the recipients are null.
     */
    private String getToText(Address[] recipients) {
        try {
            if (recipients != null && recipients.length > 0) {
                StringBuilder to = new StringBuilder("to: ");

                for (int i = 0; i < recipients.length; i++) {
                    Address recipient = recipients[i];
                    to.append(recipient);
                    if (i < recipients.length - 1) to.append(", "); // add comma if not last recipient
                }
                return to.toString();
            }
        } catch (Exception ignored) {
        }
        return "to me";
    }
}
