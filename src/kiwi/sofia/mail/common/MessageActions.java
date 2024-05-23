package kiwi.sofia.mail.common;

import jakarta.mail.Flags;
import jakarta.mail.Folder;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import kiwi.sofia.mail.view.InboxView;

public class MessageActions {
    public static void starMessage(Message message, boolean isStarred) {
        Task<Void> starTask = new Task<>() {
            @Override
            protected Void call() throws MessagingException {
                message.setFlag(Flags.Flag.FLAGGED, !isStarred);
                System.out.printf("Successfully %s email with subject: %s\n", isStarred ? "unstarred" : "starred", message.getSubject());
                return null;
            }
        };

        // Display an error if starring failed
        starTask.setOnFailed(e -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Failed to star email");
            alert.setHeaderText(null);
            alert.setContentText(starTask.getException().getMessage());
            alert.showAndWait();
        });

        new Thread(starTask).start();
    }

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

        deleteTask.setOnFailed(e -> {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setTitle("Failed to delete email");
            errorAlert.setHeaderText(null);
            errorAlert.setContentText(deleteTask.getException().getMessage());
            errorAlert.showAndWait();
        });

        deleteTask.setOnSucceeded(e -> {
            InboxView.getInstance().fetchEmails();

            Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
            successAlert.setTitle("Email deleted");
            successAlert.setHeaderText(null);
            successAlert.setContentText("The email has been successfully deleted.");
            successAlert.showAndWait();
        });

        new Thread(deleteTask).start();
    }
}
