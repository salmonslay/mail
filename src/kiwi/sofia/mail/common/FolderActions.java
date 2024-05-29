package kiwi.sofia.mail.common;

import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import kiwi.sofia.mail.view.InboxView;

import java.util.ArrayList;

/**
 * Helper methods related to Folder objects.
 */
public class FolderActions {
    /**
     * Asks the user for a folder name and returns it. Blocking.
     *
     * @param title  The title of the dialog
     * @param header The header of the dialog
     * @return The name of the folder, or null if the user cancelled / didn't select anything
     */
    public static String askUserForFolder(String title, String header, boolean ignoreSpecialFolders) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);

        ListView<String> listView = new ListView<>();

        // we don't want to show the special folders (gmail & inbox) in the list
        ArrayList<String> folderNames = InboxView.getFolderNames();
        for (String folderName : folderNames) {
            if (ignoreSpecialFolders && (folderName.equalsIgnoreCase("inbox") || folderName.contains("[Gmail]")))
                continue;

            listView.getItems().add(folderName);
        }

        alert.getDialogPane().setContent(listView);
        alert.showAndWait();

        // no folder selected, or the user cancelled
        if (alert.getResult().getButtonData().isCancelButton()) return null;

        return listView.getSelectionModel().getSelectedItem();
    }


    /**
     * @param folderName The name of the folder
     * @return The FontAwesome icon literal for the folder
     */
    public static String getIconLiteral(String folderName) {
        if (folderName.equalsIgnoreCase("all mail")) return "fa-envelope-o";
        else if (folderName.equalsIgnoreCase("drafts")) return "fa-file-o";
        else if (folderName.equalsIgnoreCase("sent mail")) return "fa-paper-plane-o";
        else if (folderName.equalsIgnoreCase("spam")) return "fa-exclamation";
        else if (folderName.equalsIgnoreCase("starred")) return "fa-star-o";
        else if (folderName.equalsIgnoreCase("trash")) return "fa-trash-o";
        else if (folderName.equalsIgnoreCase("important")) return "fa-bookmark-o";
        else if (folderName.equalsIgnoreCase("inbox")) return "fa-inbox";
        else if (folderName.equalsIgnoreCase("new")) return "fa-plus";

        return "fa-folder-o";
    }
}
