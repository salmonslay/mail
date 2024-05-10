package kiwi.sofia.mail.view;

import javafx.fxml.Initializable;
import javafx.scene.layout.Pane;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Interface for all views in the program.
 */
public interface SofView extends Initializable {
    Pane getView();

    @Override
    default void initialize(URL location, ResourceBundle resources) {
    }
}
