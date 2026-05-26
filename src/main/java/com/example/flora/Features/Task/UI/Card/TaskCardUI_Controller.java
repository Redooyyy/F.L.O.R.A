package com.example.flora.Features.Task.UI.Card;

import com.example.flora.Features.Task.UI.TaskUI_Controller;
import com.example.flora.Features.Task.model.Task;
import com.example.flora.Features.Task.model.TaskStatus;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

public class TaskCardUI_Controller {

    private final TaskUI_Controller parentController;

    @FXML
    private Pane accentBar;
    @FXML
    private Label taskTitle;
    @FXML
    private Label subTitle;
    @FXML
    private Label dueDate;
    @FXML
    private Label assigneeLabel;
    @FXML
    private Pane avatarDot;
    @FXML
    private Label taskStatus;

    private Task boundTask;

    public TaskCardUI_Controller(TaskUI_Controller parentController) {
        this.parentController = parentController;
    }

    public void bind(Task task) {
        this.boundTask = task;

        boolean draft = task.isDraft();

        taskTitle.setText(task.getTitle() != null ? task.getTitle() : "");
        subTitle.setText(task.getDescription() != null ? task.getDescription() : "");
        dueDate.setText(task.getDueDate() != null ? task.getDueDate() : "No due date");


        if (draft) {
            assigneeLabel.setText("✏  Draft — unassigned");
            assigneeLabel.setStyle("-fx-text-fill:#FBB024;");
            avatarDot.setStyle("-fx-background-color:#3D3B55; -fx-background-radius:50%;");
        } else {
            assigneeLabel.setText(task.getAssigneeId() != null
                    ? "@" + task.getAssigneeId() : "");
            assigneeLabel.setStyle("-fx-text-fill:#7A778F;");
            avatarDot.setStyle("-fx-background-color:#7C6AF7; -fx-background-radius:50%;");
        }

        applyStatus(taskStatus, task.getStatus());
        applyAccentBar(task.getStatus(), draft);
    }

    @FXML
    private void selectedTaskCard() {
        if (boundTask != null) parentController.onTaskSelected(boundTask);
    }

    @FXML
    private void toggleStatus() {
        if (boundTask != null) {
            parentController.onStatusToggled(boundTask);

            TaskStatus next = switch (boundTask.getStatus()) {
                case TODO -> TaskStatus.IN_PROGRESS;
                case IN_PROGRESS -> TaskStatus.IN_REVIEW;
                case IN_REVIEW -> TaskStatus.DONE;
                case DONE -> TaskStatus.TODO;
            };
            boundTask.setStatus(next);
            applyStatus(taskStatus, next);
            applyAccentBar(next, boundTask.isDraft());
        }
    }

    private void applyStatus(Label label, TaskStatus status) {
        label.getStyleClass().removeAll(
                "status-progress", "status-done", "status-todo", "status-late");
        if (status == null) {
            label.setText("To Do");
            label.getStyleClass().add("status-todo");
            return;
        }
        switch (status) {
            case IN_PROGRESS -> {
                label.setText("In Progress");
                label.getStyleClass().add("status-progress");
            }
            case DONE -> {
                label.setText("Done");
                label.getStyleClass().add("status-done");
            }
            case IN_REVIEW -> {
                label.setText("In Review");
                label.getStyleClass().add("status-late");
            }
            default -> {
                label.setText("To Do");
                label.getStyleClass().add("status-todo");
            }
        }
    }

    private void applyAccentBar(TaskStatus status, boolean draft) {
        if (accentBar == null) return;
        String color = draft ? "#FBB024" : switch (status) {
            case TODO -> "#4A5568";
            case IN_PROGRESS -> "#7C6AF7";
            case IN_REVIEW -> "#F5A623";
            case DONE -> "#34D399";
        };
        accentBar.setStyle("-fx-background-color:" + color
                + "; -fx-background-radius:14 0 0 14;");
    }
}