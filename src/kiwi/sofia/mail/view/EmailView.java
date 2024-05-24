package kiwi.sofia.mail.view;

import com.sun.mail.imap.IMAPFolder;
import jakarta.mail.Flags;
import jakarta.mail.Folder;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.print.PrinterJob;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.web.WebView;
import javafx.stage.DirectoryChooser;
import kiwi.sofia.mail.common.*;
import kiwi.sofia.mail.task.DownloadAttachmentsTask;
import kiwi.sofia.mail.task.GetEmailBodyTask;
import kiwi.sofia.mail.task.SetNonFetchedEmailValuesTask;
import org.apache.commons.lang3.time.StopWatch;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.prefs.Preferences;

/**
 * A view for displaying an email. Contains information about the email, the email content, and buttons for replying,
 * forwarding, and downloading attachments.
 */
public class EmailView implements SofView {
    private final Pane rootPane;
    private static EmailView instance;
    private Message message;
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
    private String html;
    @FXML
    private Label statusLabel;
    @FXML
    private FontIcon starIcon;

    private EmailView() {
        rootPane = new GridPane();

        try {
            // Load in the view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/EmailView.fxml"));
            loader.setController(this);
            loader.load();

            rootPane.getChildren().add(loader.getRoot());

            System.out.println("EmailView ctor loaded");
        } catch (Exception e) {
            System.out.println("Failed to load EmailView.fxml: " + e.getMessage());
        }
    }

    /**
     * Loads all content, sets the labels and the circle.
     */
    public void initialize() {
        System.out.println("EmailView initialize");
        webView.getEngine().loadContent("");

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        // Email content task
        GetEmailBodyTask task = new GetEmailBodyTask(message);
        task.onSucceededProperty().set(event -> {
            statusLabel.setText("");

            setBody(task.getValue());
            displayAttachments();

            System.out.printf("Email content loaded in %d ms%n", stopWatch.getTime());
        });

        task.onFailedProperty().set(event -> {
            statusLabel.setText("Failed to load email content");
        });

        new Thread(task).start();

        // Attachments, to-text & reply all. We disable them to prevent user interaction before the values are set
        toLabel.setText("to: ");
        attachmentsButton.setDisable(true);
        replyAllButton.setDisable(true);
        SetNonFetchedEmailValuesTask buttonsTask = new SetNonFetchedEmailValuesTask(message, attachmentsButton, replyAllButton, toLabel);
        new Thread(buttonsTask).start();

        try {
            // Set the name, email and subject labels & tooltips
            String regex = "\"?(.+?)\"? (<.+>)"; // Matches "Name" <email>, without quotes including angle brackets
            String from = message.getFrom()[0].toString();
            String name = from.replaceAll(regex, "$1"); // name (without surrounding quotes)
            String email = from.replaceAll(regex, "$2"); // <email>

            senderLabel.setText(name);
            senderLabel.setTooltip(new Tooltip(name));

            emailLabel.setText(email);
            emailLabel.setTooltip(new Tooltip(email));

            subjectLabel.setText(message.getSubject());
            subjectLabel.setTooltip(new Tooltip(message.getSubject()));

            // Parse dates
            Locale locale = new Locale("en", "US");
            String pattern = "MMM d, yyyy 'at' K:mm a"; // Mar 15, 2021 at 9:30 AM
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern, locale);
            dateLabel.setText(simpleDateFormat.format(message.getSentDate()));

            if (message.getFlags().contains(Flags.Flag.FLAGGED)) {
                starIcon.setIconLiteral("fa-star");
                starIcon.setIconColor(Constants.starIconPaint);
            }

            setCircle(from);
        } catch (Exception e) {
            System.out.println("Failed to set labels: " + e.getMessage());
        }
    }

    /**
     * Sets the web view to the body of the email. Prioritizes HTML over plain text.
     *
     * @param body A pair of the body & its mime type of the email
     */
    private void setBody(Pair<String, String> body) {
        html = body.getA();
        if (body.getB().equalsIgnoreCase("text/plain")) // Fix new lines if we're displaying plain text
            html = html.replaceAll("\n", "<br>");

        webView.getEngine().loadContent(html);
    }

    @Override
    public Pane getView() {
        return rootPane;
    }

    @FXML
    private void actionGoBackToInbox() {
        ClientView.showInbox();
    }

    @FXML
    private void actionReply() {
        AuthorView.show(message, AuthorMode.REPLY);
    }

    @FXML
    private void actionReplyAll() {
        AuthorView.show(message, AuthorMode.REPLY_ALL);
    }

    @FXML
    private void actionForward() {
        AuthorView.show(message, AuthorMode.FORWARD);
    }

    @FXML
    private void actionMoveEmail() {
        try {
            IMAPFolder folder = (IMAPFolder) message.getFolder();
            Folder dest = ImapManager.getCachedStore().getFolder("Outlook");

            folder.open(Folder.READ_WRITE);
            dest.open(Folder.READ_WRITE);
            folder.moveMessages(new Message[]{message}, dest);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void actionPrintEmail() {
        System.out.println("Creating print job");
        PrinterJob job = PrinterJob.createPrinterJob();
        if (job != null) {
            job.showPrintDialog(rootPane.getScene().getWindow());
            webView.getEngine().print(job);
            job.endJob();
        }
    }

    @FXML
    private void actionStarEmail() {
        boolean isStarred = starIcon.getIconLiteral().equals("fa-star");
        starIcon.setIconLiteral(isStarred ? "fa-star-o" : "fa-star");
        starIcon.setIconColor(isStarred ? Paint.valueOf("#000000") : Constants.starIconPaint);

        MessageActions.flagMessage(message, Flags.Flag.FLAGGED, !isStarred);
    }

    @FXML
    private void actionTrashEmail() {
        MessageActions.askAndTrashMessage(message);
    }

    @FXML
    private void actionUnreadEmail() {
        MessageActions.flagMessage(message, Flags.Flag.SEEN, false);
        ClientView.showInbox();
    }

    /**
     * Downloads the attachments of the email and opens the directory where they are saved.
     */
    @FXML
    private void actionDownloadAttachments() {
        Preferences prefs = Preferences.userNodeForPackage(EmailView.class);
        String lastPath = prefs.get("lastPath", System.getProperty("user.home"));

        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setInitialDirectory(new File(lastPath));
        directoryChooser.setTitle("Select directory to save attachments");
        File window = directoryChooser.showDialog(rootPane.getScene().getWindow());

        if (window == null) return; // return if the user cancels the dialog

        String path = window.getAbsolutePath();
        prefs.put("lastPath", path);

        DownloadAttachmentsTask task = new DownloadAttachmentsTask(message, path, BodyParser.getHashCode(message));
        task.onSucceededProperty().set(event -> {
            if (task.getValue() == null) return;

            // open the folder in explorer when the attachments are downloaded
            try {
                Runtime.getRuntime().exec("explorer.exe /open," + path);
            } catch (IOException e) {
                System.out.println("Failed to open directory: " + e.getMessage());
            }

            displayAttachments(); // they're now downloaded and can be displayed
        });

        new Thread(task).start();
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

    /**
     * Forces the client to display an email view with the specified message
     *
     * @param message The message to display
     */
    public static void show(Message message) {
        EmailView instance = getInstance();
        instance.message = message;
        instance.initialize();
        ClientView.setCenter(instance.getView());
    }

    /**
     * Replaces attachment CIDs with a local path to the attachment, letting them be displayed.
     * For this to work the attachments need to be saved to a temporary directory beforehand.
     */
    private void displayAttachments() {
        String regex = "src=\"cid:(.+?)\"";
        String tempPath = BodyParser.getTempDir(BodyParser.getHashCode(message)).toString().replaceAll("\\\\", "/"); // Replace backslashes with forward slashes
        String replacement = "src=\"file://" + tempPath + "/$1\""; // Set the src attribute to the CID
        html = html.replaceAll(regex, replacement) + " ";

        webView.getEngine().loadContent(html);
    }

    private static EmailView getInstance() {
        if (instance == null) {
            instance = new EmailView();
        }
        return instance;
    }
}
