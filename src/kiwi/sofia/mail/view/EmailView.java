package kiwi.sofia.mail.view;

import jakarta.mail.Address;
import jakarta.mail.Message;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.web.WebView;
import javafx.stage.DirectoryChooser;
import kiwi.sofia.mail.common.BodyParser;
import kiwi.sofia.mail.task.DownloadAttachmentsTask;

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
    @FXML
    private Label emailLabel;
    @FXML
    private Label senderLabel;
    @FXML
    private Label dateLabel;
    @FXML
    private Label firstLetterLabel;
    @FXML
    private Label toLabel;
    @FXML
    private Circle circle;

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

            attachmentsButton.setDisable(BodyParser.attachmentCount(message) == 0);

            String regex = "(.+) (<.+>)";
            String from = message.getFrom()[0].toString();
            senderLabel.setText(from.replaceAll(regex, "$1"));
            emailLabel.setText(from.replaceAll(regex, "$2"));
            dateLabel.setText(message.getSentDate().toString());

            setCircle(from);

            String toText = getToText();
            toLabel.setText(toText);
            toLabel.setTooltip(new Tooltip(toText));

            rootPane.getChildren().add(loader.getRoot());
        } catch (Exception e) {
            System.out.println("Failed to load EmailView.fxml: " + e.getMessage());
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
        AuthorView.show();
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
        directoryChooser.setInitialDirectory(new File(lastPath));
        directoryChooser.setTitle("Select directory to save attachments");
        File window = directoryChooser.showDialog(rootPane.getScene().getWindow());

        if (window == null) return;

        String path = window.getAbsolutePath();
        prefs.put("lastPath", path);

        new Thread(new DownloadAttachmentsTask(content, path)).start();
    }

    /**
     * Sets the circle with the first letter of the sender, together with a color based on the letter.
     *
     * @param from The sender of the email
     */
    private void setCircle(String from) {
        char firstChar = ' ';
        for (char c : from.toCharArray()) {
            if (c != ' ' && c != '"') {
                firstChar = c;
                break;
            }
        }
        firstLetterLabel.setText((firstChar + "").toUpperCase());

        String[] colors = new String[]{"#f44336", "#e91e63", "#9c27b0", "#673ab7", "#3f51b5", "#2196f3", "#03a9f4", "#00bcd4", "#009688", "#4caf50", "#8bc34a", "#cddc39", "#ffeb3b", "#ffc107", "#ff9800", "#ff5722", "#795548", "#9e9e9e", "#607d8b"};
        int index = (int) firstChar % colors.length;
        circle.setStyle("-fx-fill: " + colors[index]);
    }

    public static void show(Message message) {
        ClientView.setCenter(new EmailView(message).getView());
    }

    private String getToText() {
        try {
            Address[] recipients = message.getAllRecipients();
            if (recipients != null && recipients.length > 0) {
                String to = "to: ";
                for (int i = 0; i < recipients.length; i++) {
                    Address recipient = recipients[i];
                    to += recipient;
                    if (i < recipients.length - 1) to += ", ";
                }
                return to;
            }
        } catch (Exception ignored) {
        }
        return "to me";
    }
}
