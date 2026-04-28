package com.example.flora.Features.Home.UI.Cards;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

public class NotificationCardController {

    @FXML
    private Label notificationDescription;

    @FXML
    private AnchorPane card;

    @FXML
    private Label notificationTime;

    @FXML
    private Label notificationTitle;

    public void setValue(String notificationTitle, String notificationDescription,String notificationTime){
        this.notificationDescription.setText(notificationDescription);
        this.notificationTitle.setText(notificationTitle);
        this.notificationTime.setText(notificationTime);
    }

    @FXML
    private void delete(){
        Parent parent = card.getParent();

        if(parent instanceof Pane pane){
            pane.getChildren().remove(card);
        }
    }

}
