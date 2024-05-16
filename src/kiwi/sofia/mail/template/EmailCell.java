package kiwi.sofia.mail.template;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.GridPane;
import kiwi.sofia.mail.view.ClientView;
import kiwi.sofia.mail.view.EmailView;

public class EmailCell extends ListCell<Message> {

    @FXML
    private Label subjectLabel;
    @FXML
    private Label fromLabel;
    @FXML
    private Label dateLabel;
    @FXML
    private GridPane gridPane;
    private FXMLLoader loader;
    private Message message;

    @Override
    protected void updateItem(Message message, boolean empty) {
        super.updateItem(message, empty);

        if (empty) return;

        if (loader == null) {
            loader = new FXMLLoader(getClass().getResource("/fxml/EmailCell.fxml"));
            loader.setController(this);

            try {
                loader.load();
            } catch (Exception ignored) {
            }
        }
        try {
            this.message = message;

            subjectLabel.setText(message.getSubject());
            fromLabel.setText(message.getFrom()[0].toString());

            dateLabel.setText(message.getSentDate().toString());

            setGraphic(gridPane);
            setText(null);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @FXML
    protected void openEmail() {
        try {
            System.out.printf("Opening email with subject: %s\n", message.getSubject());

            EmailView.show(message);
        } catch (MessagingException e) {
            System.out.println(e.getMessage());
        }
    }
}
