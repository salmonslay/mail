package kiwi.sofia.mail.task;

import javafx.concurrent.Task;
import kiwi.sofia.mail.view.LoginView;

import javax.mail.*;
import java.util.Properties;
import java.util.prefs.Preferences;

public class ConnectionVerifierTask extends Task<Void> {

    @Override
    protected Void call() {
        if (!verifySmtp()) {
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
     * @return true if the SMTP connection was successful, false otherwise
     * @see <a href="https://stackoverflow.com/a/3060866/11420970/">Source</a>
     */
    private boolean verifySmtp() {
        Preferences prefs = Preferences.userNodeForPackage(LoginView.class);
        updateMessage("Verifying SMTP connection...");

        try {
            Properties props = new Properties();

            props.put("mail.smtp.timeout", "5000");
            props.put("mail.smtp.connectiontimeout", "5000");
            props.put("mail.smtp.ssl.enable", "true");
            props.put("mail.smtp.auth", "true");

            props.put("mail.smtp.host", prefs.get("smtpHost", "smtp.gmail.com"));
            props.put("mail.smtp.port", prefs.getInt("smtpPort", 465));

            Session session = Session.getInstance(props, null);
            Transport transport = session.getTransport("smtp");
            transport.connect(prefs.get("smtpUsername", ""), prefs.get("smtpPassword", ""));
            transport.close();

            System.out.printf("SMTP connection to %s successful\n", prefs.get("smtpHost", "smtp.gmail.com"));
            return true;
        } catch (AuthenticationFailedException e) {
            updateMessage("SMTP authentication failed - incorrect username or password");
        } catch (Exception e) {
            updateMessage("Error connecting to SMTP server");
        }
        return false;
    }

    /**
     * Checks if the IMAP credentials are correct by trying to connect to the IMAP server.
     *
     * @return true if the IMAP connection was successful, false otherwise
     */
    private boolean verifyImap() {
        Preferences prefs = Preferences.userNodeForPackage(LoginView.class);
        updateMessage("Verifying IMAP connection...");

        try {
            Properties properties = new Properties();
            properties.put("mail.imap.host", prefs.get("imapHost", "imap.gmail.com"));
            properties.put("mail.imap.port", prefs.getInt("imapPort", 993));
            properties.put("mail.imap.ssl.enable", "true");

            Session session = Session.getInstance(properties);
            Store store = session.getStore("imap");
            store.connect(prefs.get("imapUsername", ""), prefs.get("imapPassword", ""));
            store.close();

            System.out.printf("IMAP connection to %s successful\n", prefs.get("imapHost", "imap.gmail.com"));
            return true;
        } catch (Exception e) {
            updateMessage("Error connecting to IMAP server");
        }
        return false;
    }
}
