package com.example.flora.Features.test.ui;

import com.example.flora.Core.Transition.SceneTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick(ActionEvent event) throws IOException {
        Stage stage = (Stage)(((Node)event.getSource()).getScene().getWindow());
        SceneTransition sceneTransition = new SceneTransition(stage);

        sceneTransition.switchFromLogin("/Auth/UI/LoginUI.fxml");
    }
}
