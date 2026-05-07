package com.example.flora.Features.Overview.UI;

import com.example.flora.Features.Home.UI.Cards.ProjectShowCardController;
import com.example.flora.Features.Home.UI.Cards.TaskNotifyController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class Overview_Controller implements Initializable {
    //NOTE:Project
    @FXML
    private VBox taskShow;
    @FXML
    private ScrollPane scrollPane; //projects view
    @FXML
    private  ScrollPane scrollPaneP; // projects task view
    @FXML
    private HBox ProjectsShow;
    @FXML
    private Label completedTask;
    @FXML
    private Label dueTask;



    //NOTE: Dummy data for testing
    void dummyData(){
        try {
            loadProjectCard("F.L.O.R.A","Full-Stack","Redoy","In-Progress",.25);
            loadProjectCard("M.E.M.O.","Full-Stack","Redoy","In-Progress",.45);
            loadProjectCard("Hospital Management System","Full-Stack","Redoy","In-Progress",.95);
            loadProjectCard("AI-Assistant","Full-Stack","Redoy","In-Progress",.55);
            loadProjectCard("Student Portal","Full-Stack","Redoy","In-Progress",.15);
            loadProjectCard("Weather App","Full-Stack","Redoy","In-Progress",1.00);

            loadTaskNotifyCard("Project Management System","15");
            loadTaskNotifyCard("MEMO","25");
            loadTaskNotifyCard("Hospital Management System","35");
            loadTaskNotifyCard("Student Portal","5");
            loadTaskNotifyCard("Weather App","55");

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }



    //NOTE: Project card
    void loadProjectCard(String projectName, String projectCategory, String leadName, String projectStatus, double projectProgress) throws IOException {
        FXMLLoader loader =
                new FXMLLoader(getClass().getResource("/Home/UI/Cards/ProjectShowCard.fxml"));
        AnchorPane card = loader.load();
        ProjectShowCardController controller =
                loader.getController();
        controller.setData(projectName,projectCategory,leadName,projectStatus,projectProgress);
        ProjectsShow.getChildren().add(card);
    }

    //NOTE:Task notify
    void loadTaskNotifyCard(String projectName, String taskCount) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Home/UI/Cards/taskNotify.fxml"));
        AnchorPane card = loader.load();
        TaskNotifyController controller = loader.getController();
        controller.setValue(projectName,taskCount);
        taskShow.getChildren().add(card);
    }

    void removeScrollBar(ScrollPane scrollPane){
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        removeScrollBar(this.scrollPane);
        removeScrollBar(this.scrollPaneP);
        dummyData();
    }
}
