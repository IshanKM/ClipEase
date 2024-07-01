package com.example.clipease.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class ClipboardController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }

    public void testbutton()    {
        System.out.println("test");
    }
}
