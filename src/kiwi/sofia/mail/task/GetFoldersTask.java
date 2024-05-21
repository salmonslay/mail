package kiwi.sofia.mail.task;

import jakarta.mail.Folder;
import javafx.concurrent.Task;
import kiwi.sofia.mail.common.ImapManager;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 * A task that fetches the user's folders.
 */
public class GetFoldersTask extends Task<ArrayList<Folder>> {
    @Override
    protected ArrayList<Folder> call() throws Exception {
        Folder[] folders = ImapManager.getFolders();

        if (folders == null) {
            throw new RuntimeException("Error fetching folders"); // will be caught by javafx
        }

        // [Gmail]-folders first
        ArrayList<Folder> foldersList = new ArrayList<>();
        for (Folder folder : folders) {
            String name = folder.getFullName();
            if (!name.contains("[Gmail]") || name.equalsIgnoreCase("[Gmail]"))
                continue;

            foldersList.add(folder);
            System.out.println("Added folder: " + name);
        }

        // Add the non-Gmail folders to the end of the list
        for (Folder folder : folders) {
            String name = folder.getFullName();
            if (name.contains("[Gmail]"))
                continue;

            if (name.equals("INBOX"))
                foldersList.add(0, folder);
            else
                foldersList.add(folder);
        }


        return foldersList;
    }
}
