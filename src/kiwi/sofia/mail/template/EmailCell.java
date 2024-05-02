package kiwi.sofia.mail.template;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.GridPane;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.GridPane;

import javax.mail.Message;
import java.io.IOException;

public class EmailCell extends ListCell<String> {

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
    private FXMLLoader mLLoader;

    @Override
    protected void updateItem(String message, boolean empty) {
        super.updateItem(message, empty);

        if (empty)
            return;

        if (mLLoader == null) {
            mLLoader = new FXMLLoader(getClass().getResource("/fxml/EmailCell.fxml"));
            mLLoader.setController(this);

            try {
                mLLoader.load();
            } catch (Exception ignored) {
            }
        }

        subjectLabel.setText("Subject looooooooong");
        fromLabel.setText("From");
        //dateLabel.setText("hello");
        bodyLabel.setText("Body");
        setGraphic(gridPane);
        setText(null);

    }
}
