package kiwi.sofia.mail.template;

import jakarta.mail.Message;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.Pane;
import kiwi.sofia.mail.view.SofView;
import org.kordamp.ikonli.javafx.FontIcon;

public class FolderCell extends ListCell<String> implements SofView {
    @FXML
    private Pane rootPane;
    @FXML
    private Label folderLabel;
    @FXML
    private FontIcon icon;
    private String folderName;

    @Override
    protected void updateItem(String folderName, boolean empty) {
        super.updateItem(folderName, empty);

        if (empty) return;

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/FolderCell.fxml"));
            loader.setController(this);
            loader.load();

            this.folderName = folderName;
            folderLabel.setText(folderName);

            setGraphic(rootPane);

        } catch (Exception e) {
            System.out.println("Failed to load FolderCell.fxml: " + e.getMessage());
        }
    }

    @Override
    public Pane getView() {
        return rootPane;
    }
}
