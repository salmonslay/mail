package kiwi.sofia.mail.view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import kiwi.sofia.mail.common.ImapManager;
import kiwi.sofia.mail.template.EmailCell;

import javax.mail.*;

public class InboxView implements SofView {
    private final Pane contentPane;
    @FXML
    private ListView<Message> listView;
    private ObservableList<Message> messageObservableList = FXCollections.observableArrayList();

    public InboxView() {
        contentPane = new GridPane();

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/InboxView.fxml"));
            loader.setController(this);
            loader.load();

            listView.setItems(messageObservableList);
            listView.setCellFactory(param -> new EmailCell());

            contentPane.getChildren().add(loader.getRoot());
        } catch (Exception e) {
            System.out.println("Failed to load InboxView.fxml" + e.getMessage());
        }

        try {
            Folder inbox = ImapManager.getInbox();
            Message[] messages = inbox.getMessages();
            Message[] reversedMessages = new Message[messages.length];
            for (int i = 0; i < messages.length; i++) {
                reversedMessages[messages.length - i - 1] = messages[i];
            }

            for(int i = 0; i < 10; i++) {
                messageObservableList.add(reversedMessages[i]);
            }
        } catch (Exception e) {
            System.out.println("Failed to get inbox messages" + e.getMessage());
        }
    }


    @Override
    public Pane getView() {
        return contentPane;
    }
}
