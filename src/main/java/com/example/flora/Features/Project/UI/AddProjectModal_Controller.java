package com.example.flora.Features.Project.UI;

import com.example.flora.Features.Project.ViewModel.ProjectViewModel;
import com.example.flora.Features.Home.UI.HomeUI_Controller;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.util.Duration;

import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.ResourceBundle;

public class AddProjectModal_Controller implements Initializable {

    @FXML
    private AnchorPane addProjectPanel;
    @FXML
    private TextField nameField;
    @FXML
    private TextArea descriptionField;
    @FXML
    private Button statusBadge;
    @FXML
    private Label feedbackLabel;

    @FXML
    private FlowPane devSelectedPane;   // top zone: chips user picked
    @FXML
    private FlowPane devAvailablePane;  // bottom zone: chips to pick from
    @FXML
    private Label devEmptyLabel;     // "None selected yet" placeholder

    @FXML
    private FlowPane techSelectedPane;
    @FXML
    private FlowPane techAvailablePane;
    @FXML
    private Label techEmptyLabel;
    @FXML
    private TextField techCustomField;  // custom tech input

    private HomeUI_Controller homeController;
    private ProjectViewModel projectViewModel;
    private String ownerId;

    private final LinkedHashSet<String> selectedDevices = new LinkedHashSet<>();
    private final LinkedHashSet<String> selectedTechs = new LinkedHashSet<>();

    private static final String[] STATUS_OPTIONS = {"PLANNING", "ACTIVE", "ON_HOLD", "COMPLETED"};
    private static final String[] STATUS_CLASSES = {"status-todo", "status-progress", "status-review", "status-done"};
    private int statusIndex = 0;

    private static final String[] DEVICE_OPTIONS = {
            "Desktop", "Mobile", "Web", "Tablet", "Wearable", "TV", "CLI", "Embedded"
    };
    private static final String[] TECH_OPTIONS = {
            "Java", "JavaFX", "Kotlin", "Python", "JavaScript", "TypeScript",
            "React", "Spring Boot", "MySQL", "PostgreSQL", "SQLite", "MongoDB",
            "Firebase", "Docker", "REST API", "GraphQL", "Swift", "C++"
    };

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        applyStatusStyle(0);

        for (String opt : DEVICE_OPTIONS)
            addAvailableChip(opt, devAvailablePane, selectedDevices, devSelectedPane, devAvailablePane, devEmptyLabel);
        for (String opt : TECH_OPTIONS)
            addAvailableChip(opt, techAvailablePane, selectedTechs, techSelectedPane, techAvailablePane, techEmptyLabel);

        techCustomField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) addCustomTech();
        });
    }

    public void setValue(HomeUI_Controller homeController,
                         ProjectViewModel projectViewModel,
                         String ownerId) {
        this.homeController = homeController;
        this.projectViewModel = projectViewModel;
        this.ownerId = ownerId;
    }

    public void openPanel() {
        clearForm();
        slideEffect(-620);
    }

    public void closePanel() {
        slideEffect(620);
    }

    @FXML
    private void closePanel(ActionEvent e) {
        closePanel();
    }

    @FXML
    private void cycleStatus(ActionEvent event) {
        statusIndex = (statusIndex + 1) % STATUS_OPTIONS.length;
        applyStatusStyle(statusIndex);
    }

    @FXML
    private void addCustomTech() {
        String val = techCustomField.getText().trim();
        if (val.isEmpty() || selectedTechs.contains(val)) {
            techCustomField.clear();
            return;
        }
        techCustomField.clear();

        addSelectedChip(val, selectedTechs, techSelectedPane, techAvailablePane, techEmptyLabel, true);
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
            projectViewModel.createProject(
                    new ArrayList<>(selectedDevices),
                    new ArrayList<>(selectedTechs));
            showFeedback("✔  Project created!", true);
            PauseTransition wait = new PauseTransition(Duration.millis(700));
            wait.setOnFinished(e -> closePanel());
            wait.play();
        } catch (Exception ex) {
            showFeedback("✘  Failed to create project: " + ex.getMessage(), false);
        }
    }

    private void addAvailableChip(String val,
                                  FlowPane targetPane,
                                  LinkedHashSet<String> selSet,
                                  FlowPane selPane,
                                  FlowPane availPane,
                                  Label emptyLabel) {
        Button chip = new Button(val);
        chip.getStyleClass().add("select-chip");
        chip.setPadding(new Insets(5, 13, 5, 13));
        chip.setMnemonicParsing(false);

        chip.setOnAction(e -> {
            if (selSet.contains(val)) return;
            selSet.add(val);
            chip.getStyleClass().setAll("select-chip-active");
            chip.setText(val + "  ✕");
            availPane.getChildren().remove(chip);
            hideEmptyLabel(emptyLabel);

            chip.setScaleX(0.7);
            chip.setScaleY(0.7);
            chip.setOpacity(0);
            selPane.getChildren().add(chip);
            popIn(chip);

            chip.setOnAction(ev -> moveBackToAvail(val, chip, selSet, selPane, availPane, emptyLabel));
        });

        targetPane.getChildren().add(chip);
    }

    private void addSelectedChip(String val,
                                 LinkedHashSet<String> selSet,
                                 FlowPane selPane,
                                 FlowPane availPane,
                                 Label emptyLabel,
                                 boolean customEntry) {
        selSet.add(val);
        hideEmptyLabel(emptyLabel);

        Button chip = new Button(val + "  ✕");
        chip.getStyleClass().add("select-chip-active");
        chip.setPadding(new Insets(5, 13, 5, 13));
        chip.setMnemonicParsing(false);

        chip.setOnAction(e -> {
            if (customEntry) {
                selSet.remove(val);
                shrinkOut(chip, () -> {
                    selPane.getChildren().remove(chip);
                    showEmptyLabelIfNeeded(selPane, emptyLabel);
                });
            } else {
                moveBackToAvail(val, chip, selSet, selPane, availPane, emptyLabel);
            }
        });

        chip.setScaleX(0.7);
        chip.setScaleY(0.7);
        chip.setOpacity(0);
        selPane.getChildren().add(chip);
        popIn(chip);
    }


    private void moveBackToAvail(String val,
                                 Button chip,
                                 LinkedHashSet<String> selSet,
                                 FlowPane selPane,
                                 FlowPane availPane,
                                 Label emptyLabel) {
        selSet.remove(val);
        shrinkOut(chip, () -> {
            selPane.getChildren().remove(chip);
            showEmptyLabelIfNeeded(selPane, emptyLabel);

            chip.setText(val);
            chip.getStyleClass().setAll("select-chip");
            chip.setScaleX(1);
            chip.setScaleY(1);
            chip.setOpacity(0);
            availPane.getChildren().add(chip);

            chip.setOnAction(ev -> {
                if (selSet.contains(val)) return;
                selSet.add(val);
                chip.getStyleClass().setAll("select-chip-active");
                chip.setText(val + "  ✕");
                availPane.getChildren().remove(chip);
                hideEmptyLabel(emptyLabel);
                chip.setScaleX(0.7);
                chip.setScaleY(0.7);
                chip.setOpacity(0);
                selPane.getChildren().add(chip);
                popIn(chip);
                chip.setOnAction(ev2 -> moveBackToAvail(val, chip, selSet, selPane, availPane, emptyLabel));
            });

            FadeTransition ft = new FadeTransition(Duration.millis(150), chip);
            ft.setToValue(1);
            ft.play();
        });
    }


    private void hideEmptyLabel(Label label) {
        if (label != null) label.setVisible(false);
    }

    private void showEmptyLabelIfNeeded(FlowPane pane, Label label) {
        if (label == null) return;
        long visibleChips = pane.getChildren().stream()
                .filter(n -> n instanceof Button)
                .count();
        if (visibleChips == 0) label.setVisible(true);
    }


    private void popIn(Button chip) {
        ScaleTransition st = new ScaleTransition(Duration.millis(180), chip);
        st.setToX(1);
        st.setToY(1);
        FadeTransition ft = new FadeTransition(Duration.millis(180), chip);
        ft.setToValue(1);
        st.play();
        ft.play();
    }

    private void shrinkOut(Button chip, Runnable onDone) {
        ScaleTransition st = new ScaleTransition(Duration.millis(130), chip);
        st.setToX(0.6);
        st.setToY(0.6);
        FadeTransition ft = new FadeTransition(Duration.millis(130), chip);
        ft.setToValue(0);
        st.setOnFinished(e -> onDone.run());
        st.play();
        ft.play();
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
            bounce.setFromY(0);
            bounce.setToY(-6);
            bounce.setAutoReverse(true);
            bounce.setCycleCount(2);
            bounce.play();
        } else {
            TranslateTransition shake = new TranslateTransition(Duration.millis(60), feedbackLabel);
            shake.setFromX(0);
            shake.setToX(8);
            shake.setAutoReverse(true);
            shake.setCycleCount(6);
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
        techCustomField.clear();
        selectedDevices.clear();
        selectedTechs.clear();

        devSelectedPane.getChildren().clear();
        devAvailablePane.getChildren().clear();
        techSelectedPane.getChildren().clear();
        techAvailablePane.getChildren().clear();

        devEmptyLabel.setVisible(true);
        techEmptyLabel.setVisible(true);

        for (String opt : DEVICE_OPTIONS)
            addAvailableChip(opt, devAvailablePane, selectedDevices, devSelectedPane, devAvailablePane, devEmptyLabel);
        for (String opt : TECH_OPTIONS)
            addAvailableChip(opt, techAvailablePane, selectedTechs, techSelectedPane, techAvailablePane, techEmptyLabel);

        statusIndex = 0;
        applyStatusStyle(0);
        feedbackLabel.setVisible(false);
        feedbackLabel.setText("");
    }
}