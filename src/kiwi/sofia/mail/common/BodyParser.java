package kiwi.sofia.mail.common;

import jakarta.mail.*;
import jakarta.mail.internet.MimeBodyPart;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * Helper functions to parse the body of an email.
 */
public class BodyParser {

    /**
     * Converts the body of an email to HTML or plain text. Blocking if the content hasn't been loaded yet.
     *
     * @param body    The body of the email.
     * @param getHtml Whether to return the body as HTML or plain text.
     * @return The body of the email.
     */
    public static String parse(Object body, boolean getHtml) {
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

                if (!html.isEmpty() && getHtml) {
                    System.out.println("Returning requested and found HTML body");
                    return html.toString();
                } else if (!plainText.isEmpty()) {
                    System.out.printf("Returning plain text body, requested? %b\n", getHtml);
                    return plainText.toString();
                } else {
                    System.out.println("No body found");

                    return createError("Failed to load email body", "", getHtml);
                }

            } catch (MessagingException | IOException e) {
                return createError("Failed to load email body", e.getMessage(), getHtml);
            }
        } else {
            return createError("Failed to load email body", "Type " + body.getClass().getName() + " is not supported.", getHtml);
        }
    }

    /**
     * Creates an error message.
     *
     * @param header The header of the error message.
     * @param body   The body of the error message.
     * @param html   Whether to return the error message as HTML or plain text.
     * @return The error message.
     */
    private static String createError(String header, String body, boolean html) {
        if (html)
            return "<h1>" + header + "</h1>" + body;
        else
            return header + "\n" + body;
    }

    /**
     * Travels through the body of an email and saves any attachments to the specified path.
     *
     * @param body The body of the email.
     * @param path The path to save the attachments to.
     * @return a map of attachment cids and their corresponding file names, or null if no attachments were found.
     */
    public static Map<String, String> downloadAttachments(Object body, String path) {
        Map<String, String> attachments = new HashMap<>();
        if (!(body instanceof Multipart))
            return null;

        try {
            Multipart multipart = (Multipart) body;
            for (int i = 0; i < multipart.getCount(); i++) {
                BodyPart part = multipart.getBodyPart(i);
                String contentType = part.getContentType().toLowerCase();
                if (part.isMimeType("multipart/alternative") || part.isMimeType("multipart/related")) {
                    downloadAttachments(part.getContent(), path);
                } else if (contentType.contains("name=")) {
                    saveFile(part, part.getFileName(), path);

                    // Save file name from content ID if available
                    Enumeration<Header> headers = part.getAllHeaders();
                    while (headers.hasMoreElements()) {
                        Header header = headers.nextElement();
                        if (header.getName().equals("Content-ID") || header.getName().equals("X-Attachment-Id")) {
                            String regex = "<(.+?)>";
                            String cid = header.getValue().replaceAll(regex, "$1");
                            String fileName = part.getFileName();

                            attachments.put(cid, fileName);
                            System.out.printf("Saving attachment %s, CID %s\n", fileName, cid);
                        }
                    }
                }
            }
            return attachments;
        } catch (MessagingException | IOException e) {
            System.out.println("Failed to save attachments: " + e.getMessage());
            return null;
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

    public static String extractPlainText(Object body) {
        return parse(body, false);
    }

    public static String extractHtml(Object body) {
        return parse(body, true);
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
