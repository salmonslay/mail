package kiwi.sofia.mail.template;

import jakarta.mail.Flags;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import kiwi.sofia.mail.common.AuthorMode;
import kiwi.sofia.mail.common.Constants;
import kiwi.sofia.mail.common.MessageActions;
import kiwi.sofia.mail.view.AuthorView;
import kiwi.sofia.mail.view.EmailView;
import kiwi.sofia.mail.view.InboxView;
import org.apache.commons.lang3.time.DateUtils;
import org.kordamp.ikonli.javafx.FontIcon;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * A clickable cell in the email list. Opens the email when clicked.
 */
@SuppressWarnings("deprecation") // getYear, whatever
public class EmailCell extends ListCell<Message> {
    @FXML
    private Label subjectLabel;
    @FXML
    private Label fromLabel;
    @FXML
    private Label dateLabel;
    @FXML
    private Pane rootPane;
    @FXML
    private FontIcon deleteIcon;
    @FXML
    private FontIcon starIcon;

    /**
     * The email message of this cell.
     */
    private Message message;

    private static Font unreadFont;

    @Override
    protected void updateItem(Message message, boolean empty) {
        super.updateItem(message, empty);

        if (empty) {
            setGraphic(null); // clear this cell if empty
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/EmailCell.fxml"));
            loader.setController(this);
            loader.load();

            this.message = message;

            subjectLabel.setText(message.getSubject());

            // Display name as sender if possible, fallback to email address
            String fromPattern = "\"?(.+?)\"? (<.+>)";
            String from = message.getFrom()[0].toString();
            if (from.matches(fromPattern)) {
                from = from.replaceAll(fromPattern, "$1");
            }
            fromLabel.setText(from);

            // Display date as 9:30 AM if today, or Mar 15 if not.
            Date date = message.getSentDate();
            Locale locale = new Locale("en", "US");
            String pattern = DateUtils.isSameDay(new Date(), date) ? "K:mm a" : "MMM d";

            // We're displaying year if not this year
            if (date.getYear() != new Date().getYear())
                pattern += ", yyyy";

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern, locale);
            dateLabel.setText(simpleDateFormat.format(date));

            Flags flags = message.getFlags();
            if (!flags.contains(Flags.Flag.SEEN)) {
                if (unreadFont == null) {
                    Font oldFont = fromLabel.getFont();
                    unreadFont = Font.font(oldFont.getFamily(), FontWeight.BOLD, oldFont.getSize()); // keep size, change style
                }
                fromLabel.setFont(unreadFont);
                subjectLabel.setFont(unreadFont);
                dateLabel.setFont(unreadFont);
            }

            if (flags.contains(Flags.Flag.FLAGGED)) {
                starIcon.setIconLiteral("fa-star");
                starIcon.setIconColor(Constants.starIconPaint);
            }

            setGraphic(rootPane);
            setText(null);
        } catch (Exception e) {
            System.out.println("Failed to load EmailCell.fxml: " + e.getMessage());
        }
    }

    /**
     * Open this email in the appropriate view (AuthorView for drafts, EmailView for received emails).
     */
    @FXML
    protected void openEmail() {
        boolean isDraft = InboxView.getInstance().getFolderName().contains("Drafts");
        try {
            System.out.printf("Opening %s with subject: %s\n", isDraft ? "draft" : "email", message.getSubject());

            if (isDraft)
                AuthorView.show(message, AuthorMode.EDIT);
            else
                EmailView.show(message);

            InboxView.getInstance().fetchEmails();
        } catch (MessagingException e) {
            System.out.printf("Failed to open %s: %s\n", isDraft ? "draft" : "email", e.getMessage());
        }
    }

    /**
     * Asks the user if they want to delete this message.
     */
    @FXML
    private void actionDelete() {
        MessageActions.askAndTrashMessage(message);
    }

    @FXML
    private void actionStar() {
        // Filled & gold star for starred, otherwise empty & black
        // We set it before we actually star the email to give immediate feedback
        boolean isStarred = starIcon.getIconLiteral().equals("fa-star");
        starIcon.setIconLiteral(isStarred ? "fa-star-o" : "fa-star");
        starIcon.setIconColor(isStarred ? Paint.valueOf("#000000") : Constants.starIconPaint);

        MessageActions.starMessage(message, isStarred);
    }
}
