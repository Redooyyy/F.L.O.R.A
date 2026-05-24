package com.example.flora.Features.Task.UI.Card;

import com.example.flora.Features.Task.UI.TaskUI_Controller;
import com.example.flora.Features.Task.model.Task;
import com.example.flora.Features.Task.model.TaskStatus;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class TaskCardUI_Controller {

    private final TaskUI_Controller parentController;

    @FXML
    private Label taskTitle;
    @FXML
    private Label dueDate;
    @FXML
    private Label taskStatus;
    @FXML
    private Label subTitle;

    private Task boundTask;


    public TaskCardUI_Controller(TaskUI_Controller parentController) {
        this.parentController = parentController;
    }

    public void bind(Task task) {
        this.boundTask = task;

        taskTitle.setText(task.getTitle() != null ? task.getTitle() : "");
        subTitle.setText(task.getDescription() != null ? task.getDescription() : "");
        dueDate.setText(task.getDueDate() != null ? task.getDueDate() : "No due date");

        applyStatus(taskStatus, task.getStatus());
    }


    @FXML
    private void selectedTaskCard() {
        if (boundTask != null) {
            parentController.onTaskSelected(boundTask);
        }
    }

    @FXML
    private void toggleStatus() {
        if (boundTask != null) {
            parentController.onStatusToggled(boundTask);
        }
    }


    private void applyStatus(Label label, TaskStatus status) {
        label.getStyleClass().removeAll(
                "status-progress",
                "status-done",
                "status-todo",
                "status-late"
        );

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
}