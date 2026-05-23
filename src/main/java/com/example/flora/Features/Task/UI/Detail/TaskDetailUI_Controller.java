package com.example.flora.Features.Task.UI.Detail;

import com.example.flora.Features.Task.UI.TaskUI_Controller;
import com.example.flora.Features.Task.model.Task;
import com.example.flora.Features.Task.model.TaskStatus;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

/**
 * TaskDetailUI_Controller
 *
 * Bound to TaskDetailUI.fxml.
 * Displays full task details and lets the user:
 *   - Cycle through TODO → IN_PROGRESS → IN_REVIEW → DONE (and back)
 *   - Mark as Done directly
 *   - Delete the task
 *   - Close the panel
 */
public class TaskDetailUI_Controller {

    // ── Injected via constructor ───────────────────────────────────────────────
    private final TaskUI_Controller parentController;

    // ── FXML bindings ──────────────────────────────────────────────────────────
    @FXML private Label  detailTitle;
    @FXML private Label  detailDescription;
    @FXML private Label  detailStatus;
    @FXML private Label  detailDueDate;
    @FXML private Label  detailAssignee;
    @FXML private Label  detailCreatedAt;

    @FXML private Pane   detailAvatarDot;

    // Progress step dots
    @FXML private Pane   stepTodo;
    @FXML private Pane   stepInProgress;
    @FXML private Pane   stepInReview;
    @FXML private Pane   stepDone;

    @FXML private Button cycleStatusBtn;
    @FXML private Button markDoneBtn;
    @FXML private Button deleteBtn;

    // ── Bound task ─────────────────────────────────────────────────────────────
    private Task boundTask;

    // ── Constructor ────────────────────────────────────────────────────────────
    public TaskDetailUI_Controller(TaskUI_Controller parentController) {
        this.parentController = parentController;
    }

    // ── Binding ────────────────────────────────────────────────────────────────

    /** Call this after loading the FXML to populate every field. */
    public void bind(Task task) {
        this.boundTask = task;
        refresh();
    }

    /** Re-render all fields from the current boundTask state. */
    private void refresh() {
        if (boundTask == null) return;

        detailTitle.setText(nvl(boundTask.getTitle(), "Untitled"));
        detailDescription.setText(nvl(boundTask.getDescription(), "No description provided."));
        detailDueDate.setText(nvl(boundTask.getDueDate(), "No due date"));
        detailCreatedAt.setText(nvl(boundTask.getCreatedAt(), "—"));

        // Assignee
        boolean isDraft = boundTask.isDraft();
        detailAssignee.setText(isDraft ? "Unassigned (Draft)" : boundTask.getAssigneeId());
        detailAvatarDot.setStyle(isDraft
                ? "-fx-background-color:#3D3B55; -fx-background-radius:50%;"
                : "-fx-background-color:#7C6AF7; -fx-background-radius:50%;");

        // Status chip + progress dots
        applyStatus(boundTask.getStatus());

        // If already done, disable Mark as Done button
        markDoneBtn.setDisable(boundTask.getStatus() == TaskStatus.DONE);
    }

    // ── FXML handlers ──────────────────────────────────────────────────────────

    /** Cycles status: TODO → IN_PROGRESS → IN_REVIEW → DONE → TODO */
    @FXML
    private void onCycleStatus() {
        if (boundTask == null) return;
        TaskStatus next = switch (boundTask.getStatus()) {
            case TODO        -> TaskStatus.IN_PROGRESS;
            case IN_PROGRESS -> TaskStatus.IN_REVIEW;
            case IN_REVIEW   -> TaskStatus.DONE;
            case DONE        -> TaskStatus.TODO;
        };
        parentController.onStatusToggled(boundTask);   // updates VM + repository
        boundTask.setStatus(next);                     // keep local copy in sync
        refresh();
    }

    /** Instantly marks the task Done. */
    @FXML
    private void onMarkDone() {
        if (boundTask == null) return;
        parentController.onMarkDone();
        boundTask.setStatus(TaskStatus.DONE);
        refresh();
    }

    /** Deletes the task and closes the panel. */
    @FXML
    private void onDelete() {
        if (boundTask == null) return;
        parentController.onDeleteTask(boundTask);
        parentController.closeDetailPanel();
    }

    /** Closes the detail panel without any changes. */
    @FXML
    private void onClose() {
        parentController.closeDetailPanel();
    }

    // ── Status styling ─────────────────────────────────────────────────────────

    private void applyStatus(TaskStatus status) {
        // Reset chip classes
        detailStatus.getStyleClass().removeAll(
                "status-todo", "status-progress", "status-late", "status-done");

        // Reset all step dots to inactive
        String inactive = "-fx-background-color:#2C2A40; -fx-background-radius:3px;";
        String active   = "-fx-background-color:#7C6AF7; -fx-background-radius:3px;";
        String done     = "-fx-background-color:#34D399; -fx-background-radius:3px;";

        stepTodo.setStyle(inactive);
        stepInProgress.setStyle(inactive);
        stepInReview.setStyle(inactive);
        stepDone.setStyle(inactive);

        if (status == null) status = TaskStatus.TODO;

        switch (status) {
            case TODO -> {
                detailStatus.setText("To Do");
                detailStatus.getStyleClass().add("status-todo");
                stepTodo.setStyle(active);
                cycleStatusBtn.setText("Start →");
            }
            case IN_PROGRESS -> {
                detailStatus.setText("In Progress");
                detailStatus.getStyleClass().add("status-progress");
                stepTodo.setStyle(done);
                stepInProgress.setStyle(active);
                cycleStatusBtn.setText("Send to Review →");
            }
            case IN_REVIEW -> {
                detailStatus.setText("In Review");
                detailStatus.getStyleClass().add("status-late");
                stepTodo.setStyle(done);
                stepInProgress.setStyle(done);
                stepInReview.setStyle(active);
                cycleStatusBtn.setText("Mark Done →");
            }
            case DONE -> {
                detailStatus.setText("Done");
                detailStatus.getStyleClass().add("status-done");
                stepTodo.setStyle(done);
                stepInProgress.setStyle(done);
                stepInReview.setStyle(done);
                stepDone.setStyle(done);
                cycleStatusBtn.setText("Reopen →");
            }
        }
    }

    // ── Helpers ────────────────────────────────────────────────────────────────
    private String nvl(String value, String fallback) {
        return (value != null && !value.isBlank()) ? value : fallback;
    }
}