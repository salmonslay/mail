package kiwi.sofia.mail.template;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.GridPane;
import kiwi.sofia.mail.view.ClientView;
import kiwi.sofia.mail.view.EmailView;
import kiwi.sofia.mail.view.InboxView;


import java.io.IOException;

public class EmailCell extends ListCell<Message> {

    @FXML
    private Label subjectLabel;
    @FXML
    private Label fromLabel;
    @FXML
    private Label dateLabel;
    @FXML
    private Label bodyLabel;
    @FXML
    private GridPane gridPane;
    private FXMLLoader loader;
    private Message message;

    @Override
    protected void updateItem(Message message, boolean empty) {
        super.updateItem(message, empty);

        if (empty)
            return;

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
            bodyLabel.setText("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum");
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

            ClientView.setCenter(new EmailView(message).getView());
        } catch (MessagingException e) {
            System.out.println(e.getMessage());
        }
    }
}
