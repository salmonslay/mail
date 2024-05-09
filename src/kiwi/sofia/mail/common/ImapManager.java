package kiwi.sofia.mail.common;

import jakarta.mail.Folder;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.Store;

import java.util.Properties;

public class ImapManager {
    private static Folder inbox;

    /**
     * Gets the user's inbox folder through IMAP. Blocking.
     *
     * @return a pair of the inbox folder and an exception if an error occurred
     */
    public static Pair<Folder, Exception> getInboxExc() {
        System.out.println("Getting inbox");

        ConnectionRecord set = ConnectionRecord.getImapConnectionSet();

        try {
            Properties props = PropertiesCreator.createImapProperties();
            Session session = Session.getInstance(props);
            Store store = session.getStore("imap");
            store.connect(set.username(), set.password());

            System.out.printf("IMAP connection to %s successful\n", set.host());

            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_ONLY);
            ImapManager.inbox = inbox;
            return new Pair<>(inbox, null);
        } catch (MessagingException e) {
            return new Pair<>(null, e);
        }
    }

    /**
     * This method is always blocking.
     *
     * @return the user's inbox folder through IMAP
     */
    public static Folder getInbox() {
        return getInboxExc().getA();
    }

    /**
     * Blocking if the inbox hasn't been cached yet.
     *
     * @return a cached version of the user's inbox folder, or a new one if it hasn't been cached yet
     */
    public static Folder getCachedInbox() {
        if (inbox == null) {
            System.out.println("Re-fetching non-cached inbox");
            inbox = getInbox();
        }
        return inbox;
    }
}
