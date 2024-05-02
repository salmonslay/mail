package kiwi.sofia.mail.view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import kiwi.sofia.mail.template.EmailCell;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import java.net.URL;
import java.util.Properties;
import java.util.ResourceBundle;

public class InboxView implements SofView {
    private final GridPane contentPane;
    private final ListView<String> listView;
    private ObservableList<String> messageObservableList = FXCollections.observableArrayList();

    public InboxView() {
        contentPane = new GridPane();


        messageObservableList.add("UUH");
        messageObservableList.add("test");

        listView = new ListView<>();
        contentPane.add(listView, 0, 0);
        // max width of listView
        listView.setMaxWidth(Double.MAX_VALUE);

        listView.setItems(messageObservableList);
        listView.setCellFactory((lv) -> {
            return new EmailCell();
        });


        System.out.println("h");
    }


    @Override
    public Pane getView() {
        return contentPane;
    }
}
