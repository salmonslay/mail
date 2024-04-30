package view;

import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

public class InboxView implements SofView {
    private final GridPane contentPane;

    public InboxView() {
        contentPane = new GridPane();
    }

    @Override
    public Pane getView() {
        return contentPane;
    }
}
