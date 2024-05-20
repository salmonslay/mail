package kiwi.sofia.mail.view;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

/**
 * The main view for the program, used to nest other views as long as the user is logged in.
 * This view also maintains the menu bar.
 */
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

    public static void setCenter(Node node) {
        getInstance().getBorderPane().setCenter(node);
    }

    public static ClientView getInstance() {
        if (instance == null)
            instance = new ClientView();

        return instance;
    }

    public static void showInbox() {
        getInstance().getBorderPane().setCenter(InboxView.getInstance().getView());
    }

    @FXML
    public void actionWriteEmail() {
        AuthorView.show();
    }

    @FXML
    public void actionLogOut() {
        LoginView.clearCredentials();
        rootPane.getScene().setRoot(new LoginView().getView());
    }

    @FXML
    public void actionQuit() {
        System.exit(0);
    }

    @FXML
    public void actionOpenInbox() {
        showInbox();
    }
}
