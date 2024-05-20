package kiwi.sofia.mail;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import kiwi.sofia.mail.view.ClientView;
import kiwi.sofia.mail.view.LoginView;
import kiwi.sofia.mail.view.SofView;

public class Main extends Application {
    public static boolean skipLogin = false;

    public static void main(String[] args) {
        if (args.length > 0 && args[0].equals("--skip-login")) {
            skipLogin = true;
        }

        launch(args);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Clearing credentials...");
            LoginView.clearCredentials();
            System.out.println("Credentials cleared");
        }));
    }

    @Override
    public void start(Stage stage) {
        SofView view = skipLogin ? ClientView.getInstance() : new LoginView();
        Pane root = view.getView();
        Scene scene = new Scene(root, 1280, 720);
        stage.setTitle("Mail");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();

        stage.widthProperty().addListener((obs, oldVal, newVal) -> {
            root.setPrefWidth((double) newVal);
        });

        stage.heightProperty().addListener((obs, oldVal, newVal) -> {
            root.setPrefHeight((double) newVal);
        });
    }
}
