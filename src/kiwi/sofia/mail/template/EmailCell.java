package kiwi.sofia.mail.template;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.GridPane;

import javax.mail.Message;
import javax.mail.MessagingException;
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

            subjectLabel.setText(message.getSubject());
            fromLabel.setText(message.getFrom()[0].toString());

            dateLabel.setText(message.getSentDate().toString());
            bodyLabel.setText("body");
            setGraphic(gridPane);
            setText(null);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }
}
