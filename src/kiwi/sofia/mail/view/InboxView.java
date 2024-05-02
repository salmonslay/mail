package kiwi.sofia.mail.view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import kiwi.sofia.mail.template.EmailCell;

public class InboxView implements SofView {
    private final Pane contentPane;
    @FXML
    private ListView<String> listView;
    private ObservableList<String> messageObservableList = FXCollections.observableArrayList();

    public InboxView() {
        contentPane = new GridPane();

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/InboxView.fxml"));
            loader.setController(this);
            loader.load();

            listView.setItems(messageObservableList);
            listView.setCellFactory(param -> new EmailCell());

            messageObservableList.add("a");
            messageObservableList.add("hello hello");
            messageObservableList.add("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum");

            contentPane.getChildren().add(loader.getRoot());
        } catch (Exception e) {
            System.out.println("Failed to load InboxView.fxml" + e.getMessage());
        }
    }


    @Override
    public Pane getView() {
        return contentPane;
    }
}
