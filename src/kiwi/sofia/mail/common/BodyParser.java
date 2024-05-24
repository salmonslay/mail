package kiwi.sofia.mail.common;

import jakarta.mail.*;
import jakarta.mail.internet.MimeBodyPart;
import org.apache.commons.lang3.time.StopWatch;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
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
     * @return The body of the email and its content type.
     */
    public static Pair<String, String> parse(Object body, boolean getHtml) {
        if (body instanceof String) {
            return new Pair<>((String) body, "text/plain");
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
                    return new Pair<>(html.toString(), "text/html");
                } else if (!plainText.isEmpty()) {
                    System.out.printf("Returning plain text body, requested? %b\n", getHtml);
                    return new Pair<>(plainText.toString(), "text/plain");
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
     * @return The error message and its content type.
     */
    private static Pair<String, String> createError(String header, String body, boolean html) {
        if (html)
            return new Pair<>("<h1>" + header + "</h1>" + body, "text/html");
        else
            return new Pair<>(header + "\n" + body, "text/plain");
    }

    /**
     * Travels through the body of an email and saves any attachments to the specified path.
     *
     * @param body The body of the email.
     * @param path The path to save the attachments to.
     * @return a map of attachment cids and their corresponding file names, or null if no attachments were found.
     */
    public static Map<String, String> downloadAttachments(Object body, String path, Integer messageHashCode) {
        Map<String, String> attachments = new HashMap<>();
        if (!(body instanceof Multipart))
            return null;

        try {
            Multipart multipart = (Multipart) body;
            for (int i = 0; i < multipart.getCount(); i++) {
                BodyPart part = multipart.getBodyPart(i);
                String contentType = part.getContentType().toLowerCase();

                if (part.isMimeType("multipart/alternative") || part.isMimeType("multipart/related")) {
                    Map<String, String> map = downloadAttachments(part.getContent(), path, messageHashCode);
                    if (map != null)
                        attachments.putAll(map); // add all attachments from nested multipart

                } else if (contentType.contains("name=")) {
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
                            saveFile(part, part.getFileName(), path, messageHashCode, cid);

                            break;
                        }
                    }

                    // fallback if no cid is available
                    saveFile(part, part.getFileName(), path, messageHashCode, "no-cid" + i);
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
     * @param part     The attachment to save.
     * @param fileName The fileName of the attachment.
     * @param dirPath  The path to save the attachment to.
     */
    private static void saveFile(BodyPart part, String fileName, String dirPath, Integer messageHashCode, String cid) {
        try {
            Path path = Path.of(dirPath, fileName);
            ((MimeBodyPart) part).saveFile(path.toFile());

            Path tempPath = getTempPath(cid, messageHashCode);
            Files.createDirectories(tempPath.getParent());
            Files.copy(path, tempPath);
        } catch (IOException | MessagingException e) {
            System.out.println("Failed to save attachment: " + e.getMessage());
        }
    }

    /**
     * @param messageHashCode the hash code of a message
     * @return the temp directory of a message
     */
    public static Path getTempDir(Integer messageHashCode) {
        return Path.of(System.getProperty("java.io.tmpdir"), "sofmail", messageHashCode.toString());
    }

    /**
     * @param cid             the content id of the file
     * @param messageHashCode the hash code of the owning message
     * @return the temp path of a file
     */
    public static Path getTempPath(String cid, Integer messageHashCode) {
        return Path.of(getTempDir(messageHashCode).toString(), cid);
    }

    public static String extractPlainText(Object body) {
        return parse(body, false).getA();
    }

    public static String extractHtml(Object body) {
        return parse(body, true).getA();
    }

    private static int attachmentCount(Object body, int startFrom) {
        try {
            if (body instanceof Message)
                body = ((Message) body).getContent();

            Multipart multipart = (Multipart) body;
            for (int i = 0; i < multipart.getCount(); i++) {
                BodyPart part = multipart.getBodyPart(i);
                String contentType = part.getContentType().toLowerCase();
                if (part.isMimeType("multipart/alternative") || part.isMimeType("multipart/related"))
                    startFrom += attachmentCount(part.getContent(), startFrom);
                else if (contentType.contains("name="))
                    startFrom++;
            }
        } catch (Exception ignored) {
        }

        return startFrom;
    }

    /**
     * Checks if the message has any attachments, and if so returns the number of attachments.
     *
     * @param msg The message to check for attachments.
     * @return The number of attachments in the message.
     */
    public static int attachmentCount(Message msg) {
        return attachmentCount(msg, 0);
    }

    /**
     * Gets the hash code of the subject, date and recipients. Blocking if the content hasn't been loaded yet.
     *
     * @param msg The message to get the hash code of.
     * @return The hash code of the message content and sent date.
     */
    public static Integer getHashCode(Message msg) {
        try {
            return (msg.getSubject() + msg.getSentDate() + Arrays.toString(msg.getAllRecipients())).hashCode();
        } catch (MessagingException e) {
            System.out.println("Failed to get hash code: " + e.getMessage());
            return 0;
        }
    }
}
