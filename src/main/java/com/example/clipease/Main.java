package com.example.clipease;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("clipboard-view.fxml"));

        //this will hide tittle bar
        //stage.initStyle(StageStyle.UNDECORATED);
        stage.initStyle(StageStyle.UTILITY);
        Scene scene = new Scene(fxmlLoader.load(), 300, 400);
        stage.setTitle("ClipEase");
        stage.setScene(scene);
        stage.setAlwaysOnTop(true);
        stage.show();

    }

    public static void main(String[] args) {
        launch();
    }
}
