package kiwi.sofia.mail.template;

import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.GridPane;

import javax.mail.Message;

public class EmailCell extends ListCell<Message> {
    public EmailCell() {
        super();
    }

    @Override
    protected void updateItem(Message message, boolean empty) {
        super.updateItem(message, empty);

        if (empty)
            return;

        GridPane gridPane = new GridPane();
        gridPane.add(new Label("hello"), 0, 0);

        setGraphic(gridPane);
    }
}
