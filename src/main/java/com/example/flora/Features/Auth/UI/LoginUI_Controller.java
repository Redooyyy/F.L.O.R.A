package com.example.flora.Features.Auth.UI;

import com.example.flora.Core.DI.AppContainer;
import com.example.flora.Core.Transition.ErrorTransition;
import com.example.flora.Core.Transition.SceneTransition;
import com.example.flora.Core.Validation.ValidationHelper;
import com.example.flora.Core.session.UserSession;
import com.example.flora.Features.Auth.exception.AuthException;
import com.example.flora.Features.Auth.model.User;
import com.example.flora.Features.Auth.ViewModel.AuthViewModel;
import com.example.flora.Features.Home.UI.HomeUI_Controller;
import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

public class LoginUI_Controller {

    private final AuthViewModel authViewModel;
    private final AppContainer appContainer;

    private boolean showingLogin = true; // true = login panel visible

    @FXML
    private AnchorPane mainPane;
    @FXML
    private AnchorPane slidePane;

    @FXML
    private TextField username;   // email on login side
    @FXML
    private TextField password;

    @FXML
    private TextField fullname;
    @FXML
    private TextField email;
    @FXML
    private TextField pass;
    @FXML
    private TextField rePass;

    @FXML
    private Button loginButton;
    @FXML
    private Button signupButton;
    @FXML
    private Button signRegButton;

    @FXML
    private ImageView work;
    @FXML
    private ImageView robo;
    @FXML
    private ImageView collabImg;
    @FXML
    private ImageView welcomeTxt;
    @FXML
    private Circle disCir;
    @FXML
    private Circle cirU;
    @FXML
    private Circle cirD;


    public LoginUI_Controller(AuthViewModel authViewModel, AppContainer appContainer) {
        this.authViewModel = authViewModel;
        this.appContainer = appContainer;
    }


    @FXML
    void register(ActionEvent event) {
        signRegButton.setText(showingLogin ? "LOGIN" : "SIGNUP");
        applyPanelVisibility();
        animateSlide();
    }

    private void animateSlide() {
        int dir = showingLogin ? 1 : -1;  // positive = slide right

        slide(slidePane, dir * 629, 500);
        slide(robo, dir * -950, 500);
        slide(cirD, dir * -268, 900, dir * 35);
        slide(cirU, dir * 85, 900, dir * 386);
        slide(disCir, dir * 78, 900, dir * -499);

        showingLogin = !showingLogin;
    }

    private void applyPanelVisibility() {

        boolean goingToSignup = showingLogin;

        username.setVisible(!goingToSignup);
        password.setVisible(!goingToSignup);
        loginButton.setVisible(!goingToSignup);
        collabImg.setVisible(!goingToSignup);
        welcomeTxt.setVisible(!goingToSignup);

        fullname.setVisible(goingToSignup);
        email.setVisible(goingToSignup);
        pass.setVisible(goingToSignup);
        rePass.setVisible(goingToSignup);
        signupButton.setVisible(goingToSignup);
    }


    @FXML
    private void login(ActionEvent event) throws IOException {
       ErrorTransition.clearAllErrors(username, password);

        String emailVal = username.getText().trim();
        String passwordVal = password.getText();


        String validationError = ValidationHelper.validateLoginFields(emailVal, passwordVal);
        if (validationError != null) {
            handleValidationError(validationError, emailVal, passwordVal);
            return;
        }


        try {
            User user = authViewModel.login(emailVal, passwordVal);
            UserSession.setUser(user);
            appContainer.initUserSession();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            new SceneTransition(stage).switchFromLogin(
                    "/Home/UI/HomeUI.fxml",
                    c -> new HomeUI_Controller(appContainer, appContainer.getNotificationViewModel())
            );

        } catch (AuthException ex) {
            handleAuthException(ex);
        }
    }


    private void handleValidationError(String message, String emailVal, String passwordVal) {
        if (message.equals(ValidationHelper.Msg.EMAIL_EMPTY) ||
                message.equals(ValidationHelper.Msg.EMAIL_INVALID)) {
            ErrorTransition.failField(username, message, mainPane);
        } else {
            ErrorTransition.failField(password, message, mainPane);
        }
    }


    @FXML
    private void signup(ActionEvent event) {
        ErrorTransition.clearAllErrors(fullname, email, pass, rePass);

        String fullnameVal = fullname.getText().trim();
        String emailVal = email.getText().trim();
        String passVal = pass.getText();
        String rePassVal = rePass.getText();


        String validationError = ValidationHelper.validateSignupFields(emailVal, passVal, rePassVal, fullnameVal);
        if (validationError != null) {
            routeSignupError(validationError);
            return;
        }

        try {
            User user = authViewModel.signup(emailVal, passVal);
            System.out.println("Called 1");
            UserSession.setUser(user);
            System.out.println("Called 2 -_-");
            ErrorTransition.showSuccess("Account created! Please log in.", mainPane);
            System.out.println("Called -_-");
            clearSignupFields();
            PauseTransition pause =
                    new javafx.animation.PauseTransition(Duration.seconds(1.8));
            pause.setOnFinished(this::register);
            pause.play();

        } catch (AuthException ex) {
            handleAuthException(ex);
            System.out.println("Called -_-");
        }
    }

    private void routeSignupError(String message) {
        if (message.equals(ValidationHelper.Msg.FULLNAME_EMPTY)) {
            ErrorTransition.failField(fullname, message, mainPane);
        } else if (message.equals(ValidationHelper.Msg.EMAIL_EMPTY) ||
                message.equals(ValidationHelper.Msg.EMAIL_INVALID)) {
            ErrorTransition.failField(email, message, mainPane);
        } else if (message.equals(ValidationHelper.Msg.PASSWORD_EMPTY) ||
                message.equals(ValidationHelper.Msg.PASSWORD_TOO_SHORT)) {
            ErrorTransition.failField(pass, message, mainPane);
        } else if (message.equals(ValidationHelper.Msg.CONFIRM_EMPTY) ||
                message.equals(ValidationHelper.Msg.PASSWORDS_MISMATCH)) {
            ErrorTransition.failFields(message, mainPane, pass, rePass);
        } else {
            ErrorTransition.showError(message, mainPane);
        }
    }

    private void handleAuthException(AuthException ex) {
        switch (ex.getReason()) {
            case INVALID_CREDENTIALS -> ErrorTransition.failFields(
                    ValidationHelper.Msg.WRONG_CREDENTIALS, mainPane, username, password);
            case USER_ALREADY_EXISTS -> ErrorTransition.failField(
                    email, ValidationHelper.Msg.USER_EXISTS, mainPane);
            case SERVER_ERROR -> ErrorTransition.showError(
                    ValidationHelper.Msg.SERVER_ERROR, mainPane);
        }
    }


    private void clearSignupFields() {
        fullname.clear();
        email.clear();
        pass.clear();
        rePass.clear();
    }

    private void slide(Node node, double byX, double durationMs) {
        slide(node, byX, durationMs, 0);
    }

    private void slide(Node node, double byX, double durationMs, double byY) {
        TranslateTransition tt = new TranslateTransition(Duration.millis(durationMs), node);
        tt.setByX(byX);
        if (byY != 0) tt.setByY(byY);
        tt.play();
    }
}