package com.example.clipease;

import javafx.animation.RotateTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

import java.io.IOException;

public class SettingsController {


    //private Button backButton;
    @FXML
    private ImageView backButton;

    @FXML
    private AnchorPane rootPane;

    @FXML
    public void initialize() {
        backButton.setOnMouseClicked(event -> {
            try {
                showClipboardView();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        // Add rotate effect handlers
        backButton.setOnMouseEntered(event -> {
            RotateTransition rotateTransition = new RotateTransition(Duration.seconds(0.2), backButton);
            rotateTransition.setToAngle(360); // Rotate 90 degrees
            rotateTransition.play();
        });

        backButton.setOnMouseExited(event -> {
            RotateTransition rotateTransition = new RotateTransition(Duration.seconds(0.2), backButton);
            rotateTransition.setToAngle(0); // Rotate back to 0 degrees
            rotateTransition.play();
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
