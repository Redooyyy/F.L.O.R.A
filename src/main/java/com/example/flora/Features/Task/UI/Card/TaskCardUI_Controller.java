package com.example.flora.Features.Task.UI.Card;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.text.Text;

public class TaskCardUI_Controller {
    @FXML
    private Text taskTitle;
    @FXML
    private Label dueDate;
    @FXML
    private Label taskStatus;
    @FXML
    private Text subTitle;



    public void setValue(String taskTitle, String dueDate, String taskStatus, String subTitle){
        this.taskTitle.setText(taskTitle);
        this.dueDate.setText(dueDate);
        this.taskStatus.setText(taskStatus);
        this.subTitle.setText(subTitle);
    }
}
