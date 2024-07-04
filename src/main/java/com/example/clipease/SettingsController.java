package com.example.clipease;

import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

import java.io.IOException;

public class SettingsController {

    @FXML
    private Button backButton;

    @FXML
    private AnchorPane rootPane;

    @FXML
    public void initialize() {
        backButton.setOnAction(event -> {
            try {
                showClipboardView();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void setRootPane(AnchorPane rootPane) {
        this.rootPane = rootPane;
    }
    private void showClipboardView() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("clipboard-view.fxml"));
        Pane clipboardPane = loader.load();

        // Animate the transition
        clipboardPane.setTranslateX(-rootPane.getWidth());
        rootPane.getChildren().add(clipboardPane);

        TranslateTransition transition = new TranslateTransition(Duration.seconds(0.2), clipboardPane);
        transition.setToX(0);
        transition.setOnFinished(evt -> rootPane.getChildren().remove(backButton.getParent()));
        transition.play();
    }
}
