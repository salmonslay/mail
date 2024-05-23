package kiwi.sofia.mail.view;

import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import kiwi.sofia.mail.common.ConnectionRecord;
import kiwi.sofia.mail.task.ConnectionVerifierTask;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.prefs.Preferences;

public class LoginView implements SofView {
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
    private final CheckBox rememberPassword;
    private final Button loginButton;
    private final Label statusLabel;
    private final TextField displayNameField;

    public LoginView() {
        Preferences prefs = Preferences.userNodeForPackage(LoginView.class);
        ConnectionRecord smtpSet = ConnectionRecord.getSmtpConnectionSet();
        ConnectionRecord imapSet = ConnectionRecord.getImapConnectionSet();

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

        contentPane.add(new Label("Display name:"), 0, line);
        displayNameField = new TextField(smtpSet.displayName());
        contentPane.add(displayNameField, 1, line++);

        contentPane.add(new Label("Username:"), 0, line); // is it bad practice to init without a reference? eh, saves quite a few lines in this otherwise very ugly function
        smtpUsernameField = new TextField(smtpSet.username());
        contentPane.add(smtpUsernameField, 1, line++);

        contentPane.add(new Label("Password:"), 0, line);
        smtpPasswordField = new PasswordField();
        smtpPasswordField.setText(smtpSet.password());
        contentPane.add(smtpPasswordField, 1, line++);

        contentPane.add(new Label("Host:"), 0, line);
        smtpHostField = new TextField(smtpSet.host());
        contentPane.add(smtpHostField, 1, line++);

        contentPane.add(new Label("Port:"), 0, line);
        smtpPortField = new ComboBox<>();
        smtpPortField.getItems().addAll(465, 587);
        smtpPortField.setValue(smtpSet.port());
        contentPane.add(smtpPortField, 1, line++);

        Text imapTitle = new Text("IMAP Server");
        imapTitle.setFont(Font.font("Arial", FontWeight.NORMAL, 20));
        contentPane.add(imapTitle, 0, line++, 2, 1);

        contentPane.add(new Label("Username:"), 0, line);
        imapUsernameField = new TextField(imapSet.username());
        contentPane.add(imapUsernameField, 1, line++);

        contentPane.add(new Label("Password:"), 0, line);
        imapPasswordField = new PasswordField();
        imapPasswordField.setText(imapSet.password());
        contentPane.add(imapPasswordField, 1, line++);

        contentPane.add(new Label("Host:"), 0, line);
        imapHostField = new TextField(imapSet.host());
        contentPane.add(imapHostField, 1, line++);

        contentPane.add(new Label("Port:"), 0, line);
        imapPortField = new ComboBox<>();
        imapPortField.getItems().addAll(143, 993);
        imapPortField.setValue(imapSet.port());
        contentPane.add(imapPortField, 1, line++);

        contentPane.add(new Separator(), 0, line++, 2, 1);

        rememberMe = new CheckBox("Remember me");
        rememberMe.setSelected(prefs.getBoolean("rememberMe", false));
        rememberMe.setTooltip(new Tooltip("This will not remember your password"));
        contentPane.add(rememberMe, 0, line++, 2, 1);

        rememberPassword = new CheckBox("Remember password");
        rememberPassword.setSelected(prefs.getBoolean("rememberPassword", false));
        contentPane.add(rememberPassword, 0, line++, 2, 1);

        loginButton = new Button("Login");
        contentPane.add(loginButton, 0, line++, 2, 1);
        loginButton.setOnAction(this::login);

        statusLabel = new Label();
        contentPane.add(statusLabel, 0, line++, 2, 1);
    }

    private void login(ActionEvent e) {
        Preferences prefs = Preferences.userNodeForPackage(LoginView.class);

        if (rememberPassword.isSelected() && !prefs.getBoolean("dontWarnAboutPasswords", false)) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "", new ButtonType("Continue", ButtonBar.ButtonData.FINISH), new ButtonType("Don't ask again", ButtonBar.ButtonData.YES), ButtonType.CANCEL);
            alert.initOwner(getView().getScene().getWindow());
            alert.setTitle("Security warning");
            alert.setHeaderText("Your password(s) will be saved in plaintext in the registry, and could be retrieved by anyone with\naccess to your computer. This might be convenient, but is not recommended.");
            alert.setContentText("Are you sure you want to continue?");
            alert.showAndWait();

            if (alert.getResult().getButtonData().isCancelButton()) {
                rememberPassword.setSelected(false);
                return;
            } else if (alert.getResult().getButtonData().getTypeCode().equals("Y")) {
                prefs.putBoolean("dontWarnAboutPasswords", true);
                System.out.println("Not warning about passwords anymore");
            }
        }
        
        saveCredentials();
        System.out.println("Logging in...");

        ConnectionVerifierTask verifyTask = new ConnectionVerifierTask();
        statusLabel.textProperty().bind(verifyTask.messageProperty());

        verifyTask.setOnRunning((successEvent) -> {
            loginButton.setDisable(true);
        });

        verifyTask.setOnSucceeded((successEvent) -> {
            System.out.println("Login successful");

            SofView clientView = ClientView.getInstance();
            contentPane.getScene().setRoot(clientView.getView());
        });

        verifyTask.setOnFailed((failedEvent) -> {
            loginButton.setDisable(false);
            System.out.println("Login failed");
        });

        ExecutorService smtpExecutor = Executors.newFixedThreadPool(1);
        smtpExecutor.execute(verifyTask);
        smtpExecutor.shutdown();
    }

    /**
     * Saves the login credentials to prefs (the registry).
     * They will be saved regardless of user preference (to be used in the application),
     * but cleared upon exit in case the user wishes so.
     */
    private void saveCredentials() {
        Preferences prefs = Preferences.userNodeForPackage(LoginView.class);
        prefs.put("smtpDisplayName", displayNameField.getText());
        prefs.put("smtpUsername", smtpUsernameField.getText());
        prefs.put("smtpHost", smtpHostField.getText());
        prefs.putInt("smtpPort", smtpPortField.getValue());
        prefs.put("imapUsername", imapUsernameField.getText());
        prefs.put("imapHost", imapHostField.getText());
        prefs.putInt("imapPort", imapPortField.getValue());
        prefs.putBoolean("rememberMe", rememberMe.isSelected());
        prefs.putBoolean("rememberPassword", rememberPassword.isSelected());

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

        if (!prefs.getBoolean("rememberMe", false)) {
            prefs.remove("smtpDisplayName");
            prefs.remove("smtpUsername");
            prefs.remove("smtpHost");
            prefs.remove("smtpPort");
            prefs.remove("imapUsername");
            prefs.remove("imapHost");
            prefs.remove("imapPort");

            System.out.println("Cleared login details");
        }

        if (!prefs.getBoolean("rememberPassword", false)) {
            prefs.remove("smtpPassword");
            prefs.remove("imapPassword");

            System.out.println("Cleared passwords");
        }
    }

    public Pane getView() {
        return contentPane;
    }
}
