package kiwi.sofia.mail.task;

import jakarta.mail.*;
import javafx.concurrent.Task;
import kiwi.sofia.mail.common.*;

public class ConnectionVerifierTask extends Task<Void> {

    @Override
    protected Void call() {
        if (!verifySmtp()) {
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
     * @return true if the SMTP connection was successful, false otherwise
     */
    private boolean verifySmtp() {
        ConnectionRecord set = ConnectionRecord.getSmtpConnectionSet();

        updateMessage("Verifying SMTP connection...");

        Pair<Transport, Exception> result = SmtpManager.getTransportExc();
        if (result.getB() != null) {
            updateMessage("Error connecting to SMTP server: " + result.getB().getMessage());
            return false;
        }

        return true;
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
