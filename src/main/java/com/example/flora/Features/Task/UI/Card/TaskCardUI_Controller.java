package com.example.flora.Features.Task.UI.Card;

import com.example.flora.Features.Task.UI.TaskUI_Controller;
import com.example.flora.Features.Task.model.TaskStatus;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.text.Text;

public class TaskCardUI_Controller {
    @FXML
    private Label taskTitle;
    @FXML
    private Label dueDate;
    @FXML
    private Label taskStatus;
    @FXML
    private Label subTitle;
    private TaskUI_Controller controller;

    public void setController(TaskUI_Controller controller){
        this.controller = controller;
    }

    public void setValue(String taskTitle, String dueDate, String taskStatus, String subTitle){
        this.taskTitle.setText(taskTitle);
        this.dueDate.setText(dueDate);
        this.taskStatus.setText(taskStatus);
        this.subTitle.setText(subTitle);
        applyStatus(this.taskStatus, this.taskStatus.getText());
    }

    @FXML
    private void selectedTaskCard(){
        controller.selectedTask(taskTitle.getText(),subTitle.getText(), TaskStatus.TODO);
    }

    private void applyStatus(Label label, String status) {
        // Clear any previous status class first
        label.getStyleClass().removeAll(
                "status-progress",
                "status-done",
                "status-todo",
                "status-late"
        );

        // Apply the right one based on value
        switch (status) {
            case "IN_PROGRESS" -> {
                label.setText("In Progress");
                label.getStyleClass().add("status-progress");
            }
            case "DONE" -> {
                label.setText("Done");
                label.getStyleClass().add("status-done");
            }
            case "TODO" -> {
                label.setText("To Do");
                label.getStyleClass().add("status-todo");
            }
            case "IN_REVIEW" -> {
                label.setText("Overdue");
                label.getStyleClass().add("status-late");
            }
            default -> {
                label.setText(status);
                label.getStyleClass().add("status-todo");
            }
        }
    }
}
