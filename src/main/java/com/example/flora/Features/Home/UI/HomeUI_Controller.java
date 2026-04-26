package com.example.flora.Features.Home.UI;

import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class HomeUI_Controller {
    @FXML
    private VBox notificationBar;
    @FXML
    private VBox sidebar;

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

    void slideEffect(Node node, Duration duration, double x){
        TranslateTransition moveSlide = new TranslateTransition();
        moveSlide.setNode(node);
        moveSlide.setDuration(duration);
        moveSlide.setByX(x);
        moveSlide.play();
    }
}
