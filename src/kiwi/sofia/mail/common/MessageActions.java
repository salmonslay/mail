package kiwi.sofia.mail.common;

import jakarta.mail.Flags;
import jakarta.mail.Folder;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import kiwi.sofia.mail.view.ClientView;
import kiwi.sofia.mail.view.InboxView;

public class MessageActions {
    /**
     * Flag or unflag a message. Not blocking.
     *
     * @param message The message to flag
     * @param flag    The flag to set
     * @param set     Whether to set or unset the flag
     */
    public static void flagMessage(Message message, Flags.Flag flag, boolean set) {
        Task<Void> flagTask = new Task<>() {
            @Override
            protected Void call() throws MessagingException {
                message.setFlag(flag, set);
                System.out.printf("Successfully %s flag on email with subject: %s\n", set ? "set" : "unset", message.getSubject());
                return null;
            }
        };

        // Display an error if starring failed
        flagTask.setOnFailed(e -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Failed to apply flag to email");
            alert.setHeaderText(null);
            alert.setContentText(flagTask.getException().getMessage());
            alert.showAndWait();
        });

        new Thread(flagTask).start();
    }

    /**
     * This method will ask the user for confirmation and then delete a message.
     * It will also move the message to the trash folder if it exists.
     *
     * @param message The message to delete
     */
    public static void askAndTrashMessage(Message message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete email");
        alert.setHeaderText(null);
        try {
            alert.setContentText("Are you sure you want to delete the email \"" + message.getSubject() + "\"?");
        } catch (MessagingException e) {
            alert.setContentText("Are you sure you want to delete this email?");
        }
        alert.showAndWait();

        if (alert.getResult().getButtonData().isCancelButton()) {
            return;
        }

        Task<Void> deleteTask = new Task<>() {
            @Override
            protected Void call() throws MessagingException {
                message.setFlag(Flags.Flag.DELETED, true);

                // Special case for Gmail: move to trash instead of deleting
                // https://javaee.github.io/javamail/FAQ
                Folder trash = ImapManager.getFolder("[Gmail]/Trash");
                if (trash != null)
                    message.getFolder().copyMessages(new Message[]{message}, trash);

                System.out.printf("Successfully deleted email with subject: %s\n", message.getSubject());
                return null;
            }
        };

        // sometimes this task fails but the message is still deleted, no idea why
        deleteTask.setOnFailed(e -> {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setTitle("Failed to delete email");
            errorAlert.setHeaderText(null);
            String errorMessage = deleteTask.getException().getMessage();
            if (errorMessage.isBlank())
                errorMessage = "Could not get an error message, but it's possible that the email was deleted regardless. JavaMail is not very reliable.";
            errorAlert.setContentText(errorMessage);
            errorAlert.showAndWait();
        });

        deleteTask.setOnSucceeded(e -> {
            InboxView.getInstance().fetchEmails();
            ClientView.showInbox();

            Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
            successAlert.setTitle("Email deleted");
            successAlert.setHeaderText(null);
            successAlert.setContentText("The email has been successfully deleted.");
            successAlert.showAndWait();
        });

        new Thread(deleteTask).start();
    }
}
