package view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.util.ArrayList;

public class LoginView {
    private final GridPane contentPane;
    private final TextField smtpUsernameField;
    private final PasswordField smtpPasswordField;
    private final TextField smtpHostField;
    private final ComboBox<Integer> smtpPortField;
    private final TextField imapUsernameField;
    private final PasswordField imapPasswordField;
    private final TextField imapHostField;
    private final ComboBox<Integer> imapPortField;
    private final Button loginButton;

    public LoginView() {
        contentPane = new GridPane();
        contentPane.setAlignment(Pos.CENTER);
        contentPane.setHgap(10);
        contentPane.setVgap(10);
        contentPane.setPadding(new Insets(25, 25, 25, 25));

        Text titleText = new Text("Login");
        titleText.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        contentPane.add(titleText, 0, 0, 2, 1);

        Text smtpTitle = new Text("SMTP Server");
        smtpTitle.setFont(Font.font("Arial", FontWeight.NORMAL, 20));
        contentPane.add(smtpTitle, 0, 1, 2, 1);

        contentPane.add(new Label("Username:"), 0, 2); // is it bad practice to init without a reference? eh, saves quite a few lines in this otherwise very ugly function
        smtpUsernameField = new TextField();
        contentPane.add(smtpUsernameField, 1, 2);

        contentPane.add(new Label("Password:"), 0, 3);
        smtpPasswordField = new PasswordField();
        contentPane.add(smtpPasswordField, 1, 3);

        contentPane.add(new Label("Host:"), 0, 4);
        smtpHostField = new TextField();
        contentPane.add(smtpHostField, 1, 4);

        contentPane.add(new Label("Port:"), 0, 5);
        smtpPortField = new ComboBox<>();
        smtpPortField.getItems().addAll(25, 465, 587);
        smtpPortField.setValue(465);
        contentPane.add(smtpPortField, 1, 5);

        Text imapTitle = new Text("IMAP Server");
        imapTitle.setFont(Font.font("Arial", FontWeight.NORMAL, 20));
        contentPane.add(imapTitle, 0, 6, 2, 1);

        contentPane.add(new Label("Username:"), 0, 7);
        imapUsernameField = new TextField();
        contentPane.add(imapUsernameField, 1, 7);

        contentPane.add(new Label("Password:"), 0, 8);
        imapPasswordField = new PasswordField();
        contentPane.add(imapPasswordField, 1, 8);

        contentPane.add(new Label("Host:"), 0, 9);
        imapHostField = new TextField();
        contentPane.add(imapHostField, 1, 9);

        contentPane.add(new Label("Port:"), 0, 10);
        imapPortField = new ComboBox<>();
        imapPortField.getItems().addAll(143, 993);
        imapPortField.setValue(993);
        contentPane.add(imapPortField, 1, 10);

        contentPane.add(new Separator(), 0, 11, 2, 1);

        loginButton = new Button("Login");
        contentPane.add(loginButton, 0, 12, 2, 1);
        loginButton.setOnAction(e -> login());
    }

    private void login() {

    }

    public Pane getView() {
        return contentPane;
    }
}
