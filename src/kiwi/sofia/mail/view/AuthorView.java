package kiwi.sofia.mail.view;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.web.HTMLEditor;
import javafx.stage.FileChooser;
import kiwi.sofia.mail.common.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

public class AuthorView implements SofView {
    @FXML
    private Pane rootPane;
    @FXML
    private Label filesAttachedLabel;
    @FXML
    private TextField subjectField;
    @FXML
    private TextField addressField;
    @FXML
    private HTMLEditor messageField;
    @FXML
    private Button sendButton;
    @FXML
    private Button attachButton;
    private List<File> files = new ArrayList<>();

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
    public void actionBackToInbox() {
        ClientView.showInbox();
    }

    @FXML
    public void actionAttachFiles() {
        Preferences prefs = Preferences.userNodeForPackage(AuthorView.class);
        String lastPath = prefs.get("lastPath", System.getProperty("user.home"));

        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(lastPath));
        fileChooser.setTitle("Attach files");
        files = fileChooser.showOpenMultipleDialog(rootPane.getScene().getWindow());

        if (files == null)
            return;

        filesAttachedLabel.setText(files.size() + (files.size() == 1 ? " file" : " files") + " attached");

        prefs.put("lastPath", files.get(0).getParent());
    }

    @FXML
    public void actionSend() {
        Pair<InternetAddress[], String> recipients = getRecipients();
        if (recipients.getB() != null) {
            System.out.println("Invalid email address: " + recipients.getB());

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Invalid email address");
            alert.setHeaderText("Invalid email address");
            alert.setContentText("The email address " + recipients.getB() + " is invalid.");
            alert.showAndWait();
            return;
        }

        System.out.println("Sending email");
        setButtons(true);

        Task<MimeMessage> sendTask = new Task<>() {
            @Override
            protected MimeMessage call() {
                try {
                    updateMessage("Sending message...");
                    ConnectionRecord set = ConnectionRecord.getSmtpConnectionSet();

                    MimeMessage message = new MimeMessage(SmtpManager.getCachedSession());
                    message.setFrom(new InternetAddress(set.username(), set.displayName()));
                    message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(addressField.getText()));
                    message.setSubject(subjectField.getText());

                    BodyPart messageBody = new MimeBodyPart();
                    messageBody.setContent(messageField.getHtmlText(), "text/html");

                    Multipart multipart = new MimeMultipart();
                    multipart.addBodyPart(messageBody);

                    for (File file : files) {
                        MimeBodyPart attachment = new MimeBodyPart();
                        attachment.attachFile(file);
                        multipart.addBodyPart(attachment);
                    }

                    message.setContent(multipart);

                    Transport.send(message);
                    updateMessage("Email sent successfully");
                    return message;
                } catch (Exception e) {
                    updateMessage("Failed to send email: " + e.getMessage());
                    return null;
                }
            }
        };

        filesAttachedLabel.textProperty().bind(sendTask.messageProperty());

        sendTask.setOnSucceeded(event -> {
            EmailView.show(sendTask.getValue());
        });

        sendTask.setOnFailed(event -> {
            setButtons(false);

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Failed to send email");
            alert.setHeaderText("Failed to send email");
            alert.setContentText(sendTask.getException().getMessage());
            alert.showAndWait();
        });

        new Thread(sendTask).start();
    }

    /**
     * Parses the recipient addresses from the address field.
     *
     * @return A pair containing the recipient addresses if successful,
     * or the incorrect address in question if one could not be parsed.
     */
    public Pair<InternetAddress[], String> getRecipients() {
        String addressList = addressField.getText();
        String[] addresses = addressList.split(",");
        ArrayList<InternetAddress> recipients = new ArrayList<>();

        for (String address : addresses) {
            address = address.trim();
            try {
                recipients.add(new InternetAddress(address));
                System.out.println("Added recipient: " + address);
            } catch (Exception e) {
                System.out.println("Invalid email address: " + address);
                return new Pair<>(null, address);
            }
        }

        return new Pair<>(recipients.toArray(new InternetAddress[0]), null);
    }

    private void setButtons(boolean disable) {
        sendButton.setDisable(disable);
        attachButton.setDisable(disable);
        addressField.setDisable(disable);
        subjectField.setDisable(disable);
        messageField.setDisable(disable);
    }
}