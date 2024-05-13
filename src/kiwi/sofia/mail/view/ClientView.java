package kiwi.sofia.mail.view;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import kiwi.sofia.mail.template.EmailCell;

public class ClientView implements SofView {
    private static ClientView instance;
    @FXML
    private Pane rootPane;

    @FXML
    private BorderPane borderPane;

    private ClientView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Client.fxml"));
            loader.setController(this);
            loader.load();

            borderPane.setCenter(InboxView.getInstance().getView());
        } catch (Exception e) {
            System.out.println("Failed to load Client.fxml" + e.getMessage());
        }
    }

    @Override
    public Pane getView() {
        return rootPane;
    }

    public BorderPane getBorderPane() {
        return borderPane;
    }

    public static ClientView getInstance() {
        if (instance == null)
            instance = new ClientView();

        return instance;
    }
}
