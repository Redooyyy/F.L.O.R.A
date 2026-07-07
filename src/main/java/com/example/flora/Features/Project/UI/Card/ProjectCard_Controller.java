package com.example.flora.Features.Project.UI.Card;

import com.example.flora.Features.Project.UI.ProjectDetailUI_Controller;
import com.example.flora.Features.Project.ViewModel.ProjectViewModel;
import com.example.flora.Features.Project.model.Project;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ProjectCard_Controller implements Initializable {

    @FXML
    private AnchorPane rootCard;
    @FXML
    private Rectangle  accentBar;
    @FXML
    private Label      projectName;
    @FXML
    private Label      projectLead;
    @FXML
    private FlowPane   devicePane;
    @FXML
    private FlowPane   techPane;
    @FXML
    private Button     statusBadge;
    @FXML
    private Label      createdAtLabel;

    private Project project;
    private ProjectViewModel projectViewModel;
    private ProjectDetailUI_Controller detailController;
    private boolean isLeader;

    private static final String[] STATUS_OPTIONS = { "PLANNING", "ACTIVE", "ON_HOLD", "COMPLETED" };
    private static final String[] STATUS_CLASSES = { "status-todo", "status-progress", "status-review", "status-done" };
    private int statusIndex = 0;

    private static final String[] ACCENT_COLORS = {
            "#6D4FC2", "#34D399", "#F59E0B", "#63B3ED", "#F87171", "#A855F7"
    };
    private static int nextColor = 0;

    @Override
    public void initialize(URL location, ResourceBundle resources) { }


    public void setData(Project project, ProjectViewModel projectViewModel,
                        ProjectDetailUI_Controller detailController) {
        setData(project, projectViewModel, detailController,
                project.getDevices() != null && !project.getDevices().isEmpty() ? project.getDevices() : List.of("Desktop", "Mobile", "Web"),
                project.getTechs() != null && !project.getTechs().isEmpty() ? project.getTechs() : List.of("Java", "JavaFX", "SQL"),
                project.getStatus() != null ? project.getStatus() : "PLANNING");
    }

    public void setData(Project project, ProjectViewModel projectViewModel, ProjectDetailUI_Controller detailController, List<String> devices, List<String> techs, String status) {
        this.project          = project;
        this.projectViewModel = projectViewModel;
        this.detailController = detailController;
        this.isLeader         = projectViewModel.isLeaderOf(project);

        projectName.setText(project.getName());
        String leaderUsername = detailController != null ? detailController.getUsernameById(project.getOwnerId()) : project.getOwnerId();
        projectLead.setText("@" + leaderUsername);
        createdAtLabel.setText(project.getCreatedAt() != null ? project.getCreatedAt() : "");


        accentBar.setFill(Color.web(ACCENT_COLORS[nextColor % ACCENT_COLORS.length]));
        nextColor++;


        populatePills(devicePane, devices);
        populatePills(techPane, techs);


        applyStatus(status);

        if (isLeader) {
            statusBadge.setDisable(false);
            statusBadge.setOpacity(1.0);
            Tooltip.install(statusBadge, new Tooltip("Click to change project status"));
        } else {
            statusBadge.setDisable(true);
            statusBadge.setStyle("-fx-opacity: 1.0;");
        }

        rootCard.setOnMouseClicked(e -> detailController.openProject(
                project, projectViewModel.getCurrUserID(), isLeader, () -> { }
        ));
    }


    @FXML
    private void cycleProjectStatus() {
        if (!isLeader) return;
        statusIndex = (statusIndex + 1) % STATUS_OPTIONS.length;
        String newStatus = STATUS_OPTIONS[statusIndex];
        applyStatus(newStatus);

        project.setStatus(newStatus);
        projectViewModel.updateProject(project);
    }


    private void populatePills(FlowPane pane, List<String> items) {
        pane.getChildren().clear();
        for (String item : items) {
            Label pill = new Label(item);
            pill.getStyleClass().add("card-pill");
            pill.setPadding(new Insets(3, 9, 3, 9));
            pane.getChildren().add(pill);
        }
    }

    private void applyStatus(String status) {
        statusBadge.getStyleClass().removeAll(STATUS_CLASSES);
        for (int i = 0; i < STATUS_OPTIONS.length; i++) {
            if (STATUS_OPTIONS[i].equalsIgnoreCase(status)) { statusIndex = i; break; }
        }
        String cls = switch (status == null ? "" : status.toUpperCase()) {
            case "ACTIVE", "IN_PROGRESS" -> "status-progress";
            case "DONE", "COMPLETED"     -> "status-done";
            case "ON_HOLD", "IN_REVIEW"  -> "status-review";
            default                      -> "status-todo";
        };
        statusBadge.getStyleClass().add(cls);
        statusBadge.setText(status != null ? status : "PLANNING");
    }
}