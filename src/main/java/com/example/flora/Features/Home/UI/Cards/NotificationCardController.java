package com.example.flora.Features.Home.UI.Cards;

import com.example.flora.Features.Home.UI.HomeUI_Controller;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

import java.io.IOException;

public class NotificationCardController {

    @FXML
    private Label notificationDescription;

    @FXML
    private AnchorPane card;

    @FXML
    private Label notificationTime;

    @FXML
    private Label notificationTitle;
    private HomeUI_Controller homeController;

    public Label getNotificationTitle() {
        return notificationTitle;
    }

    public Label getNotificationDescription() {
        return notificationDescription;
    }

    public Label getNotificationTime() {
        return notificationTime;
    }

    // Inject parent controller
    public void setHomeController(HomeUI_Controller homeController) {
        this.homeController = homeController;
    }

    public void setValue(String notificationTitle, String notificationDescription, String notificationTime){
        this.notificationDescription.setText(notificationDescription);
        this.notificationTitle.setText(notificationTitle);
        this.notificationTime.setText(notificationTime);
    }

    public void notification() throws IOException {
        if (homeController != null) {
            homeController.setNotification(
                    notificationTitle.getText(),
                    notificationDescription.getText(),
                    notificationTime.getText()
            );
        }}

    @FXML
    private void delete(){
        Parent parent = card.getParent();

        if(parent instanceof Pane pane){
            pane.getChildren().remove(card);
        }
    }
    @FXML
    private void showPane(MouseEvent event) throws IOException {
        notification();
    }
    @FXML
    private void showLabel(MouseEvent event) throws IOException {
        showPane(event);
    }
    @FXML
    private void showDesc(MouseEvent event) throws IOException {
    showPane(event);
    }
    @FXML
    private void showTime(MouseEvent event) throws IOException {
        showPane(event);
    }
}
