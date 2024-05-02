package kiwi.sofia.mail.template;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.GridPane;

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
    private FXMLLoader loader;

    @Override
    protected void updateItem(String message, boolean empty) {
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

        subjectLabel.setText("Subject looooooooonglooooooooonglooooooooonglooooooooong");
        fromLabel.setText("From");
        dateLabel.setText("27/7");
        bodyLabel.setText(message);
        setGraphic(gridPane);
        setText(null);

    }
}
