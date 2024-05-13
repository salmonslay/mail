package kiwi.sofia.mail.view;

import jakarta.mail.FetchProfile;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import kiwi.sofia.mail.common.ImapManager;
import kiwi.sofia.mail.task.FetchEmailsTask;
import kiwi.sofia.mail.template.EmailCell;

import java.net.URL;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


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
    private Message[] messages;
    private int currentPage = 0;

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
            this.messages = reversedMessages;

            currentPage = 0;
            refreshPage();
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

    /**
     * Changes the page to the left. If the current page is the first page, the button is disabled.
     */
    @FXML
    public void changePageLeft() {
        System.out.println("Changing page left");

        currentPage--;
        if (currentPage <= 0) {
            currentPage = 0;
            buttonLeft.setDisable(true);
        } else {
            buttonLeft.setDisable(false);
        }

        buttonRight.setDisable(false);

        refreshPage();
    }

    /**
     * Changes the page to the right. If the current page is the last page, the button is disabled.
     */
    @FXML
    public void changePageRight() {
        System.out.println("Changing page right");

        currentPage++;
        if (currentPage >= messages.length / 50) {
            currentPage = messages.length / 50;
            buttonRight.setDisable(true);
        } else {
            buttonRight.setDisable(false);
        }

        buttonLeft.setDisable(false);

        refreshPage();
    }

    /**
     * Refreshes the page with emails from the current page number.
     */
    private void refreshPage() {
        messageObservableList.clear();
        int firstIndex = currentPage * 50;
        int lastIndex = Math.min((currentPage + 1) * 50, messages.length);
        Message[] messages = Arrays.copyOfRange(this.messages, firstIndex, lastIndex);

        FetchProfile fetchProfile = new FetchProfile();
        fetchProfile.add(FetchProfile.Item.ENVELOPE);

        try {
            ImapManager.getCachedInbox().fetch(messages, fetchProfile);
            messageObservableList.addAll(messages);
        } catch (MessagingException e) {
            e.printStackTrace();
        }

        updatePageLabel();
    }

    /**
     * Updates the page label with the current email range in the format "51 - 100 of 1323"
     */
    private void updatePageLabel() {
        pageLabel.setText(MessageFormat.format("{0} - {1} of {2}",
                currentPage * 50 + 1,
                Math.min((currentPage + 1) * 50, messages.length),
                messages.length));
    }
}
