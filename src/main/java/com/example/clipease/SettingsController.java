package com.example.clipease;

import javafx.animation.RotateTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.prefs.Preferences;
import java.nio.file.Paths;

public class SettingsController {

    private final Preferences preferences = Preferences.userNodeForPackage(SettingsController.class);
    private static final String DURATION_PREF_KEY = "clipboard_duration";
    private static final String PASTE_DURATION_PREF_KEY = "paste_duration";
    private static final String DEFAULT_DURATION = "30d";
    private static final String DEFAULT_PASTE_DURATION = "2.5";

    @FXML
    private ImageView backButton;

    @FXML
    private Button exportButton;

    @FXML
    private AnchorPane rootPane;

    @FXML
    private ChoiceBox<String> durationChoiceBox;

    @FXML
    private ChoiceBox<String> pasteDurationChoiceBox;

    private static boolean exporting = false;

    @FXML
    public void initialize() {
        // Load saved preferences
        String savedDuration = preferences.get(DURATION_PREF_KEY, DEFAULT_DURATION);
        durationChoiceBox.setValue(savedDuration);

        // Save preference on change
        durationChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                preferences.put(DURATION_PREF_KEY, newValue);
            }
        });

        // Populate choice box with options
        pasteDurationChoiceBox.getItems().addAll("1.7", "2.1", "5.1", "10.5","20.4","50.3");

        // Load saved preference or set default
        //String savedPasteDuration = preferences.get(PASTE_DURATION_PREF_KEY, DEFAULT_PASTE_DURATION);
        //pasteDurationChoiceBox.setValue(savedPasteDuration);

        // Save preference on change
        pasteDurationChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                preferences.put(PASTE_DURATION_PREF_KEY, newValue);
                updatePasteScript(newValue);
                //System.out.println(newValue);
            }
        });

        // Add click handler to export button
        exportButton.setOnAction(event -> exportToTextFile());

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
            rotateTransition.setToAngle(360); // Rotate 360 degrees
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

    private void updatePasteScript(String duration) {

        //String duration = preferences.get(PASTE_DURATION_PREF_KEY, DEFAULT_PASTE_DURATION);

        // Update paste.sh file with the selected duration
        String scriptContent = "#!/bin/bash\n" +
                "sleep " + duration + "\n" +
                "xdotool key ctrl+v\n";

        Path scriptFile = Paths.get("src/main/resources/com/example/clipease/paste.sh");
        try (FileWriter writer = new FileWriter(scriptFile.toFile())) {
            writer.write(scriptContent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteOldClipboardItems() {
        String duration = preferences.get(DURATION_PREF_KEY, DEFAULT_DURATION);
        long days = convertDurationToDays(duration);
        ClipboardDB.deleteOldClipboardItems(days);
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
                return Long.MAX_VALUE;
            default:
                return 15;
        }
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

    private void exportToTextFile() {
        //exporting = true;
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Text File");
        fileChooser.setInitialFileName("clipboard_history.txt");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));

        // Get the primary screen bounds
        Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();

        // Center the FileChooser dialog
        Stage tempStage = new Stage();
        tempStage.initStyle(StageStyle.UTILITY);
        tempStage.setOpacity(0); // Make the stage invisible
        tempStage.setAlwaysOnTop(true); // Keep it on top
        tempStage.setX(primaryScreenBounds.getMinX() + primaryScreenBounds.getWidth() / 2 - 200); // Approximate width of FileChooser
        tempStage.setY(primaryScreenBounds.getMinY() + primaryScreenBounds.getHeight() / 2 - 200); // Approximate height of FileChooser
        tempStage.show();

        File file = fileChooser.showSaveDialog(tempStage);

        if (file != null) {
            try (FileWriter writer = new FileWriter(file)) {
                ClipboardDB db = new ClipboardDB();
                List<String> clipboardHistory = db.getClipboardHistory();

                int counter = 1;
                for (String content : clipboardHistory) {
                    writer.append(counter + "." + content.replace("\n", " ").replace("\r", " ")).append("\n");
                    counter++;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Close the temporary stage
        tempStage.close();
    }

    public static boolean isExporting() {
        return exporting;
    }
}