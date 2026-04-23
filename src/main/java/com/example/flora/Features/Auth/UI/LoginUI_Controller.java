package com.example.flora.Features.Auth.UI;

import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginUI_Controller implements Initializable {
    boolean slideToggle = true;
    @FXML
    private AnchorPane slidePane;
    @FXML
    private Button signRegButton;
    @FXML
    private ImageView work;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
@FXML
void register(ActionEvent event){

    if (slideToggle) {
        signRegButton.setText("SignIn");
    } else {
        signRegButton.setText("Register");
    }
    slideLeft();
}
    void slideLeft(){
        TranslateTransition transition = new TranslateTransition();
        transition.setNode(slidePane);
        transition.setDuration(Duration.millis(500));
        transition.setByX(slideToggle?629:-629);
        transition.play();
        slideToggle=!slideToggle;
    }
}
