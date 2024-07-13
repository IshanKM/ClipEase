package com.example.clipease;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("clipboard-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 310, 400); // Set your desired width and height
        stage.setTitle("ClipEase");
        stage.setScene(scene);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setAlwaysOnTop(true);

        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds(); // Get the screen bounds

        // Set the position of the stage to the bottom right corner
        double margin = 20.0;
        stage.setX(screenBounds.getMaxX() - scene.getWidth() - margin);
        stage.setY(screenBounds.getMaxY() - scene.getHeight() - margin);


        // Add a focus listener to close the app when it loses focus
       /* stage.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue && !SettingsController.isExporting()) {
                stage.close();
            }
        }); */
        stage.show();
    }
    public static void main(String[] args) {
        launch();
    }
}
