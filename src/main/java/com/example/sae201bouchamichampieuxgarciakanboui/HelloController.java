package com.example.sae201bouchamichampieuxgarciakanboui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

public class HelloController {

    @FXML
    private MenuButton timeMenuButton;

    @FXML
    public void initialize() {
        // Set the initial text for the MenuButton
        timeMenuButton.setText("10 min");
    }

    @FXML
    private void handleTimeChange(javafx.event.ActionEvent event) {
        MenuItem source = (MenuItem) event.getSource();
        timeMenuButton.setText(source.getText());
    }
}