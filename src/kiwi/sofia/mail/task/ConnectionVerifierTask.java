package kiwi.sofia.mail.task;

import javafx.concurrent.Task;
import kiwi.sofia.mail.common.ImapManager;
import kiwi.sofia.mail.common.Pair;
import kiwi.sofia.mail.view.LoginView;

import javax.mail.*;
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
            updateMessage("Error connecting to SMTP server: " + e.getMessage());
        }
        return null;
    }

    /**
     * Checks if the IMAP credentials are correct by trying to connect to the IMAP server.
     *
     * @return true if the IMAP connection was successful, false otherwise
     */
    private boolean verifyImap() {
        updateMessage("Verifying IMAP connection...");
        Pair<Folder, Exception> result = ImapManager.getInboxExc();
        if (result.getB() != null) {
            updateMessage("Error connecting to IMAP server: " + result.getB().getMessage());
            return false;
        }

        return true;
    }
}
