package kiwi.sofia.mail.template;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.GridPane;
import kiwi.sofia.mail.view.ClientView;
import kiwi.sofia.mail.view.EmailView;
import org.apache.commons.lang3.time.DateUtils;

import java.text.DateFormat;
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
    private GridPane gridPane;
    private FXMLLoader loader;
    private Message message;

    @Override
    protected void updateItem(Message message, boolean empty) {
        super.updateItem(message, empty);

        if (empty) return;

        if (loader == null) {
            loader = new FXMLLoader(getClass().getResource("/fxml/EmailCell.fxml"));
            loader.setController(this);

            try {
                loader.load();
            } catch (Exception ignored) {
            }
        }
        try {
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

            setGraphic(gridPane);
            setText(null);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @FXML
    protected void openEmail() {
        try {
            System.out.printf("Opening email with subject: %s\n", message.getSubject());

            EmailView.show(message);
        } catch (MessagingException e) {
            System.out.println(e.getMessage());
        }
    }
}
