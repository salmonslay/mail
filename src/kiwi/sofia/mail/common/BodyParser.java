package kiwi.sofia.mail.common;

import jakarta.mail.BodyPart;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.internet.MimeBodyPart;
import org.jsoup.Jsoup;

import java.io.IOException;

public class BodyParser {

    /**
     * Converts the body of an email to HTML. Blocking if the content hasn't been loaded yet.
     *
     * @param body The body of the email.
     * @return The body of the email as HTML.
     */
    public static String extractHtml(Object body) {
        if (body instanceof String) {
            return (String) body;
        } else if (body instanceof Multipart) {
            StringBuilder plainText = new StringBuilder();
            StringBuilder html = new StringBuilder();
            try {
                Multipart multipart = (Multipart) body;
                System.out.printf("Reading %d parts\n", multipart.getCount());

                for (int i = 0; i < multipart.getCount(); i++) {
                    BodyPart part = multipart.getBodyPart(i);
                    String contentType = part.getContentType().toLowerCase();
                    System.out.printf("Part %d: %s\n", i, contentType);

                    if (part.isMimeType("text/plain") && !contentType.contains("name=")) { // ignore name= in content type to prevent displaying attachments
                        plainText.append(part.getContent());
                    } else if (part.isMimeType("text/html") && !contentType.contains("name=")) {
                        html.append(part.getContent());
                    } else if (part.isMimeType("multipart/alternative") || part.isMimeType("multipart/related")) {
                        html.append(extractHtml(part.getContent())); // recursively extract HTML from nested multipart
                    } else {
                        System.out.println("Non-supported content type: " + contentType);
                    }
                }

                if (!html.isEmpty()) {
                    System.out.println("HTML body found");
                    return html.toString();
                } else if (!plainText.isEmpty()) {
                    System.out.println("Plain text body found");
                    return plainText.toString();
                } else {
                    System.out.println("No body found");
                    return "<h1>Failed to load email body</h1>";
                }

            } catch (MessagingException | IOException e) {
                return "<h1>Failed to load email body</h1>" + e.getMessage();
            }
        } else {
            return "<h1>Failed to load email body</h1>Type " + body.getClass().getName() + " is not supported.";
        }
    }

    /**
     * Travels through the body of an email and saves any attachments to the specified path.
     *
     * @param body The body of the email.
     * @param path The path to save the attachments to.
     * @return true if attachments were saved successfully, false otherwise
     */
    public static boolean downloadAttachments(Object body, String path) {
        if (!(body instanceof Multipart))
            return false;

        try {
            Multipart multipart = (Multipart) body;
            for (int i = 0; i < multipart.getCount(); i++) {
                BodyPart part = multipart.getBodyPart(i);
                String contentType = part.getContentType().toLowerCase();
                if (part.isMimeType("multipart/alternative") || part.isMimeType("multipart/related")) {
                    downloadAttachments(part.getContent(), path);
                } else if (contentType.contains("name=")) {
                    System.out.printf("Saving attachment %s\n", part.getFileName());
                    saveFile(part, part.getFileName(), path);
                }
            }
            return true;
        } catch (MessagingException | IOException e) {
            System.out.println("Failed to save attachments: " + e.getMessage());
            return false;
        }
    }

    /**
     * Saves an attachment to the specified path.
     *
     * @param part The attachment to save.
     * @param name The name of the attachment.
     * @param path The path to save the attachment to.
     */
    private static void saveFile(BodyPart part, String name, String path) {
        try {
            ((MimeBodyPart) part).saveFile(path + "/" + name);
            System.out.printf("Saved attachment %s\n", name);
        } catch (IOException | MessagingException e) {
            System.out.println("Failed to save attachment: " + e.getMessage());
        }
    }

    public static String toPlainText(Object body) {
        String html = extractHtml(body);
        return Jsoup.parse(html).wholeText();
    }

    /**
     * Checks if the message has any attachments, and if so returns the number of attachments.
     *
     * @param msg The message to check for attachments.
     * @return The number of attachments in the message.
     * @see <a href="https://javaee.github.io/javamail/FAQ#hasattach">Source</a>
     */
    public static int attachmentCount(Message msg) {
        try {
            if (msg.isMimeType("multipart/mixed") || msg.isMimeType("multipart/related")) {
                Multipart mp = (Multipart) msg.getContent();
                return mp.getCount();
            }
        } catch (MessagingException | IOException e) {
            System.out.println("Failed to check for attachments: " + e.getMessage());
        }
        return 0;
    }
}
