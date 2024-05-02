package kiwi.sofia.mail;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import kiwi.sofia.mail.view.InboxView;
import kiwi.sofia.mail.view.LoginView;
import kiwi.sofia.mail.view.SofView;

public class Main extends Application {
    public static void main(String[] args) {
        launch(args);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Clearing credentials...");
            LoginView.clearCredentials();
            System.out.println("Credentials cleared");
        }));
    }

    @Override
    public void start(Stage stage) {
        SofView view = new InboxView();
        Pane root = view.getView();
        Scene scene = new Scene(root, 800, 600);
        stage.setTitle("Mail");
        stage.setScene(scene);
        stage.show();
    }
}
