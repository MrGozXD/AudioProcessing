package com.example.demo;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

//HelloController = ensemble des fonctions sous les <Button text="nomDuBouton" onAction="#fonctionDésirée">
public class HelloController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }


}