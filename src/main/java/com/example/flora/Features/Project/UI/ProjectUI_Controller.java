package com.example.flora.Features.Project.UI;

import com.example.flora.Features.Project.UI.Card.ProjectCard_Controller;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ProjectUI_Controller implements Initializable {
    //NOTE: sidebar info
    @FXML
    private Text nickName;
    @FXML
    private Text totalProject;
    @FXML
    private Text completedProject;
    @FXML
    private Text inProgressProject;
    @FXML
    private Text onholdProject;
    @FXML
    private Text outOfScheduleProject;

    //NOTE: project card view
    @FXML
    private ScrollPane projectCardView;
    @FXML
    private GridPane projectGrid;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setNickname("Bushra");
        removeScrollBar(projectCardView);
        try {
            addProjectCard(10);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    void addProjectCard(int num) throws IOException {
        for (int i = 0; i < num; i++) {
            int col = i % 3;
            int row = i / 3;
            projectGrid.add(projectCard(), col, row);
        }
    }

    AnchorPane projectCard() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Project/UI/Card/ProjectCard.fxml"));
        AnchorPane card = loader.load();
        card.setMaxWidth(Double.MAX_VALUE);
        GridPane.setHgrow(card, javafx.scene.layout.Priority.ALWAYS);
        ProjectCard_Controller cardController = loader.getController();

        projectGrid.setHgap(10);
        projectGrid.setVgap(10);
        projectGrid.setPadding(new Insets(10));

        return  card;
    }

    //NOTE: Helper functions
    public void setNickname(String name){
        nickName.setText(name);
    }

    void removeScrollBar(ScrollPane scrollPane){
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
    }
}
