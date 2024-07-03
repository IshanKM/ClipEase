package com.example.clipease;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.util.Duration;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;

import java.io.*;
import java.util.List;

public class ClipboardViewController {

    @FXML
    private ListView<String> clipboardListView;

    private ClipboardDB clipboardDB;

    @FXML
    public void initialize() {
        clipboardDB = new ClipboardDB();
        List<String> history = clipboardDB.getClipboardHistory();

        clipboardListView.getItems().addAll(history);

        // Start clipboard monitoring using Timeline
        Timeline clipboardTimeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            String currentContent = getClipboardContent();
            if (!clipboardListView.getItems().isEmpty() && !currentContent.equals(clipboardListView.getItems().get(0)) && !currentContent.isEmpty()) {
                clipboardDB.saveClipboardContent(currentContent);
                clipboardListView.getItems().add(0, currentContent); // Add to the top of the list
                if (clipboardListView.getItems().size() > 10) {
                    clipboardListView.getItems().remove(10); // Limit to top 10 items
                }
            } else if (clipboardListView.getItems().isEmpty() && !currentContent.isEmpty()) {
                clipboardDB.saveClipboardContent(currentContent);
                clipboardListView.getItems().add(0, currentContent); // Add first item
            }
        }));
        clipboardTimeline.setCycleCount(Timeline.INDEFINITE);
        clipboardTimeline.play();

        clipboardListView.setOnMouseClicked(event -> {
            String selectedItem = clipboardListView.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                pasteClipboardContent(selectedItem);
            }
        });
    }

    private String getClipboardContent() {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        if (clipboard.hasString()) {
            return clipboard.getString();
        }
        return "";
    }

    private void pasteClipboardContent(String content) {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent clipboardContent = new ClipboardContent();
        clipboardContent.putString(content);
        clipboard.setContent(clipboardContent);

        // Extract the script from resources
        try (InputStream is = getClass().getResourceAsStream("/com/example/clipease/paste.sh");
             BufferedReader reader = new BufferedReader(new InputStreamReader(is));
             BufferedWriter writer = new BufferedWriter(new FileWriter("paste.sh"))) {

            if (is == null) {
                throw new FileNotFoundException("Resource not found: /com/example/clipease/paste.sh");
            }

            String line;
            while ((line = reader.readLine()) != null) {
                writer.write(line);
                writer.newLine();
            }

            // Make the script executable
            new ProcessBuilder("chmod", "+x", "paste.sh").start().waitFor();

            // Run the script in a background thread
            new Thread(() -> {
                try {
                    new ProcessBuilder("./paste.sh").start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
