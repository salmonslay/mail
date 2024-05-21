package kiwi.sofia.mail.view;

import jakarta.mail.FetchProfile;
import jakarta.mail.Folder;
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
import kiwi.sofia.mail.task.GetFoldersTask;
import kiwi.sofia.mail.template.EmailCell;
import kiwi.sofia.mail.template.FolderCell;

import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A view for displaying the user's inbox.
 */
public class InboxView implements SofView {
    private static InboxView instance;
    @FXML
    private ListView<Message> emailListView;
    @FXML
    private ListView<Folder> folderListView;
    @FXML
    private StackPane stackPane;
    @FXML
    private Pane rootPane;
    @FXML
    private Label emailStatusLabel;
    @FXML
    private Label folderStatusLabel;
    @FXML
    private Button buttonRight;
    @FXML
    private Button buttonLeft;
    @FXML
    private Label pageLabel;
    private final ObservableList<Message> messageObservableList = FXCollections.observableArrayList();
    private final ObservableList<Folder> folderObservableList = FXCollections.observableArrayList();
    private Message[] messages;
    private int currentPage = 0;
    @FXML
    private Button buttonReload;
    private boolean refreshing = false;
    private String folderName = "INBOX";

    private InboxView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/InboxView.fxml"));
            loader.setController(this);
            loader.load();

            emailListView.setItems(messageObservableList);
            emailListView.setCellFactory(param -> new EmailCell());

            folderListView.setItems(folderObservableList);
            folderListView.setCellFactory(param -> new FolderCell());

            folderStatusLabel.setText("");
        } catch (Exception e) {
            System.out.println("Failed to load InboxView.fxml" + e.getMessage());
        }
    }

    public static void showFolder(Folder folder) {
        InboxView inboxView = getInstance();
        inboxView.fetchEmails(folder.getFullName());
    }

    @FXML
    public void fetchEmails() {
        fetchEmails(folderName);
    }

    /**
     * Fetches emails from the user's inbox and updates the view with the emails.
     */
    public void fetchEmails(String folder) {
        this.folderName = folder;

        buttonLeft.setDisable(true);
        pageLabel.setText("...");
        buttonReload.setDisable(true);

        FetchEmailsTask fetchEmailsTask = new FetchEmailsTask(folder);

        emailStatusLabel.textProperty().bind(fetchEmailsTask.messageProperty());

        fetchEmailsTask.setOnSucceeded(event -> {
            messageObservableList.clear();
            emailStatusLabel.textProperty().unbind();
            emailStatusLabel.setText("");

            Message[] messages = fetchEmailsTask.getValue();
            Message[] reversedMessages = new Message[messages.length];
            for (int i = 0; i < messages.length; i++) {
                reversedMessages[messages.length - i - 1] = messages[i];
            }
            this.messages = reversedMessages;

            currentPage = 0;
            refreshPage();

            buttonReload.setDisable(false);
        });

        fetchEmailsTask.setOnFailed(event -> {
            emailStatusLabel.textProperty().unbind();
            emailStatusLabel.setText("Failed to fetch emails.\n" + fetchEmailsTask.getException().getMessage());
            System.out.println("Failed to fetch emails: " + fetchEmailsTask.getException().getMessage());

            buttonReload.setDisable(false);
        });

        ExecutorService executorService = Executors.newFixedThreadPool(1);
        executorService.execute(fetchEmailsTask);
        executorService.shutdown();
    }

    private void fetchFolders() {
        GetFoldersTask task = new GetFoldersTask();

        folderStatusLabel.setText("Fetching folders...");

        task.setOnSucceeded(event -> {
            folderObservableList.clear();
            ArrayList<Folder> folders = task.getValue();

            folderObservableList.addAll(folders);

            folderStatusLabel.setText("");
        });

        task.setOnFailed(event -> {
            System.out.println("Failed to fetch folders: " + task.getException().getMessage());
            folderStatusLabel.setText("Failed to fetch folders.");
        });

        new Thread(task).start();
    }

    @Override
    public Pane getView() {
        return rootPane;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        fetchEmails("INBOX");
        fetchFolders();
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
        emailListView.scrollTo(0);
    }

    /**
     * Refreshes the page with emails from the current page number.
     */
    private void refreshPage() {
        System.out.println("Refreshing page " + currentPage);

        messageObservableList.clear();
        int firstIndex = currentPage * 50;
        int lastIndex = Math.min((currentPage + 1) * 50, messages.length);
        Message[] messages = Arrays.copyOfRange(this.messages, firstIndex, lastIndex);

        FetchProfile fetchProfile = new FetchProfile();
        fetchProfile.add(FetchProfile.Item.ENVELOPE);

        try {
            ImapManager.getCachedInbox(folderName).fetch(messages, fetchProfile);
            messageObservableList.addAll(messages);

            System.out.printf("Fetched %d messages\n", messages.length);
        } catch (MessagingException e) {
            System.out.println("Failed to fetch messages: " + e.getMessage());
        }

        updatePageLabel();
    }

    /**
     * Updates the page label with the current email range in the format "51 - 100 of 1323"
     */
    private void updatePageLabel() {
        pageLabel.setText(MessageFormat.format("{0} - {1} of {2}", currentPage * 50 + 1, Math.min((currentPage + 1) * 50, messages.length), messages.length));
    }

    public String getFolderName() {
        return folderName;
    }

    @FXML
    private void actionCompose(){
        AuthorView.show();
    }
}
