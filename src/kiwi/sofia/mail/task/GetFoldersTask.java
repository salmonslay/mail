package kiwi.sofia.mail.task;

import jakarta.mail.Folder;
import javafx.concurrent.Task;
import kiwi.sofia.mail.common.ImapManager;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * A task that fetches the user's folders.
 */
public class GetFoldersTask extends Task<String[]> {
    @Override
    protected String[] call() throws Exception {
        Folder[] folders = ImapManager.getFolders();

        if (folders == null) {
            throw new RuntimeException("Error fetching folders"); // will be caught by javafx
        }

        ArrayList<String> folderNames = new ArrayList<>();

        for (Folder f : folders) {
            folderNames.add(f.getFullName());
            System.out.println(f.getFullName());
        }

        return folderNames.toArray(new String[0]);
    }
}
