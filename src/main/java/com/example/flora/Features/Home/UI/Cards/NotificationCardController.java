package com.example.flora.Features.Home.UI.Cards;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class NotificationCardController {

    @FXML
    private Label notificationDescription;

    @FXML
    private Label notificationTime;

    @FXML
    private Label notificationTitle;

    public void setValue(String notificationTitle, String notificationDescription,String notificationTime){
        this.notificationDescription.setText(notificationDescription);
        this.notificationTitle.setText(notificationTitle);
        this.notificationTime.setText(notificationTime);
    }

}
