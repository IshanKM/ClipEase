package com.example.clipease;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;

import java.util.Timer;
import java.util.TimerTask;

public class ClipboardController {

    @FXML
    private ListView<String> clipboardListView;

    private ObservableList<String> clipboardHistory;

    @FXML
    public void initialize() {
        clipboardHistory = FXCollections.observableArrayList();
        clipboardListView.setItems(clipboardHistory);

        // Start clipboard monitoring
        Timer clipboardTimer = new Timer(true);
        clipboardTimer.scheduleAtFixedRate(new TimerTask() {
            private String lastContent = "";

            @Override
            public void run() {
                String currentContent = getClipboardContent();
                if (!currentContent.equals(lastContent)) {
                    lastContent = currentContent;
                    Platform.runLater(() -> {
                        clipboardHistory.add(0, currentContent); // Add to the top of the list
                        // TODO: Store in SQLite database
                    });
                }
            }
        }, 0, 1000); // Check every second
    }

    private String getClipboardContent() {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        if (clipboard.hasString()) {
            return clipboard.getString();
        }
        return "";
    }
}
