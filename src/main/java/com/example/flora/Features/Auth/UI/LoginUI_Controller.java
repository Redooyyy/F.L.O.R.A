package com.example.flora.Features.Auth.UI;

import com.example.flora.Features.Auth.ViewModel.AuthViewModel;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

public class LoginUI_Controller{

    private final AuthViewModel authViewModel = new AuthViewModel();

    boolean slideToggle = true;
    @FXML
    private AnchorPane slidePane;
    @FXML
    private ImageView work;
    @FXML
    private ImageView robo;
    @FXML
    private ImageView collabImg;
    @FXML
    private ImageView welcomeTxt;
    @FXML
    private Button loginButton;
    @FXML
    private Button signupButton;
    @FXML
    private Button signRegButton;
    @FXML
    private Circle disCir;
    @FXML
    private Circle cirU;
    @FXML
    private Circle cirD;
    @FXML
    private TextField password; //sign in password
    @FXML
    private TextField username;
    @FXML
    private TextField rePass;
    @FXML
    private TextField pass; //signUp password
    @FXML
    private TextField email;
    @FXML
    private TextField fullname;


    @FXML
    void register(ActionEvent event){
    if (slideToggle) {
        signRegButton.setText("LOGIN");
        } else {
        signRegButton.setText("SIGNUP");
        }
    visibilityControl();
    slider();
    }
    void slider(){
        //bar
        TranslateTransition regBlockTransition = new TranslateTransition();
        regBlockTransition.setNode(slidePane);
        regBlockTransition.setDuration(Duration.millis(500));
        regBlockTransition.setByX(slideToggle?629:-629);
        regBlockTransition.play();

        //robo
        TranslateTransition roboTransition = new TranslateTransition();
        roboTransition.setNode(robo);
        roboTransition.setDuration(Duration.millis(500));
        roboTransition.setByX(slideToggle?-950:950);
        roboTransition.play();

        //circles
        TranslateTransition cirTransition1 = new TranslateTransition();
        cirTransition1.setNode(cirD);
        cirTransition1.setDuration(Duration.millis(900));
        cirTransition1.setByX(slideToggle?-268:268);
        cirTransition1.setByY(slideToggle?35:-35);
        cirTransition1.play();

        TranslateTransition cirTransition2 = new TranslateTransition();
        cirTransition2.setNode(cirU);
        cirTransition2.setDuration(Duration.millis(900));
        cirTransition2.setByX(slideToggle?85:-85);
        cirTransition2.setByY(slideToggle?386:-386);
        cirTransition2.play();

        TranslateTransition cirTransition3 = new TranslateTransition();
        cirTransition3.setNode(disCir);
        cirTransition3.setDuration(Duration.millis(900));
        cirTransition3.setByX(slideToggle?78:-78);
        cirTransition3.setByY(slideToggle?-499:499);
        cirTransition3.play();

        slideToggle=!slideToggle;
    }


    void visibilityControl(){
       username.setVisible(!slideToggle);
       password.setVisible(!slideToggle);
       loginButton.setVisible(!slideToggle);
       collabImg.setVisible(!slideToggle);
       welcomeTxt.setVisible(!slideToggle);

       fullname.setVisible(slideToggle);
       email.setVisible(slideToggle);
       pass.setVisible(slideToggle);
       rePass.setVisible(slideToggle);
       signupButton.setVisible(slideToggle);
    }

    //TODO: email validity check
    //TODO: password validity check

    @FXML
    private void signup(ActionEvent event) {
        if(pass == rePass) authViewModel.signup(email.getText().trim(),pass.getText());
        else System.out.println("Password didn't matched"); //TODO: a warning text in UI
    }
    @FXML
    private void login(ActionEvent event) {
        authViewModel.login(email.getText().trim(),password.getText());
    }
}
