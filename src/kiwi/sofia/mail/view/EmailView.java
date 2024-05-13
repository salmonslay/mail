package kiwi.sofia.mail.view;

import jakarta.mail.Message;
import jakarta.mail.Multipart;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.web.WebView;

public class EmailView implements SofView {
    private final Pane contentPane;
    private final Message message;


    @FXML
    private WebView webView;
    @FXML
    private Label subjectLabel;

    public EmailView(Message message) {
        contentPane = new GridPane();
        this.message = message;

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/EmailView.fxml"));
            loader.setController(this);
            loader.load();

            subjectLabel.setText(message.getSubject());
            setBody(message.getContent());

            contentPane.getChildren().add(loader.getRoot());
        } catch (Exception e) {
            System.out.println("Failed to load InboxView.fxml" + e.getMessage());
        }
    }

    private void setBody(Object body) {
        webView.getEngine().loadContent(parseBody(body));
    }

    public static String parseBody(Object body) {
        if (body instanceof String) {
            return (String) body;
        } else if (body instanceof Multipart) {
            try {
                Multipart multipart = (Multipart) body;
                StringBuilder content = new StringBuilder();
                for (int i = 0; i < multipart.getCount(); i++) {
                    content.append(multipart.getBodyPart(i).getContent());
                }
                return content.toString();
            } catch (Exception e) {
                return "<h1>Failed to load email body</h1>" + e.getMessage();
            }
        } else {
            return "<h1>Failed to load email body</h1>Type " + body.getClass().getName() + " is not supported.";
        }
    }

    @Override
    public Pane getView() {
        return contentPane;
    }
}
