package com.example.clipease;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

public class ClipboardCell extends ListCell<String> {
    private HBox hbox = new HBox();
    private Label contentLabel = new Label();
    private Button deleteButton = new Button("X");

    public ClipboardCell(ClipboardItemDeleteHandler deleteHandler) {
        super();
        hbox.setSpacing(20);
        hbox.setPadding(new Insets(5, 10, 5, 10));

        contentLabel.setPrefHeight(50); // Set fixed height
        contentLabel.setPrefWidth(170); // Set fixed width
        contentLabel.setWrapText(true); // Allow text to wrap within the label

        HBox.setHgrow(contentLabel, Priority.ALWAYS);

        deleteButton.setOnAction(event -> {
            String item = getItem();
            deleteHandler.deleteItem(item);
        });

        hbox.getChildren().addAll(contentLabel, deleteButton);
    }

    @Override
    protected void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
            setGraphic(null);
        } else {
            contentLabel.setText(item);
            setGraphic(hbox);
        }
    }

    public interface ClipboardItemDeleteHandler {
        void deleteItem(String item);
    }
}
