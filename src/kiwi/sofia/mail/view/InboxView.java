package kiwi.sofia.mail.view;

import jakarta.mail.Message;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import kiwi.sofia.mail.task.FetchEmailsTask;
import kiwi.sofia.mail.template.EmailCell;

import java.net.URL;
import java.text.MessageFormat;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@SuppressWarnings("ManualArrayToCollectionCopy")
public class InboxView implements SofView {
    private static InboxView instance;
    @FXML
    private ListView<Message> listView;
    @FXML
    private StackPane stackPane;
    @FXML
    private Pane rootPane;
    @FXML
    private Label statusLabel;
    @FXML
    private Button buttonRight;
    @FXML
    private Button buttonLeft;
    @FXML
    private Label pageLabel;
    private ObservableList<Message> messageObservableList = FXCollections.observableArrayList();

    private InboxView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/InboxView.fxml"));
            loader.setController(this);
            loader.load();

            listView.setItems(messageObservableList);
            listView.setCellFactory(param -> new EmailCell());
        } catch (Exception e) {
            System.out.println("Failed to load InboxView.fxml" + e.getMessage());
        }
    }

    protected void fetchEmails() {
        buttonLeft.setDisable(true);
        pageLabel.setText("...");

        FetchEmailsTask fetchEmailsTask = new FetchEmailsTask();

        statusLabel.textProperty().bind(fetchEmailsTask.messageProperty());

        fetchEmailsTask.setOnSucceeded(event -> {
            statusLabel.textProperty().unbind();
            statusLabel.setText("");

            Message[] messages = fetchEmailsTask.getValue();
            Message[] reversedMessages = new Message[messages.length];
            for (int i = 0; i < messages.length; i++) {
                reversedMessages[messages.length - i - 1] = messages[i];
            }

            messageObservableList.clear();

            for (int i = 0; i < 50; i++) {
                messageObservableList.add(reversedMessages[i]);
            }

            pageLabel.setText(MessageFormat.format("Page 1/{0} ({1} emails)", messages.length / 50 + 1, messages.length));
        });

        fetchEmailsTask.setOnFailed(event -> {
            statusLabel.textProperty().unbind();
            statusLabel.setText("Could not fetch emails.\n" + fetchEmailsTask.getException().getMessage());
            System.out.println("Failed to fetch emails: " + fetchEmailsTask.getException().getMessage());
        });

        ExecutorService executorService = Executors.newFixedThreadPool(1);
        executorService.execute(fetchEmailsTask);
        executorService.shutdown();
    }

    @Override
    public Pane getView() {
        return rootPane;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        fetchEmails();
    }

    public static InboxView getInstance() {
        if (instance == null) instance = new InboxView();

        return instance;
    }

    @FXML
    public void changePageLeft() {
        System.out.println("Changing page left");
    }

    @FXML
    public void changePageRight() {
        System.out.println("Changing page right");
    }
}
