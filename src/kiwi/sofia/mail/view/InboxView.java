package kiwi.sofia.mail.view;

import jakarta.mail.Message;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import kiwi.sofia.mail.task.FetchEmailsTask;
import kiwi.sofia.mail.template.EmailCell;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class InboxView implements SofView {
    private static InboxView instance;
    private final Pane contentPane;
    @FXML
    private ListView<Message> listView;
    @FXML
    private BorderPane listPane;
    private ObservableList<Message> messageObservableList = FXCollections.observableArrayList();

    private InboxView() {
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
    }

    protected void fetchEmails() {
        FetchEmailsTask fetchEmailsTask = new FetchEmailsTask();

        fetchEmailsTask.setOnSucceeded(event -> {
            Message[] messages = fetchEmailsTask.getValue();
            Message[] reversedMessages = new Message[messages.length];
            for (int i = 0; i < messages.length; i++) {
                reversedMessages[messages.length - i - 1] = messages[i];
            }

            messageObservableList.clear();

            for (int i = 0; i < 10; i++) {
                messageObservableList.add(reversedMessages[i]);
            }
        });

        fetchEmailsTask.setOnFailed(event -> {
            System.out.println("Failed to fetch emails: " + fetchEmailsTask.getException().getMessage());
        });

        ExecutorService executorService = Executors.newFixedThreadPool(1);
        executorService.execute(fetchEmailsTask);
        executorService.shutdown();
    }


    @Override
    public Pane getView() {
        return contentPane;
    }

    public BorderPane getListPane() {
        return listPane;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        fetchEmails();
    }

    public static InboxView getInstance() {
        if (instance == null)
            instance = new InboxView();

        return instance;
    }
}
