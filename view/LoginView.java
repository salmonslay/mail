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
    private final CheckBox rememberMe;
    private final Preferences prefs;

    public LoginView() {
        prefs = Preferences.userNodeForPackage(LoginView.class);
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
        smtpUsernameField = new TextField(prefs.get("smtpUsername", ""));
        contentPane.add(smtpUsernameField, 1, line++);

        contentPane.add(new Label("Password:"), 0, line);
        smtpPasswordField = new PasswordField();
        contentPane.add(smtpPasswordField, 1, line++);

        contentPane.add(new Label("Host:"), 0, line);
        smtpHostField = new TextField(prefs.get("smtpHost", ""));
        contentPane.add(smtpHostField, 1, line++);

        contentPane.add(new Label("Port:"), 0, line);
        smtpPortField = new ComboBox<>();
        smtpPortField.getItems().addAll(25, 465, 587);
        smtpPortField.setValue(prefs.getInt("smtpPort", 465));
        contentPane.add(smtpPortField, 1, line++);

        Text imapTitle = new Text("IMAP Server");
        imapTitle.setFont(Font.font("Arial", FontWeight.NORMAL, 20));
        contentPane.add(imapTitle, 0, line++, 2, 1);

        contentPane.add(new Label("Username:"), 0, line);
        imapUsernameField = new TextField(prefs.get("imapUsername", ""));
        contentPane.add(imapUsernameField, 1, line++);

        contentPane.add(new Label("Password:"), 0, line);
        imapPasswordField = new PasswordField();
        contentPane.add(imapPasswordField, 1, line++);

        contentPane.add(new Label("Host:"), 0, line);
        imapHostField = new TextField(prefs.get("imapHost", ""));
        contentPane.add(imapHostField, 1, line++);

        contentPane.add(new Label("Port:"), 0, line);
        imapPortField = new ComboBox<>();
        imapPortField.getItems().addAll(143, 993);
        imapPortField.setValue(prefs.getInt("imapPort", 993));
        contentPane.add(imapPortField, 1, line++);

        contentPane.add(new Separator(), 0, line++, 2, 1);

        rememberMe = new CheckBox("Remember me");
        rememberMe.setSelected(true);
        rememberMe.setTooltip(new Tooltip("This will not remember your password"));
        contentPane.add(rememberMe, 0, line++, 2, 1);

        Button loginButton = new Button("Login");
        contentPane.add(loginButton, 0, line++, 2, 1);
        loginButton.setOnAction(e -> login());
    }

    private void login() {
        saveCredentials();
    }

    /**
     * Saves the login credentials to prefs (the registry).
     * They will be saved regardless of user preference, but cleared upon exit in case the user wishes so.
     * Passwords will always be cleared.
     */
    private void saveCredentials() {
        prefs.put("smtpUsername", smtpUsernameField.getText());
        prefs.put("smtpHost", smtpHostField.getText());
        prefs.putInt("smtpPort", smtpPortField.getValue());
        prefs.put("imapUsername", imapUsernameField.getText());
        prefs.put("imapHost", imapHostField.getText());
        prefs.putInt("imapPort", imapPortField.getValue());
        prefs.putBoolean("rememberMe", rememberMe.isSelected());

        prefs.put("smtpPassword", smtpPasswordField.getText());
        prefs.put("imapPassword", imapPasswordField.getText());

        System.out.println("Saved credentials");
    }

    /**
     * Clear the saved credentials from the registry.
     * Passwords will always be cleared, the rest will be cleared if rememberMe is not set.
     */
    public static void clearCredentials() {
        Preferences prefs = Preferences.userNodeForPackage(LoginView.class);
        prefs.remove("smtpPassword");
        prefs.remove("imapPassword");

        if (prefs.getBoolean("rememberMe", false))
            return;

        prefs.remove("smtpUsername");
        prefs.remove("smtpHost");
        prefs.remove("smtpPort");
        prefs.remove("imapUsername");
        prefs.remove("imapHost");
        prefs.remove("imapPort");
    }

    public Pane getView() {
        return contentPane;
    }
}
