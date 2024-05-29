package kiwi.sofia.mail.common;

import jakarta.mail.*;

import java.util.HashMap;
import java.util.Properties;

public class ImapManager {
    private static final HashMap<String, Folder> folders = new HashMap<>();
    private static Store store;

    /**
     * Gets the user's inbox folder through IMAP. Blocking.
     *
     * @return a pair of the inbox folder and an exception if an error occurred
     */
    public static Pair<Folder, Exception> getFolderExc(String folderName) {
        System.out.println("Fetching folder from server");

        try {
            Store store = getCachedStore();
            Folder folder = store.getFolder(folderName);

            if (!folder.isOpen())
                folder.open(Folder.READ_WRITE);
            ImapManager.folders.put(folderName, folder);
            return new Pair<>(folder, null);
        } catch (MessagingException e) {
            return new Pair<>(null, e);
        }
    }

    public static Pair<Store, Exception> getStoreExc() {
        System.out.println("Fetching store from server");

        try {
            ConnectionRecord set = ConnectionRecord.getImapConnectionSet();

            Properties props = PropertiesCreator.createImapProperties();
            Session session = Session.getInstance(props);
            Store store = session.getStore("imap");
            store.connect(set.username(), set.password());

            System.out.printf("IMAP connection to %s successful\n", set.host());

            return new Pair<>(store, null);
        } catch (MessagingException e) {
            return new Pair<>(null, e);
        }
    }

    /**
     * Blocking.
     *
     * @return all folders in the user's inbox, or null if an error occurred
     */
    public static Folder[] getFolders() {
        try {
            return getCachedStore().getDefaultFolder().list("*");
        } catch (MessagingException e) {
            return null;
        }
    }

    /**
     * This method is always blocking.
     *
     * @return the user's inbox folder through IMAP
     */
    public static Folder getFolder(String folderName) {
        return getFolderExc(folderName).getA();
    }

    /**
     * Blocking if the inbox hasn't been cached yet.
     *
     * @return a cached version of the user's inbox folder, or a new one if it hasn't been cached yet
     */
    public static Folder getCachedInbox(String folderName) {
        if (!folders.containsKey(folderName)) {
            System.out.printf("Re-fetching non-cached folder %s\n", folderName);
            folders.put(folderName, getFolder(folderName));
        }
        return folders.get(folderName);
    }

    public static Store getCachedStore() {
        if (store == null) {
            System.out.println("Re-fetching non-cached store");
            store = getStoreExc().getA();
        }
        return store;
    }
}
