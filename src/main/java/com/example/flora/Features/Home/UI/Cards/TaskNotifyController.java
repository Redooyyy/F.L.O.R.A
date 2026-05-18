package com.example.flora.Features.Home.UI.Cards;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class TaskNotifyController {

    @FXML
    private Label projectName;

    @FXML
    private Label taskCount;

    public void setValue(String projectName, String taskCount){
        this.projectName.setText(projectName);
        this.taskCount.setText(taskCount);
    }

}
