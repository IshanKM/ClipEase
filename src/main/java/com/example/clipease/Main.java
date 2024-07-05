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
        stage.initStyle(StageStyle.UNDECORATED); //this will hide tittle bar
        stage.setAlwaysOnTop(true);

        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds(); // Get the screen boundsUNDECORATED

        // Set the position of the stage to the bottom right corner
        double margin = 20.0;
        stage.setX(screenBounds.getMaxX() - scene.getWidth() - margin);
        stage.setY(screenBounds.getMaxY() - scene.getHeight() - margin);


        //stage.initStyle(StageStyle.UNDECORATED);

        // Add a focus listener to close the app when it loses focus
        stage.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) { // If the window loses focus
                stage.close();
            }
        });

        SettingsController settingsController = new SettingsController();
        settingsController.startCleanupTask();

        stage.show();


    }

    public static void main(String[] args) {
        launch();
    }
}
