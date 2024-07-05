package com.example.clipease;

import javafx.animation.RotateTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.prefs.Preferences;

import java.io.IOException;

public class SettingsController {

    private final Preferences preferences = Preferences.userNodeForPackage(SettingsController.class);
    private static final String DURATION_PREF_KEY = "clipboard_duration";
    private static final String DEFAULT_DURATION = "15d";


    //private Button backButton;
    @FXML
    private ImageView backButton;

    @FXML
    private AnchorPane rootPane;

    @FXML
    private ChoiceBox<String> durationChoiceBox;

    @FXML
    public void initialize() {

        // Load saved preference
        String savedDuration = preferences.get(DURATION_PREF_KEY, DEFAULT_DURATION);
        durationChoiceBox.setValue(savedDuration);

        // Save preference on change
        durationChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            preferences.put(DURATION_PREF_KEY, newValue);
        });


        // Add click handlers to back button
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

    // Public method to trigger the cleanup task
    public void startCleanupTask() {
        scheduleCleanupTask();
    }

    private void scheduleCleanupTask() {

        String duration = preferences.get(DURATION_PREF_KEY, DEFAULT_DURATION);
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        // Convert duration to days
        long days = convertDurationToDays(duration);

        Runnable cleanupTask = () -> {
            // Implement the logic to delete clipboard items older than the specified duration
            deleteOldClipboardItems(days);
        };

        // Schedule the cleanup task to run daily
        scheduler.scheduleAtFixedRate(cleanupTask, 0, 1, TimeUnit.DAYS);
    }

    private long convertDurationToDays(String duration) {
        switch (duration) {
            case "15d":
                return 15;
            case "1m":
                return 30;
            case "1y":
                return 365;
            case "lifetime":
                return Long.MAX_VALUE; // Or a very large value representing "lifetime"
            default:
                return 15; // Default duration
        }
    }
    private void deleteOldClipboardItems(long days) {
        // Implement the logic to delete clipboard items older than the specified number of days
        // This may involve running a SQL query to delete old records from the database
        // Example:
        ClipboardDB db = new ClipboardDB();
        db.deleteOldClipboardItems(days);
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
