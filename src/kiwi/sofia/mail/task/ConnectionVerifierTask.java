package kiwi.sofia.mail.task;

import javafx.concurrent.Task;
import kiwi.sofia.mail.view.LoginView;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;
import java.util.prefs.Preferences;

public class ConnectionVerifierTask extends Task<Void> {

    @Override
    protected Void call() {
        if (verifySmtp() == null) {
            throw new RuntimeException("SMTP connection failed");
        }

        if (!verifyImap()) {
            throw new RuntimeException("IMAP connection failed");
        }

        updateMessage("Connections verified");

        return null;
    }

    /**
     * Checks if the SMTP credentials are correct by trying to connect to the SMTP server.
     *
     * @return the session if the SMTP connection was successful, null otherwise
     * @see <a href="https://stackoverflow.com/a/3060866/11420970/">Source</a>
     */
    private Session verifySmtp() {
        Preferences prefs = Preferences.userNodeForPackage(LoginView.class);
        String username = prefs.get("smtpUsername", "");
        String password = prefs.get("smtpPassword", "");
        String host = prefs.get("smtpHost", "smtp.gmail.com");
        int port = prefs.getInt("smtpPort", 465);

        updateMessage("Verifying SMTP connection...");

        try {
            Properties props = new Properties();
            props.put("mail.smtp.timeout", "5000");
            props.put("mail.smtp.connectiontimeout", "5000");

            if (port == 465)
                props.put("mail.smtp.ssl.enable", "true");
            else if (port == 587)
                props.put("mail.smtp.starttls.enable", "true");

            if (!password.isBlank())
                props.put("mail.smtp.auth", "true");

            props.put("mail.smtp.host", host);
            props.put("mail.smtp.port", port);

            Session session = Session.getInstance(props, !password.isBlank() ?
                    new Authenticator() {
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(username, password);
                        }
                    } : null); // null authenticator if no password is provided

            Transport transport = session.getTransport("smtp");
            transport.connect(username, password);
            transport.close();

            System.out.printf("SMTP connection to %s successful\n", host);
            return session;
        } catch (AuthenticationFailedException e) {
            updateMessage("SMTP authentication failed - " + e.getMessage());
        } catch (Exception e) {
            updateMessage("Error connecting to SMTP server");
        }
        return null;
    }

    /**
     * Checks if the IMAP credentials are correct by trying to connect to the IMAP server.
     * Since Store is an AutoCloseable, we can't return it directly.
     *
     * @return true if the IMAP connection was successful, false otherwise
     */
    private boolean verifyImap() {
        Preferences prefs = Preferences.userNodeForPackage(LoginView.class);
        String username = prefs.get("imapUsername", "");
        String password = prefs.get("imapPassword", "");
        String host = prefs.get("imapHost", "imap.gmail.com");
        int port = prefs.getInt("imapPort", 993);

        updateMessage("Verifying IMAP connection...");

        try {
            Properties properties = new Properties();
            properties.put("mail.imap.host", host);
            properties.put("mail.imap.port", port);
            properties.put("mail.imap.ssl.enable", "true");

            Session session = Session.getInstance(properties);
            Store store = session.getStore("imap");
            store.connect(username, password);
            store.close();

            System.out.printf("IMAP connection to %s successful\n", host);
            return true;
        } catch (Exception e) {
            updateMessage("Error connecting to IMAP server");
        }
        return false;
    }
}
