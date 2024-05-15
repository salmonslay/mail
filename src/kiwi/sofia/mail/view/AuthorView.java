package kiwi.sofia.mail.view;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

public class AuthorView implements SofView {
    @FXML
    private Pane rootPane;

    @FXML
    private Label filesAttachedLabel;

    private List<File> files = new ArrayList<>();

    private AuthorView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AuthorView.fxml"));
            loader.setController(this);
            loader.load();

            filesAttachedLabel.setText("No files attached");

        } catch (Exception e) {
            System.out.println("Failed to load AuthorView.fxml" + e.getMessage());
        }
    }

    @Override
    public Pane getView() {
        return rootPane;
    }

    public static void show() {
        ClientView.setCenter(new AuthorView().getView());
    }

    @FXML
    public void actionBackToInbox() {
        ClientView.showInbox();
    }

    @FXML
    public void actionAttachFiles() {
        Preferences prefs = Preferences.userNodeForPackage(AuthorView.class);
        String lastPath = prefs.get("lastPath", System.getProperty("user.home"));

        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(lastPath));
        fileChooser.setTitle("Attach files");
        files = fileChooser.showOpenMultipleDialog(rootPane.getScene().getWindow());

        if (files == null)
            return;

        filesAttachedLabel.setText(files.size() + (files.size() == 1 ? " file" : " files") + " attached");

        prefs.put("lastPath", files.get(0).getParent());
    }

    @FXML
    public void actionSend() {
        System.out.println("Sending email");
    }
}
