package com.example.flora.Features.Home.UI;

import com.example.flora.Features.Home.UI.Cards.ProjectShowCardController;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class HomeUI_Controller implements Initializable {
    private int projectcount=0;
    @FXML
    private HBox ProjectsShow;
    @FXML
    private VBox notificationBar;
    @FXML
    private VBox sidebar;
    @FXML
    private ScrollPane scrollPane; //projects view
    @FXML
    private  ScrollPane scrollPaneP; // projects task view

    @FXML
    private void sideBarButton(ActionEvent event) {
        slideEffect(sidebar,Duration.millis(300),-230);
    }

    @FXML
    private void menuBar(MouseEvent mouseEvent) {
        slideEffect(sidebar,Duration.millis(300),230);
    }

    @FXML
    private void notificationCircle(MouseEvent mouseEvent) {
        slideEffect(notificationBar,Duration.millis(400),-540);
    }
    @FXML
    private void noificationIcon(MouseEvent mouseEvent) {
        notificationCircle(mouseEvent);
    }

    @FXML
    private void notificationBackText(MouseEvent mouseEvent) {
        slideEffect(notificationBar,Duration.millis(400),540);
    }
    @FXML
    private void notificationBackIcon(MouseEvent mouseEvent) {
    notificationBackText(mouseEvent);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        removeScrollBar(this.scrollPane);
        removeScrollBar(this.scrollPaneP);

        try {
            loadCard("F.L.O.R.A","Full-Stack","Redoy","In-Progress",.25);
            loadCard("M.E.M.O.","Full-Stack","Redoy","In-Progress",.45);
            loadCard("Hospital Management System","Full-Stack","Redoy","In-Progress",.95);
            loadCard("AI-Assistant","Full-Stack","Redoy","In-Progress",.55);
            loadCard("Student Portal","Full-Stack","Redoy","In-Progress",.15);
            loadCard("Weather App","Full-Stack","Redoy","In-Progress",1.00);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //TODO: Create project card

    void loadCard(String projectName, String projectCategory, String leadName, String projectStatus, double projectProgress) throws IOException {
        FXMLLoader loader =
                new FXMLLoader(getClass().getResource("/Home/UI/Cards/ProjectShowCard.fxml"));

        AnchorPane card = loader.load();

        ProjectShowCardController controller =
                loader.getController();

        controller.setData(projectName,projectCategory,leadName,projectStatus,projectProgress);

        ProjectsShow.getChildren().add(card);
        projectcount++;
    }

    void removeScrollBar(ScrollPane scrollPane){
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
    }
    void slideEffect(Node node, Duration duration, double x){
        TranslateTransition moveSlide = new TranslateTransition();
        moveSlide.setNode(node);
        moveSlide.setDuration(duration);
        moveSlide.setByX(x);
        moveSlide.play();
    }
}
