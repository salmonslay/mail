import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import view.LoginView;
import view.SofView;

import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

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
        SofView view = new LoginView();
        Pane root = view.getView();
        Scene scene = new Scene(root, 800, 600);
        stage.setTitle("Mail");
        stage.setScene(scene);
        stage.show();
    }
}
