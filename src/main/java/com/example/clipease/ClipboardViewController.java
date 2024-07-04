package com.example.clipease;

import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.AnchorPane;
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
    private ImageView settingsIcon;

    @FXML
    private AnchorPane rootPane; // Add this to your FXML file's root AnchorPane

    @FXML
    public void initialize() {
        clipboardDB = new ClipboardDB(); //initialize db
        List<String> history = clipboardDB.getClipboardHistory(); //get all previous history from db (previous clipboard contents)

        clipboardListView.getItems().addAll(history); //add previous history to list
        clipboardListView.setCellFactory(listView -> new ClipboardCell(this::deleteItem)); // Set custom cell factory

        // Add custom css file
        clipboardListView.getStylesheets().add(getClass().getResource("/com/example/clipease/styles.css").toExternalForm());
        // Start clipboard monitoring using TimdeleteClipboardContenteline
        Timeline clipboardTimeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            String currentContent = getClipboardContent();
            if (!clipboardListView.getItems().isEmpty() && !currentContent.equals(clipboardListView.getItems().get(0)) && !currentContent.isEmpty()) { // If list is not empty and new content is different
                clipboardDB.saveClipboardContent(currentContent);
                clipboardListView.getItems().add(0, currentContent); // Add to the top of the list
                if (clipboardListView.getItems().size() > 10) {
                    clipboardListView.getItems().remove(10); // Limit to top 10 items
                }
            } else if (clipboardListView.getItems().isEmpty() && !currentContent.isEmpty()) { // If list is empty
                clipboardDB.saveClipboardContent(currentContent);
                clipboardListView.getItems().add(0, currentContent); // Add first item
            }
        }));
        clipboardTimeline.setCycleCount(Timeline.INDEFINITE); // run again and again
        clipboardTimeline.play(); // start

        clipboardListView.setOnMouseClicked(event -> { // When list item is clicked
            String selectedItem = clipboardListView.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                pasteClipboardContent(selectedItem);
                deleteItem(selectedItem);
            }
        });

        settingsIcon.setOnMouseClicked(event -> {
            try {
                showSettingsView();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }


    // Show settings view
    private void showSettingsView() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("settings-view.fxml"));
        AnchorPane settingsPane = loader.load();

        // Pass the root pane to the settings controller
        SettingsController settingsController = loader.getController();
        settingsController.setRootPane(rootPane);

        // Animate the transition
        settingsPane.setTranslateX(rootPane.getWidth());
        rootPane.getChildren().add(settingsPane);

        TranslateTransition transition = new TranslateTransition(Duration.seconds(0.2), settingsPane);
        transition.setToX(0);
        transition.setOnFinished(evt -> rootPane.getChildren().remove(clipboardListView));
        transition.play();
    }

    public void deleteItem(String item) { // Delete clipboard content
        clipboardDB.deleteClipboardContent(item);
        clipboardListView.getItems().remove(item);
    }

    private String getClipboardContent() {  // Get clipboard content
        Clipboard clipboard = Clipboard.getSystemClipboard();
        if (clipboard.hasString()) {
            return clipboard.getString();
        }
        return "";
    }

    private void pasteClipboardContent(String content) { // Paste clipboard content
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent clipboardContent = new ClipboardContent();
        clipboardContent.putString(content);
        clipboard.setContent(clipboardContent);

        // Run the paste.sh script
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