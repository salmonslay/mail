package kiwi.sofia.mail.task;

import jakarta.mail.Folder;
import javafx.concurrent.Task;
import kiwi.sofia.mail.common.ImapManager;

import java.util.ArrayList;

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

        // [Gmail]-folders first, minus the [Gmail] folder itself
        ArrayList<Folder> foldersList = new ArrayList<>();
        for (Folder folder : folders) {
            String name = folder.getFullName();
            if (!name.contains("[Gmail]") || name.equalsIgnoreCase("[Gmail]"))
                continue;

            foldersList.add(folder);
        }

        // Add the remaining non-Gmail folders
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
