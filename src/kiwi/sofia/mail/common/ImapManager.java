package kiwi.sofia.mail.common;

import jakarta.mail.Folder;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.Store;
import kiwi.sofia.mail.view.LoginView;


import java.util.Properties;
import java.util.prefs.Preferences;

public class ImapManager {
    /**
     * Gets the user's inbox folder through IMAP.
     *
     * @return a pair of the inbox folder and an exception if an error occurred
     */
    public static Pair<Folder, Exception> getInboxExc() {
        System.out.println("Getting inbox");

        Preferences prefs = Preferences.userNodeForPackage(LoginView.class);
        String username = prefs.get("imapUsername", "");
        String password = prefs.get("imapPassword", "");
        String host = prefs.get("imapHost", "imap.gmail.com");
        int port = prefs.getInt("imapPort", 993);

        try {
            Properties properties = new Properties();
            properties.put("mail.imap.host", host);
            properties.put("mail.imap.port", port);
            properties.put("mail.imap.ssl.enable", "true");

            Session session = Session.getInstance(properties);
            Store store = session.getStore("imap");
            store.connect(username, password);

            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_ONLY);
            return new Pair<>(inbox, null);
        } catch (MessagingException e) {
            return new Pair<>(null, e);
        }
    }

    /**
     * @return the user's inbox folder through IMAP
     */
    public static Folder getInbox() {
        return getInboxExc().getA();
    }
}
