package com.example.clipease;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class ClipboardViewController {
    @FXML
    private Label welcomeText;
    @FXML
    private ImageView closeIcon;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }

    public void closeapplication() {
        System.exit(0);
    }

    @FXML
    public void handleClose() {
        Stage stage = (Stage) closeIcon.getScene().getWindow();
        stage.close();
    }
}