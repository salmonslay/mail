package kiwi.sofia.mail.view;

import jakarta.mail.Message;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.web.WebView;
import javafx.stage.DirectoryChooser;
import kiwi.sofia.mail.common.BodyParser;
import kiwi.sofia.mail.template.EmailCell;

import java.io.File;
import java.util.prefs.Preferences;

public class EmailView implements SofView {
    private final Pane rootPane;
    private final Message message;
    private Object content;

    @FXML
    private Button replyAllButton;
    @FXML
    private Button attachmentsButton;
    @FXML
    private WebView webView;
    @FXML
    private Label subjectLabel;

    public EmailView(Message message) {
        rootPane = new GridPane();
        this.message = message;

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/EmailView.fxml"));
            loader.setController(this);
            loader.load();

            subjectLabel.setText(message.getSubject());
            content = message.getContent();
            setBody(content);

            rootPane.getChildren().add(loader.getRoot());
        } catch (Exception e) {
            System.out.println("Failed to load InboxView.fxml" + e.getMessage());
        }
    }

    private void setBody(Object body) {
        webView.getEngine().loadContent(BodyParser.extractHtml(body));
    }

    @Override
    public Pane getView() {
        return rootPane;
    }

    @FXML
    public void actionGoBackToInbox() {
        ClientView.showInbox();
    }

    @FXML
    public void actionReply() {
        System.out.println("Replying to email");
    }

    @FXML
    public void actionForward() {
        System.out.println("Forwarding email");
    }

    @FXML
    public void actionReplyAll() {
        System.out.println("Replying to all");
    }

    @FXML
    public void actionDownloadAttachments() {
        Preferences prefs = Preferences.userNodeForPackage(EmailView.class);
        String lastPath = prefs.get("lastPath", System.getProperty("user.home"));

        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setInitialDirectory(new java.io.File(lastPath));
        directoryChooser.setTitle("Select directory to save attachments");
        File window = directoryChooser.showDialog(rootPane.getScene().getWindow());

        if (window == null)
            return;

        String path = window.getAbsolutePath();
        prefs.put("lastPath", path);

        BodyParser.saveAttachments(content, path);
    }
}
