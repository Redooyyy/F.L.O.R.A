package com.example.flora.Features.Task.UI.Detail;

import com.example.flora.Core.Helper.DateAndTime;
import com.example.flora.Features.Task.UI.TaskUI_Controller;
import com.example.flora.Features.Task.model.Task;
import com.example.flora.Features.Task.model.TaskStatus;
import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class TaskDetailUI_Controller {

    private static final DateTimeFormatter DATE_FMT =
            DateTimeFormatter.ofPattern("dd MMM yyyy");

    private final TaskUI_Controller parentController;

    @FXML
    private Pane headerAccentBar;
    @FXML
    private Label detailTitle;
    @FXML
    private Label roleBadgeDetail;


    @FXML
    private Label detailStatus;
    @FXML
    private Button cycleStatusBtn;
    @FXML
    private Pane stepTodo;
    @FXML
    private Pane stepInProgress;
    @FXML
    private Pane stepInReview;
    @FXML
    private Pane stepDone;


    @FXML
    private Label detailDescription;


    @FXML
    private Label detailDueDate;
    @FXML
    private Button editDeadlineBtn;
    @FXML
    private VBox deadlineEditBox;
    @FXML
    private DatePicker deadlinePicker;


    @FXML
    private Pane detailAvatarDot;
    @FXML
    private Label detailAssignee;
    @FXML
    private Button reassignBtn;
    @FXML
    private VBox reassignBox;
    @FXML
    private TextField reassignField;


    @FXML
    private Label detailCreatedAt;


    @FXML
    private Button markDoneBtn;
    @FXML
    private HBox leaderActionRow;
    @FXML
    private Button deleteBtn;
    @FXML
    private Button addTaskShortcutBtn;
    @FXML
    private Button closeBtn;


    private Task boundTask;
    private boolean isLeader = false;

    public TaskDetailUI_Controller(TaskUI_Controller parentController) {
        this.parentController = parentController;
    }


    public void setLeader(boolean leader) {
        this.isLeader = leader;
        applyRoleVisibility();
    }

    private void applyRoleVisibility() {

        editDeadlineBtn.setVisible(isLeader);
        editDeadlineBtn.setManaged(isLeader);
        reassignBtn.setVisible(isLeader);
        reassignBtn.setManaged(isLeader);
        leaderActionRow.setVisible(isLeader);
        leaderActionRow.setManaged(isLeader);


        if (roleBadgeDetail != null) {
            roleBadgeDetail.setText(isLeader ? "LEADER" : "MEMBER");
            roleBadgeDetail.setStyle(isLeader
                    ? "-fx-background-color:#241E3A; -fx-text-fill:#7C6AF7; -fx-font-size:9px;"
                      + " -fx-font-family:'System Bold'; -fx-background-radius:20; -fx-padding:3 9 3 9;"
                      + " -fx-border-color:#7C6AF755; -fx-border-radius:20; -fx-border-width:1;"
                    : "-fx-background-color:#1E1C2E; -fx-text-fill:#4A475E; -fx-font-size:9px;"
                      + " -fx-font-family:'System Bold'; -fx-background-radius:20; -fx-padding:3 9 3 9;");
        }


        if (!isLeader) {
            hideDeadlineEdit();
            hideReassign();
        }
    }


    public void bind(Task task) {
        this.boundTask = task;
        hideDeadlineEdit();
        hideReassign();
        refresh();
    }

    private void refresh() {
        if (boundTask == null) return;

        detailTitle.setText(nvl(boundTask.getTitle(), "Untitled"));

        String accentColor = boundTask.isDraft() ? "#FBB024" : switch (boundTask.getStatus()) {
            case TODO -> "#3D3B55";
            case IN_PROGRESS -> "#7C6AF7";
            case IN_REVIEW -> "#F5A623";
            case DONE -> "#34D399";
        };
        headerAccentBar.setStyle("-fx-background-color:" + accentColor + "; -fx-background-radius:2px;");

        detailDescription.setText(nvl(boundTask.getDescription(), "No description provided."));

        detailDueDate.setText(nvl(boundTask.getDueDate(), "No due date"));

        LocalDate current = DateAndTime.parseDate(boundTask.getDueDate());
        deadlinePicker.setValue(current != null ? current : LocalDate.now());
        deadlinePicker.setConverter(dateConverter());

        boolean draft = boundTask.isDraft();
        detailAssignee.setText(draft ? "Unassigned  (Draft)" : boundTask.getAssigneeId());
        detailAvatarDot.setStyle(draft
                ? "-fx-background-color:#3D3B55; -fx-background-radius:50%;"
                : "-fx-background-color:#7C6AF7; -fx-background-radius:50%;");

        if (!draft && boundTask.getAssigneeId() != null)
            reassignField.setText(boundTask.getAssigneeId());

        detailCreatedAt.setText(nvl(boundTask.getCreatedAt(), "—"));

        applyStatus(boundTask.getStatus());

        markDoneBtn.setDisable(boundTask.getStatus() == TaskStatus.DONE);

        applyRoleVisibility();
    }


    @FXML
    private void onCycleStatus() {
        if (boundTask == null) return;
        TaskStatus next = switch (boundTask.getStatus()) {
            case TODO -> TaskStatus.IN_PROGRESS;
            case IN_PROGRESS -> TaskStatus.IN_REVIEW;
            case IN_REVIEW -> TaskStatus.DONE;
            case DONE -> TaskStatus.TODO;
        };
        parentController.onStatusToggled(boundTask);
        boundTask.setStatus(next);
        refresh();
    }

    @FXML
    private void onMarkDone() {
        if (boundTask == null) return;
        boundTask.setStatus(TaskStatus.DONE);
        parentController.onStatusToggled(boundTask);
        // onStatusToggled cycles once — pin it back to DONE
        boundTask.setStatus(TaskStatus.DONE);
        refresh();
    }


    @FXML
    private void onToggleDeadlineEdit() {
        if (deadlineEditBox.isVisible()) {
            hideDeadlineEdit();
        } else {
            hideReassign();   // close the other panel first
            showWithFade(deadlineEditBox);
            editDeadlineBtn.setText("✕ Close");
        }
    }

    @FXML
    private void onCancelDeadlineEdit() {
        hideDeadlineEdit();
    }

    @FXML
    private void onSaveDeadline() {
        if (boundTask == null || !isLeader) return;
        LocalDate chosen = deadlinePicker.getValue();
        parentController.onDeadlineChanged(boundTask, chosen);
        boundTask.setDueDate(chosen != null ? chosen.format(DATE_FMT) : null);
        detailDueDate.setText(nvl(boundTask.getDueDate(), "No due date"));
        hideDeadlineEdit();
    }


    @FXML
    private void onDlNudgeMinus7() {
        nudgePicker(-7);
    }

    @FXML
    private void onDlNudgeMinus1() {
        nudgePicker(-1);
    }

    @FXML
    private void onDlNudgePlus1() {
        nudgePicker(+1);
    }

    @FXML
    private void onDlNudgePlus7() {
        nudgePicker(+7);
    }

    private void nudgePicker(int days) {
        LocalDate base = deadlinePicker.getValue() != null
                ? deadlinePicker.getValue() : LocalDate.now();
        deadlinePicker.setValue(base.plusDays(days));
    }

    private void hideDeadlineEdit() {
        deadlineEditBox.setVisible(false);
        deadlineEditBox.setManaged(false);
        editDeadlineBtn.setText("✎ Edit");
    }


    @FXML
    private void onToggleReassign() {
        if (reassignBox.isVisible()) {
            hideReassign();
        } else {
            hideDeadlineEdit();   // close the other panel first
            showWithFade(reassignBox);
            reassignBtn.setText("✕ Close");
        }
    }

    @FXML
    private void onCancelReassign() {
        hideReassign();
    }

    @FXML
    private void onSaveReassign() {
        if (boundTask == null || !isLeader) return;
        String newId = reassignField.getText().trim();
        if (newId.isBlank()) return;
        parentController.onReassigned(boundTask, newId);
        boundTask.setAssigneeId(newId);
        detailAssignee.setText(newId);
        detailAvatarDot.setStyle("-fx-background-color:#7C6AF7; -fx-background-radius:50%;");
        hideReassign();
    }

    private void hideReassign() {
        reassignBox.setVisible(false);
        reassignBox.setManaged(false);
        reassignBtn.setText("↺ Reassign");
    }


    @FXML
    private void onDelete() {
        if (boundTask == null || !isLeader) return;
        parentController.onDeleteTask(boundTask);
        parentController.closeDetailPanel();
    }


    @FXML
    private void onAddTaskShortcut() {
        parentController.closeDetailPanel();
        parentController.openAddTaskPanel();
    }


    @FXML
    private void onClose() {
        parentController.closeDetailPanel();
    }


    private void applyStatus(TaskStatus status) {
        detailStatus.getStyleClass().removeAll(
                "status-todo", "status-progress", "status-late", "status-done");

        String inactive = "-fx-background-color:#2C2A40; -fx-background-radius:3px;";
        String active = "-fx-background-color:#7C6AF7; -fx-background-radius:3px;";
        String done = "-fx-background-color:#34D399; -fx-background-radius:3px;";

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


    private void showWithFade(VBox box) {
        box.setOpacity(0);
        box.setVisible(true);
        box.setManaged(true);
        FadeTransition ft = new FadeTransition(Duration.millis(200), box);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.play();
    }

    private StringConverter<LocalDate> dateConverter() {
        return new StringConverter<>() {
            @Override
            public String toString(LocalDate d) {
                return d != null ? d.format(DATE_FMT) : "";
            }

            @Override
            public LocalDate fromString(String s) {
                try {
                    return LocalDate.parse(s, DATE_FMT);
                } catch (Exception e) {
                    return null;
                }
            }
        };
    }

    private String nvl(String v, String fallback) {
        return (v != null && !v.isBlank()) ? v : fallback;
    }
}