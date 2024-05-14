package kiwi.sofia.mail.common;

import jakarta.mail.Multipart;
import org.jsoup.Jsoup;

public class BodyParser {

    /**
     * Converts the body of an email to HTML. Blocking if the content hasn't been loaded yet.
     *
     * @param body The body of the email.
     * @return The body of the email as HTML.
     */
    public static String toHtml(Object body) {
        if (body instanceof String) {
            return (String) body;
        } else if (body instanceof Multipart) {
            try {
                Multipart multipart = (Multipart) body;
                StringBuilder content = new StringBuilder();
                for (int i = 0; i < multipart.getCount(); i++) {
                    content.append(multipart.getBodyPart(i).getContent());
                }
                return content.toString();
            } catch (Exception e) {
                return "<h1>Failed to load email body</h1>" + e.getMessage();
            }
        } else {
            return "<h1>Failed to load email body</h1>Type " + body.getClass().getName() + " is not supported.";
        }
    }

    public static String toPlainText(Object body) {
        String html = toHtml(body);
        return Jsoup.parse(html).wholeText();

    }
}
