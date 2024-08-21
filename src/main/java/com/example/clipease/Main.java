package com.example.clipease;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.Visibility;
import java.io.IOException;

import static java.lang.Thread.sleep;

public class Main extends Application {

    private Stage primaryStage;

    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("clipboard-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 400); // Set your desired width and height
        stage.setTitle("ClipEase");
        stage.setScene(scene);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setAlwaysOnTop(true);

        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds(); // Get the screen bounds

        // Set the position of the stage to the bottom right corner
        double margin = 20.0;
        stage.setX(screenBounds.getMaxX() - scene.getWidth() - margin);
        stage.setY(screenBounds.getMaxY() - scene.getHeight() - margin);


        // Add a focus listener to hide the app when it loses focus
        stage.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue && !SettingsController.isExporting()) {
                Platform.runLater(() -> {
                    ClipboardViewController clipboardViewController = new ClipboardViewController();
                    clipboardViewController.pasteClipboardContent("ishan");

                    stage.hide(); // Hide the stage instead of closing
                    addAppToTray();
                });
            }
        });

        stage.show();
    }

    public void addAppToTray() {

        try {
            sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        // Ensure AWT Toolkit is initialized
        java.awt.Toolkit.getDefaultToolkit();

        // If the system tray is not supported
        if (!SystemTray.isSupported()) {
            System.out.println("System tray not supported!");
            Platform.exit();
            return;
        }

        // Load an image for the tray icon
        java.awt.Image image = Toolkit.getDefaultToolkit().getImage(Main.class.getResource("/com/example/clipease/resources/icon.png"));

        // Set the system tray icon and menu
        final PopupMenu popup = new PopupMenu();
        final TrayIcon trayIcon = new TrayIcon(image, "ClipEase");
        final SystemTray tray = SystemTray.getSystemTray();

        // Create a popup menu components
        MenuItem openItem = new MenuItem("Open");
        openItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Platform.runLater(() -> {
                    primaryStage.show();
                    primaryStage.toFront();
                    tray.remove(trayIcon);
                });
            }
        });
        popup.add(openItem);

        MenuItem exitItem = new MenuItem("Exit");
        exitItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tray.remove(trayIcon);
                Platform.exit();
            }
        });
        popup.add(exitItem);

        trayIcon.setPopupMenu(popup);

        try {
            tray.add(trayIcon);
        } catch (AWTException e) {
            System.err.println("TrayIcon could not be added.");
        }
    }

    public static void main(String[] args) {
        launch();
    }
}
