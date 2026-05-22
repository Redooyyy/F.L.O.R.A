package com.example.flora.Features.Project.UI.Card;

import com.example.flora.Features.Project.UI.ProjectDetailUI_Controller;
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

    // ── FXML ──────────────────────────────────────────────────────────────────
    @FXML private AnchorPane rootCard;
    @FXML private Rectangle  accentBar;
    @FXML private Label      projectName;
    @FXML private Label      projectLead;
    @FXML private FlowPane   devicePane;
    @FXML private FlowPane   techPane;
    @FXML private Button     statusBadge;   // NOW a Button, not a Label
    @FXML private Label      createdAtLabel;

    // ── Injected ──────────────────────────────────────────────────────────────
    private Project                    project;
    private String                     currentUserId;
    private ProjectDetailUI_Controller detailController;
    private boolean                    isLeader;

    // Status cycle — same order as AddProjectModal
    private static final String[] STATUS_OPTIONS = { "PLANNING", "ACTIVE", "ON_HOLD", "COMPLETED" };
    private static final String[] STATUS_CLASSES = { "status-todo", "status-progress", "status-review", "status-done" };
    private int statusIndex = 0;

    // Accent colours per card
    private static final String[] ACCENT_COLORS = {
            "#6D4FC2", "#34D399", "#F59E0B", "#63B3ED", "#F87171", "#A855F7"
    };
    private static int nextColor = 0;

    @Override
    public void initialize(URL location, ResourceBundle resources) { }

    // ── Public API ────────────────────────────────────────────────────────────

    /** Minimal call — default pills, PLANNING status */
    public void setData(Project project, String currentUserId,
                        ProjectDetailUI_Controller detailController) {
        setData(project, currentUserId, detailController,
                List.of("Desktop", "Mobile", "Web"),
                List.of("Java", "JavaFX", "SQL"),
                "PLANNING");
    }

    /** Full call — real data */
    public void setData(Project project, String currentUserId,
                        ProjectDetailUI_Controller detailController,
                        List<String> devices, List<String> techs, String status) {
        this.project          = project;
        this.currentUserId    = currentUserId;
        this.detailController = detailController;
        this.isLeader         = project.getOwnerId().equals(currentUserId);

        // ── Text ──────────────────────────────────────────────────────────────
        projectName.setText(project.getName());
        projectLead.setText(project.getOwnerId());
        createdAtLabel.setText(project.getCreatedAt() != null ? project.getCreatedAt() : "");

        // ── Accent bar colour ─────────────────────────────────────────────────
        accentBar.setFill(Color.web(ACCENT_COLORS[nextColor % ACCENT_COLORS.length]));
        nextColor++;

        // ── Pills ─────────────────────────────────────────────────────────────
        populatePills(devicePane, devices);
        populatePills(techPane, techs);

        // ── Status badge button ───────────────────────────────────────────────
        applyStatus(status);

        if (isLeader) {
            // Leader: clickable, shows tooltip
            statusBadge.setDisable(false);
            statusBadge.setOpacity(1.0);
            Tooltip.install(statusBadge, new Tooltip("Click to change project status"));
        } else {
            // Member: visually identical but non-interactive
            statusBadge.setDisable(true);
            // Keep full opacity so it still looks like a badge, not a disabled button
            statusBadge.setStyle("-fx-opacity: 1.0;");
        }

        // ── Click card → open project detail ─────────────────────────────────
        rootCard.setOnMouseClicked(e -> detailController.openProject(
                project, currentUserId, isLeader, () -> { }
        ));
    }

    // ── FXML: status cycle (leader only) ─────────────────────────────────────

    @FXML
    private void cycleProjectStatus() {
        if (!isLeader) return;
        statusIndex = (statusIndex + 1) % STATUS_OPTIONS.length;
        applyStatus(STATUS_OPTIONS[statusIndex]);

        // TODO: persist the new status
        // projectViewModel.updateStatus(project.getId(), STATUS_OPTIONS[statusIndex]);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

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
        // find index so we stay in sync with the cycle
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