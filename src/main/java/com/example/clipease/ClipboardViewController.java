package com.example.clipease;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.stage.Stage;
import javafx.util.Duration;

public class ClipboardViewController {

    @FXML
    private ListView<String> clipboardListView;

    private ObservableList<String> clipboardHistory;

    @FXML
    public void initialize() {
        clipboardHistory = FXCollections.observableArrayList();
        clipboardListView.setItems(clipboardHistory);

        // Start clipboard monitoring using Timeline
        Timeline clipboardTimeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            String currentContent = getClipboardContent();
            if (!clipboardHistory.isEmpty() && !currentContent.equals(clipboardHistory.get(0)) && !currentContent.isEmpty()) {
                clipboardHistory.add(0, currentContent); // Add to the top of the list
            } else if (clipboardHistory.isEmpty() && !currentContent.isEmpty()) {
                clipboardHistory.add(0, currentContent); // Add first item
            }
        }));
        clipboardTimeline.setCycleCount(Timeline.INDEFINITE);
        clipboardTimeline.play();
    }

    private String getClipboardContent() {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        if (clipboard.hasString()) {
            return clipboard.getString();
        }
        return "";
    }

    /*@FXML
    public void handleClose() {
        Stage stage = (Stage) closeIcon.getScene().getWindow();
        stage.close();
    } */
}
