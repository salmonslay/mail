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
import java.util.prefs.Preferences;

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

    public LoginView() {
        int line = 0;

        contentPane = new GridPane();
        contentPane.setAlignment(Pos.CENTER);
        contentPane.setHgap(10);
        contentPane.setVgap(10);
        contentPane.setPadding(new Insets(25, 25, 25, 25));

        Text titleText = new Text("Login");
        titleText.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        contentPane.add(titleText, 0, line++, 2, 1);

        Text smtpTitle = new Text("SMTP Server");
        smtpTitle.setFont(Font.font("Arial", FontWeight.NORMAL, 20));
        contentPane.add(smtpTitle, 0, line++, 2, 1);

        contentPane.add(new Label("Username:"), 0, line); // is it bad practice to init without a reference? eh, saves quite a few lines in this otherwise very ugly function
        smtpUsernameField = new TextField();
        contentPane.add(smtpUsernameField, 1, line++);

        contentPane.add(new Label("Password:"), 0, line);
        smtpPasswordField = new PasswordField();
        contentPane.add(smtpPasswordField, 1, line++);

        contentPane.add(new Label("Host:"), 0, line);
        smtpHostField = new TextField();
        contentPane.add(smtpHostField, 1, line++);

        contentPane.add(new Label("Port:"), 0, line);
        smtpPortField = new ComboBox<>();
        smtpPortField.getItems().addAll(25, 465, 587);
        smtpPortField.setValue(465);
        contentPane.add(smtpPortField, 1, line++);

        Text imapTitle = new Text("IMAP Server");
        imapTitle.setFont(Font.font("Arial", FontWeight.NORMAL, 20));
        contentPane.add(imapTitle, 0, line++, 2, 1);

        contentPane.add(new Label("Username:"), 0, line);
        imapUsernameField = new TextField();
        contentPane.add(imapUsernameField, 1, line++);

        contentPane.add(new Label("Password:"), 0, line);
        imapPasswordField = new PasswordField();
        contentPane.add(imapPasswordField, 1, line++);

        contentPane.add(new Label("Host:"), 0, line);
        imapHostField = new TextField();
        contentPane.add(imapHostField, 1, line++);

        contentPane.add(new Label("Port:"), 0, line);
        imapPortField = new ComboBox<>();
        imapPortField.getItems().addAll(143, 993);
        imapPortField.setValue(993);
        contentPane.add(imapPortField, 1, line++);

        contentPane.add(new Separator(), 0, line++, 2, 1);

        Button loginButton = new Button("Login");
        contentPane.add(loginButton, 0, line++, 2, 1);
        loginButton.setOnAction(e -> login());
    }

    private void login() {

    }

    public Pane getView() {
        return contentPane;
    }
}
