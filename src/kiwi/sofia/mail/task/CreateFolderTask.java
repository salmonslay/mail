package kiwi.sofia.mail.task;

import jakarta.mail.Folder;
import javafx.concurrent.Task;
import javafx.scene.control.TextInputDialog;
import kiwi.sofia.mail.common.ImapManager;

/**
 * A task that asks the user for a folder name and creates it.
 */
public class CreateFolderTask extends Task<Void> {
    private final String folderName;

    private CreateFolderTask(String folderName) {
        this.folderName = folderName;
    }

    @Override
    protected Void call() throws Exception {
        if (folderName == null || folderName.isBlank())
            throw new IllegalArgumentException("Folder name cannot be empty");

        Folder folder = ImapManager.getCachedStore().getFolder(folderName);
        if (folder.exists())
            throw new IllegalArgumentException("Folder already exists");

        folder.create(Folder.HOLDS_MESSAGES);
        folder.setSubscribed(true);
        return null;
    }

    /**
     * Creates a dialog for the user to enter a folder name.
     *
     * @return The task that will create the folder.
     */
    public static CreateFolderTask startWizard() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Create folder");
        dialog.setHeaderText(null);
        dialog.setContentText("Enter the name of the new folder:");

        dialog.showAndWait();

        CreateFolderTask task = new CreateFolderTask(dialog.getResult());
        new Thread(task).start();
        return task;
    }
}
