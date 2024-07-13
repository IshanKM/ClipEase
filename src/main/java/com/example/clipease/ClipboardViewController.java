package com.example.clipease;

import javafx.animation.RotateTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
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
    private Pane borderPane;

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

        // to Delete old clipboard items
        Timeline checkdurationDB = new Timeline(new KeyFrame(Duration.hours(24), event -> {
            // Delete old clipboard items
            SettingsController settingsController = new SettingsController();
            settingsController.deleteOldClipboardItems();
        }));
        checkdurationDB.setCycleCount(Timeline.INDEFINITE); // run again and again
        checkdurationDB.play();

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

        // Add rotate effect handlers
        settingsIcon.setOnMouseEntered(event -> {
            RotateTransition rotateTransition = new RotateTransition(Duration.seconds(0.5), settingsIcon);
            rotateTransition.setToAngle(180); // Rotate 90 degrees
            rotateTransition.play();
        });

        settingsIcon.setOnMouseExited(event -> {
            RotateTransition rotateTransition = new RotateTransition(Duration.seconds(0.5), settingsIcon);
            rotateTransition.setToAngle(0); // Rotate back to 0 degrees
            rotateTransition.play();
        });

        //createRunningBorderEffect();
    }

    private void createRunningBorderEffect() {
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(0), event -> borderPane.setStyle("-fx-border-color: blue transparent transparent transparent; -fx-border-width: 2;")),
                new KeyFrame(Duration.seconds(0.25), event -> borderPane.setStyle("-fx-border-color: transparent blue transparent transparent; -fx-border-width: 2;")),
                new KeyFrame(Duration.seconds(0.5), event -> borderPane.setStyle("-fx-border-color: transparent transparent blue transparent; -fx-border-width: 2;")),
                new KeyFrame(Duration.seconds(0.75), event -> borderPane.setStyle("-fx-border-color: transparent transparent transparent blue; -fx-border-width: 2;")),
                new KeyFrame(Duration.seconds(1), event -> borderPane.setStyle("-fx-border-color: blue; -fx-border-width: 2;"))
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
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

    private void pasteClipboardContent(String content) {
        // Set clipboard content
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent clipboardContent = new ClipboardContent();
        clipboardContent.putString(content);
        clipboard.setContent(clipboardContent);

        // Run the script
        try {
            // Make the script executable
            new ProcessBuilder("chmod", "+x", "src/main/resources/com/example/clipease/paste.sh").start().waitFor();

            // Run the script in a background thread
            new Thread(() -> {
                try {
                    new ProcessBuilder("./src/main/resources/com/example/clipease/paste.sh").start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

}