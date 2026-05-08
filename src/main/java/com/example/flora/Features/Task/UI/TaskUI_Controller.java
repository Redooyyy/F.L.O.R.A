package com.example.flora.Features.Task.UI;

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

    public TaskUI_Controller(TaskViewModel taskViewModel){
        this.taskViewModel = taskViewModel;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        removeScrollBar(tasScroll);
        removeScrollBar(proScroll);
        try {
            addCards();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    AnchorPane cards() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Task/UI/Card/TaskCardUI.fxml"));
        AnchorPane card = loader.load();
        TaskCardUI_Controller controller = loader.getController();
        //test
        controller.setValue("README Add","27 May", TaskStatus.IN_PROGRESS.toString(),"Write documentation");
        return card;
    }

    void addCards() throws IOException {
        TaskCardScroll.getChildren().add(cards());
        TaskCardScroll.getChildren().add(cards());
        TaskCardScroll.getChildren().add(cards());
        TaskCardScroll.getChildren().add(cards());
    }



    void removeScrollBar(ScrollPane scrollPane){
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
    }
}
