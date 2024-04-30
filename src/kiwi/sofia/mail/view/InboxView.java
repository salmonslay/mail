package kiwi.sofia.mail.view;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import kiwi.sofia.mail.template.EmailCell;

import javax.mail.Message;

public class InboxView implements SofView {
    private final GridPane contentPane;

    public InboxView() {
        contentPane = new GridPane();

        ListView<Message> listView = new ListView<Message>();
        contentPane.add(listView, 0, 0);
        listView.setCellFactory((ListView<Message> cell) -> new EmailCell() {
            @Override
            protected void updateItem(Message message, boolean empty) {
                super.updateItem(message, empty);

                if (empty)
                    return;

                setGraphic(new GridPane());
            }
        });

    }

    @Override
    public Pane getView() {
        return contentPane;
    }
}
