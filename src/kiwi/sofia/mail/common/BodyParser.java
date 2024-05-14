package kiwi.sofia.mail.common;

import jakarta.mail.BodyPart;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
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

                    if (contentType.contains("text/plain") && !contentType.contains("name=")) { // ignore name= in content type to prevent displaying attachments
                        plainText.append(part.getContent());
                    } else if (contentType.contains("text/html") && !contentType.contains("name=")) {
                        html.append(part.getContent());
                    } else if (contentType.contains("multipart/alternative") || contentType.contains("multipart/related")) {
                        html.append(extractHtml(part.getContent())); // recursively extract HTML from nested multipart
                    } else {
                        System.out.println("Non-supported content type: " + contentType);
                    }
                }

                if (!html.isEmpty()) {
                    return html.toString();
                } else if (!plainText.isEmpty()) {
                    return plainText.toString();
                } else {
                    return "<h1>Failed to load email body</h1>";
                }

            } catch (MessagingException | IOException e) {
                return "<h1>Failed to load email body</h1>" + e.getMessage();
            }
        } else {
            return "<h1>Failed to load email body</h1>Type " + body.getClass().getName() + " is not supported.";
        }
    }

    public static String toPlainText(Object body) {
        String html = extractHtml(body);
        return Jsoup.parse(html).wholeText();

    }
}
