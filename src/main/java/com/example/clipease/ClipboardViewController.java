package com.example.clipease;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.input.Clipboard;
import javafx.util.Duration;

public class ClipboardViewController {

    @FXML
    private ListView<String> clipboardListView;

    private ObservableList<String> clipboardHistory;
    private ClipboardDB clipboardDB;

    @FXML
    public void initialize() {
        clipboardDB = new ClipboardDB();
        clipboardHistory = FXCollections.observableArrayList();

        // Load existing clipboard history from the database
        clipboardHistory.addAll(clipboardDB.getClipboardHistory());
        clipboardListView.setItems(clipboardHistory);

        // Start clipboard monitoring using Timeline
        Timeline clipboardTimeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            String currentContent = getClipboardContent();
            if (!clipboardHistory.isEmpty() && !currentContent.equals(clipboardHistory.get(0)) && !currentContent.isEmpty()) {
                clipboardHistory.add(0, currentContent); // Add to the top of the list
                clipboardDB.saveClipboardContent(currentContent); // Save to the database
            } else if (clipboardHistory.isEmpty() && !currentContent.isEmpty()) {
                clipboardHistory.add(0, currentContent); // Add first item
                clipboardDB.saveClipboardContent(currentContent); // Save to the database
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
}
