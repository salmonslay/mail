package kiwi.sofia.mail.view;

import jakarta.mail.Message;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.web.WebView;
import kiwi.sofia.mail.common.BodyParser;

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
        webView.getEngine().loadContent(BodyParser.toHtml(body));
    }

    @Override
    public Pane getView() {
        return contentPane;
    }
}
