package com.example.flora.Features.Home.UI.Cards;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;

public class ProjectShowCardController {

    @FXML
    private Label leadName;

    @FXML
    private Label projectCategory;

    @FXML
    private Label projectName;

    @FXML
    private Label progressPercent;

    @FXML
    private ProgressBar projectProgressBar;

    @FXML
    private Label projectStatus;

    public void setData(String projectName, String projectCategory, String leadName, String projectStatus, double projectProgress){
        this.projectProgressBar.setProgress(projectProgress);
        this.projectName.setText(projectName);
        this.projectCategory.setText(projectCategory);
        this.leadName.setText(leadName);
        this.projectStatus.setText(projectStatus);
        percentageCalculate();
    }

    void percentageCalculate(){
       String s = Double.toString(projectProgressBar.getProgress()*100);
       this.progressPercent.setText(s.substring(0,s.indexOf('.'))+"%");
    }
}
