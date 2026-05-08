package com.example.flora.Features.Task.UI;

import com.example.flora.Features.Home.UI.Cards.TaskNotifyController;
import com.example.flora.Features.Home.UI.HomeUI_Controller;
import com.example.flora.Features.Task.UI.Card.TaskCardUI_Controller;
import com.example.flora.Features.Task.ViewModel.TaskViewModel;
import com.example.flora.Features.Task.model.TaskStatus;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class TaskUI_Controller implements Initializable {
    private final TaskViewModel taskViewModel;
    @FXML
    private ScrollPane tasScroll;
    @FXML
    private ScrollPane proScroll;
    @FXML
    private Label projectNameInViewBox;
    @FXML
    private VBox projectCardScroll;
    @FXML
    private VBox TaskCardScroll;

    private final HomeUI_Controller controller;

    public TaskUI_Controller(TaskViewModel taskViewModel, HomeUI_Controller controller){
        this.taskViewModel = taskViewModel;
        this.controller = controller;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

       // controller.slideLeft();
//        removeScrollBar(tasScroll);
//        removeScrollBar(proScroll);
        try {
            loadTaskNotifyCard("HMS","10");
            loadTaskNotifyCard("HMS","10");
            loadTaskNotifyCard("HMS","10");
            dummyData();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    AnchorPane cards(String s, String s2, String s3, String s4) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Task/UI/Card/TaskCardUI.fxml"));
        AnchorPane card = loader.load();
        TaskCardUI_Controller controller = loader.getController();
        controller.setController(this);
        //test
        controller.setValue(s,s2,s3,s4);
        return card;
    }

    void dummyData() throws IOException {
        TaskCardScroll.getChildren().add(cards("Hospital Management System", "27 May", TaskStatus.TODO.toString(), "Design the patient registration module and set up database schema"));
        TaskCardScroll.getChildren().add(cards("E-Commerce Platform", "30 May", TaskStatus.IN_PROGRESS.toString(), "Implement product search with filters and sorting functionality"));
        TaskCardScroll.getChildren().add(cards("Flora Project Manager", "02 Jun", TaskStatus.IN_PROGRESS.toString(), "Build the notification system and real-time update pipeline"));
        TaskCardScroll.getChildren().add(cards("Banking App Redesign", "05 Jun", TaskStatus.TODO.toString(), "Redesign the dashboard UI following the new brand guidelines"));
        TaskCardScroll.getChildren().add(cards("Inventory System", "07 Jun", TaskStatus.DONE.toString(), "Complete stock tracking module with low inventory alerts"));
        TaskCardScroll.getChildren().add(cards("HR Management Portal", "10 Jun", TaskStatus.TODO.toString(), "Set up employee onboarding flow and document upload feature"));
        TaskCardScroll.getChildren().add(cards("Learning Management System", "12 Jun", TaskStatus.IN_PROGRESS.toString(), "Develop quiz engine with auto-grading and progress tracking"));
        TaskCardScroll.getChildren().add(cards("Real Estate App", "15 Jun", TaskStatus.TODO.toString(), "Integrate map view with property listings and filter options"));
        TaskCardScroll.getChildren().add(cards("Food Delivery Tracker", "18 Jun", TaskStatus.DONE.toString(), "Finalize order status updates and driver location tracking"));
        TaskCardScroll.getChildren().add(cards("Library System", "20 Jun", TaskStatus.TODO.toString(), "Build book borrowing workflow with due date reminders"));
    }

    public void selectedTask(String taskTitle, String taskDetail, TaskStatus status){
        controller.taskValue(taskTitle,taskDetail,status);
        controller.slideLeft();
    }

    void removeScrollBar(ScrollPane scrollPane){
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
    }

    void loadTaskNotifyCard(String projectName, String taskCount) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Home/UI/Cards/taskNotify.fxml"));
        AnchorPane card = loader.load();
        TaskNotifyController controller = loader.getController();
        controller.setValue(projectName,taskCount);
        projectCardScroll.getChildren().add(card);
    }
}
