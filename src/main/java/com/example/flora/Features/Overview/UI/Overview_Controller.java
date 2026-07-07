package com.example.flora.Features.Overview.UI;

import com.example.flora.Core.DI.AppContainer;
import com.example.flora.Features.Home.UI.Cards.ProjectShowCardController;
import com.example.flora.Features.Home.UI.Cards.TaskNotifyController;
import com.example.flora.Features.Project.model.Project;
import com.example.flora.Features.Task.model.Task;
import com.example.flora.Features.Task.model.TaskStatus;
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
import java.util.List;
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

    private final AppContainer appContainer;

    public Overview_Controller(AppContainer appContainer) {
        this.appContainer = appContainer;
    }

    //NOTE: Load data from DB
    void loadData(){
        try {
            appContainer.getProjectViewModel().loadProject();
            List<Project> projects = appContainer.getProjectViewModel().getProjects();
            int completedTasksCount = 0;
            int dueTasksCount = 0;

            for (Project project : projects) {
                String leadName = "Unknown";
                List<String> members = appContainer.getProjectViewModel().getMembers(project.getId());
                if(members != null && !members.isEmpty()) {
                    leadName = members.get(0); // Approximate lead
                }

                appContainer.getTaskViewModel().init(project.getId(), true);
                List<Task> tasks = appContainer.getTaskViewModel().getTasks();
                int projectTaskCount = tasks.size();

                double progress = 0.0;
                if(projectTaskCount > 0) {
                    long doneTasks = tasks.stream().filter(t -> t.getStatus() == TaskStatus.DONE).count();
                    progress = (double) doneTasks / projectTaskCount;
                }
                
                for(Task task : tasks) {
                    if (task.getStatus() == TaskStatus.DONE) {
                        completedTasksCount++;
                    } else {
                        dueTasksCount++;
                    }
                }

                loadProjectCard(project.getName(), "Project", leadName, progress == 1.0 ? "Done" : "In-Progress", progress);
                loadTaskNotifyCard(project.getName(), String.valueOf(projectTaskCount));
            }
            
            if(completedTask != null) completedTask.setText(String.valueOf(completedTasksCount));
            if(dueTask != null) dueTask.setText(String.valueOf(dueTasksCount));

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
        loadData();
    }
}
