package com.example.flora.Features.Home.UI.Cards;

import com.example.flora.Features.Home.UI.HomeUI_Controller;
import com.example.flora.Features.Home.ViewModel.NotificationViewModel;
import com.example.flora.Features.Home.model.Notification;
import com.example.flora.Features.Home.model.NotificationType;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
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

    private HomeUI_Controller homeController;
    private Notification notification;
    private NotificationViewModel notificationViewModel;

    public Label getNotificationTitle() {
        return notificationTitle;
    }

    public Label getNotificationDescription() {
        return notificationDescription;
    }

    public Label getNotificationTime() {
        return notificationTime;
    }


    public void setData(HomeUI_Controller homeController,
                        NotificationViewModel notificationViewModel,
                        Notification notification) {
        this.homeController = homeController;
        this.notificationViewModel = notificationViewModel;
        this.notification = notification;

        String timeText = notification.getTime()
                .toLocalTime()
                .toString()
                .substring(0, 5); // "HH:mm"

        notificationTitle.setText(notification.getTitle());
        notificationDescription.setText(notification.getDescription());
        notificationTime.setText(timeText);

        if (notification.isRead()) {
            card.setOpacity(0.6);
        }
    }


    @FXML
    private void showPane(MouseEvent event) {
        if (homeController == null) return;

        if (!notification.isRead()) {
            notificationViewModel.markAsRead(notification);
            card.setOpacity(0.6);
        }

        if (notification.getType() == NotificationType.PROJECT_INVITE) {
            homeController.setInviteNotification(notification);
            homeController.openInvitePane();
        } else {
            homeController.setNotification(notification);
            homeController.openNotificationDesc();
        }
    }

    @FXML
    private void showLabel(MouseEvent e) {
        showPane(e);
    }

    @FXML
    private void showDesc(MouseEvent e) {
        showPane(e);
    }

    @FXML
    private void showTime(MouseEvent e) {
        showPane(e);
    }


    @FXML
    private void delete() {
        if (notificationViewModel != null && notification != null) {
            notificationViewModel.delete(notification);
        }
        Parent parent = card.getParent();
        if (parent instanceof Pane pane) {
            pane.getChildren().remove(card);
        }
    }
}