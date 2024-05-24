package kiwi.sofia.mail.template;

import jakarta.mail.Folder;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.Pane;
import kiwi.sofia.mail.view.InboxView;
import kiwi.sofia.mail.view.SofView;
import org.kordamp.ikonli.javafx.FontIcon;

/**
 * A cell that contains an icon and a label for a specific folder.
 * Can be clicked to display the emails in that folder.
 */
public class FolderCell extends ListCell<Folder> implements SofView {
    @FXML
    private Pane rootPane;
    @FXML
    private Label folderLabel;
    @FXML
    private FontIcon icon;

    @Override
    protected void updateItem(Folder folder, boolean empty) {
        super.updateItem(folder, empty);

        if (empty) return;

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/FolderCell.fxml"));
            loader.setController(this);
            loader.load();

            String pattern = "\\[Gmail\\]\\/(.+)"; // Matches [Gmail]/(folderName)
            String folderName = folder.getFullName();

            // Special case for Gmail folders & INBOX
            if (folderName.matches(pattern) || folderName.equals("INBOX")) {
                if (folderName.equals("INBOX"))
                    folderName = "Inbox"; // We don't want an uppercase display name
                else
                    folderName = folderName.replaceAll(pattern, "$1"); // remove the [Gmail]/ prefix

                setFolderIcon(folderName);
            }

            folderLabel.setText(folderName);

            setGraphic(rootPane);

        } catch (Exception e) {
            System.out.println("Failed to load FolderCell.fxml: " + e.getMessage());
        }
    }

    /**
     * Sets the icon of Gmail & INBOX-folders based on their name.
     *
     * @param folderName The name of the folder.
     */
    private void setFolderIcon(String folderName) {
        if (folderName.equalsIgnoreCase("all mail"))
            icon.setIconLiteral("fa-envelope-o");
        else if (folderName.equalsIgnoreCase("drafts"))
            icon.setIconLiteral("fa-file-o");
        else if (folderName.equalsIgnoreCase("sent mail"))
            icon.setIconLiteral("fa-paper-plane-o");
        else if (folderName.equalsIgnoreCase("spam"))
            icon.setIconLiteral("fa-exclamation");
        else if (folderName.equalsIgnoreCase("starred"))
            icon.setIconLiteral("fa-star-o");
        else if (folderName.equalsIgnoreCase("trash"))
            icon.setIconLiteral("fa-trash-o");
        else if (folderName.equalsIgnoreCase("important"))
            icon.setIconLiteral("fa-bookmark-o");
        else if (folderName.equalsIgnoreCase("inbox"))
            icon.setIconLiteral("fa-inbox");
    }

    @Override
    public Pane getView() {
        return rootPane;
    }
}
