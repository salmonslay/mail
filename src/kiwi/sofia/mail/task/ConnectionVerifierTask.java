package kiwi.sofia.mail.task;

import jakarta.mail.*;
import javafx.concurrent.Task;
import kiwi.sofia.mail.common.ConnectionRecord;
import kiwi.sofia.mail.common.ImapManager;
import kiwi.sofia.mail.common.Pair;
import kiwi.sofia.mail.common.PropertiesCreator;

import java.util.Properties;

public class ConnectionVerifierTask extends Task<Void> {

    @Override
    protected Void call() {
        if (verifySmtp() == null) {
            throw new RuntimeException("SMTP connection failed");
        }

        // TODO: don't wait for the inbox, only wait for the connection
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
        ConnectionRecord set = ConnectionRecord.getSmtpConnectionSet();

        updateMessage("Verifying SMTP connection...");

        try {
            Properties props = PropertiesCreator.createSmtpProperties();

            Session session = Session.getInstance(props, !set.password().isBlank() ?
                    new Authenticator() {
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(set.username(), set.password());
                        }
                    } : null); // null authenticator if no password is provided

            Transport transport = session.getTransport("smtp");
            transport.connect(set.username(), set.password());
            transport.close();

            System.out.printf("SMTP connection to %s successful\n", set.host());
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
        Pair<Store, Exception> result = ImapManager.getStoreExc();
        if (result.getB() != null) {
            updateMessage("Error connecting to IMAP server: " + result.getB().getMessage());
            return false;
        }

        return true;
    }
}
