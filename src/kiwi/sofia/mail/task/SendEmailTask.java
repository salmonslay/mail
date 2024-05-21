package kiwi.sofia.mail.task;

import jakarta.mail.BodyPart;
import jakarta.mail.Message;
import jakarta.mail.Multipart;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import javafx.concurrent.Task;
import kiwi.sofia.mail.common.ConnectionRecord;
import kiwi.sofia.mail.common.SmtpManager;
import kiwi.sofia.mail.view.InboxView;
import org.jsoup.Jsoup;

import java.io.File;
import java.util.List;

/**
 * Task to construct and send a mime email.
 */
public class SendEmailTask extends Task<MimeMessage> {
    private final String addresses;
    private final String subject;
    private final String body;
    private final List<File> attachments;

    public SendEmailTask(String addresses, String subject, String body, List<File> attachments) {
        this.addresses = addresses;
        this.subject = subject;
        this.body = body;
        this.attachments = attachments;
    }

    @Override
    protected MimeMessage call() {
        try {
            updateMessage("Sending message...");
            ConnectionRecord set = ConnectionRecord.getSmtpConnectionSet();

            // Create new message
            MimeMessage message = new MimeMessage(SmtpManager.getCachedSession());
            message.setFrom(new InternetAddress(set.username(), set.displayName()));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(addresses));
            message.setSubject(subject);

            BodyPart htmlBody = new MimeBodyPart();
            String html = body.replaceAll("contenteditable=\"true\"", ""); // remove contenteditable attribute
            htmlBody.setContent(html, "text/html");

            BodyPart plainBody = new MimeBodyPart();
            String plain = Jsoup.parse(body).wholeText(); // parse html to plain text
            plainBody.setContent(plain, "text/plain");

            Multipart multipart = new MimeMultipart("alternative");
            multipart.addBodyPart(plainBody);
            multipart.addBodyPart(htmlBody); // most important part last - plain is only fallback

            for (File file : attachments) {
                MimeBodyPart attachment = new MimeBodyPart();
                attachment.attachFile(file);
                multipart.addBodyPart(attachment);
            }

            message.setContent(multipart);

            Transport.send(message);
            updateMessage("Email sent successfully");
            InboxView.getInstance().fetchEmails(); // refresh the inbox
            return message;
        } catch (Exception e) {
            updateMessage("Failed to send email: " + e.getMessage());
            return null;
        }
    }
}
