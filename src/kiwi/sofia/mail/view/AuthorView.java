package kiwi.sofia.mail.view;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import kiwi.sofia.mail.template.EmailCell;

public class AuthorView implements SofView {
    @FXML
    private Pane rootPane;

    @FXML
    private Label filesAttachedLabel;

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
    public void actionSendEmail() {
        System.out.println("Sending email");
    }

    @FXML
    public void actionBackToInbox() {
        ClientView.showInbox();
    }

    @FXML
    public void actionAttachFiles() {
        System.out.println("Attaching file");
    }

    @FXML
    public void actionSend() {
        System.out.println("Sending email");
    }
}
