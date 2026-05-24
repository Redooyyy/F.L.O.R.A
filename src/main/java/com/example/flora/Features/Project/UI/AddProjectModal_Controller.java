package com.example.flora.Features.Project.UI;

import com.example.flora.Features.Project.ViewModel.ProjectViewModel;
import com.example.flora.Features.Home.UI.HomeUI_Controller;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;

public class AddProjectModal_Controller implements Initializable {


    @FXML private AnchorPane addProjectPanel;
    @FXML private TextField  nameField;
    @FXML private TextArea   descriptionField;
    @FXML private Button     statusBadge;
    @FXML private Label      feedbackLabel;

    private HomeUI_Controller homeController;
    private ProjectViewModel  projectViewModel;
    private String            ownerId;

    private static final String[] STATUS_OPTIONS  = { "PLANNING", "ACTIVE", "ON_HOLD", "COMPLETED" };
    private static final String[] STATUS_CLASSES  = { "status-todo", "status-progress", "status-review", "status-done" };
    private int statusIndex = 0;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        applyStatusStyle(0);
    }


    public void setValue(HomeUI_Controller homeController,
                         ProjectViewModel  projectViewModel,
                         String            ownerId) {
        this.homeController   = homeController;
        this.projectViewModel = projectViewModel;
        this.ownerId          = ownerId;
    }

    public void openPanel() {
        clearForm();
        slideEffect(-620);   // move left into view (panel sits at x=620 off-screen)
    }

    public void closePanel() {
        slideEffect(620);
    }

    @FXML
    private void closePanel(ActionEvent event) {
        closePanel();
    }

    @FXML
    private void cycleStatus(ActionEvent event) {
        statusIndex = (statusIndex + 1) % STATUS_OPTIONS.length;
        applyStatusStyle(statusIndex);
    }

    @FXML
    private void createProject(ActionEvent event) {
        String name = nameField.getText().trim();
        String desc = descriptionField.getText().trim();

        if (name.isEmpty()) {
            showFeedback("⚠  Project name cannot be empty.", false);
            return;
        }
        if (name.length() > 80) {
            showFeedback("⚠  Name too long (max 80 characters).", false);
            return;
        }

        projectViewModel.projectNameProperty().set(name);
        projectViewModel.descriptionProperty().set(desc);

        try {
            projectViewModel.createProject();
            showFeedback("✔  Project created!", true);

            PauseTransition wait =
                    new PauseTransition(Duration.millis(700));
            wait.setOnFinished(e -> closePanel());
            wait.play();

        } catch (Exception ex) {
            showFeedback("✘  Failed to create project: " + ex.getMessage(), false);
        }
    }

    private void slideEffect(double toX) {
        TranslateTransition tt = new TranslateTransition(Duration.millis(400), addProjectPanel);
        tt.setToX(toX);
        tt.play();
    }

    private void applyStatusStyle(int index) {
        statusBadge.getStyleClass().removeAll(STATUS_CLASSES);
        statusBadge.getStyleClass().add(STATUS_CLASSES[index]);
        statusBadge.setText(STATUS_OPTIONS[index]);
    }

    private void showFeedback(String message, boolean success) {
        feedbackLabel.setText(message);
        feedbackLabel.getStyleClass().removeAll("modal-feedback-ok", "modal-feedback-err");
        feedbackLabel.getStyleClass().add(success ? "modal-feedback-ok" : "modal-feedback-err");
        feedbackLabel.setOpacity(1.0);
        feedbackLabel.setVisible(true);

        if (success) {
            TranslateTransition bounce = new TranslateTransition(Duration.millis(150), feedbackLabel);
            bounce.setFromY(0); bounce.setToY(-6);
            bounce.setAutoReverse(true); bounce.setCycleCount(2);
            bounce.play();
        } else {
            TranslateTransition shake = new TranslateTransition(Duration.millis(60), feedbackLabel);
            shake.setFromX(0); shake.setToX(8);
            shake.setAutoReverse(true); shake.setCycleCount(6);
            shake.play();
        }

        FadeTransition fadeOut = new FadeTransition(Duration.millis(400), feedbackLabel);
        fadeOut.setToValue(0.0);
        fadeOut.setDelay(Duration.seconds(2));
        fadeOut.setOnFinished(e -> {
            feedbackLabel.setVisible(false);
            feedbackLabel.setOpacity(1.0);
            feedbackLabel.setTranslateX(0);
            feedbackLabel.setTranslateY(0);
        });
        fadeOut.play();
    }

    private void clearForm() {
        nameField.clear();
        descriptionField.clear();
        statusIndex = 0;
        applyStatusStyle(0);
        feedbackLabel.setVisible(false);
        feedbackLabel.setText("");
    }
}
