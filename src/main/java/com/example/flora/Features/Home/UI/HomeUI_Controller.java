package com.example.flora.Features.Home.UI;

import com.example.flora.Features.Home.UI.Cards.NotificationCardController;
import com.example.flora.Features.Home.UI.Cards.ProjectShowCardController;
import com.example.flora.Features.Home.UI.Cards.TaskNotifyController;
import com.example.flora.Features.Home.ViewModel.NotificationViewModel;
import com.example.flora.Features.Home.model.Notification;
import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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
    //NOTE:Project
    @FXML
    private Label completedTask;
    @FXML
    private Label dueTask;
    @FXML
    private HBox ProjectsShow;
    @FXML
    private VBox taskShow;
    @FXML
    private VBox sidebar;
    @FXML
    private ScrollPane scrollPane; //projects view
    @FXML
    private  ScrollPane scrollPaneP; // projects task view



    //NOTE:Notification variables
    @FXML
    private AnchorPane notificationShow;
    @FXML
    private Button clearAllButton;
    @FXML
    private VBox notificationBar;
    @FXML
    private ScrollPane notificationScroll;
    @FXML
    private  AnchorPane notificationPane;
    @FXML
    private NotificationViewModel notificationViewModel;
    @FXML
    private Label sendTime;
    @FXML
    private Label role;
    @FXML
    private Label project;
    @FXML
    private Label sender;
    @FXML
    private Label emailBody;
    @FXML
    private Label sendTimeInvite;
    @FXML
    private Label roleInvite;
    @FXML
    private Label projectInvite;
    @FXML
    private Label senderInvite;
    @FXML
    private Label emailBodyInvite;
    @FXML
    private AnchorPane invitationPane;






    //NOTE: Init zone
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        removeScrollBar(this.scrollPane);
        removeScrollBar(this.scrollPaneP);
        removeScrollBar(this.notificationScroll);
        notificationScroll.setFitToWidth(true); // for disabling horizontal scroll
        dummyData();

    }






    //NOTE: Dummy data for testing
    void dummyData(){
        try {
            loadProjectCard("F.L.O.R.A","Full-Stack","Redoy","In-Progress",.25);
            loadProjectCard("M.E.M.O.","Full-Stack","Redoy","In-Progress",.45);
            loadProjectCard("Hospital Management System","Full-Stack","Redoy","In-Progress",.95);
            loadProjectCard("AI-Assistant","Full-Stack","Redoy","In-Progress",.55);
            loadProjectCard("Student Portal","Full-Stack","Redoy","In-Progress",.15);
            loadProjectCard("Weather App","Full-Stack","Redoy","In-Progress",1.00);

            loadTaskNotifyCard("Project Management System","15");
            loadTaskNotifyCard("MEMO","25");
            loadTaskNotifyCard("Hospital Management System","35");
            loadTaskNotifyCard("Student Portal","5");
            loadTaskNotifyCard("Weather App","55");

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






    //NOTE:sidebar open close
    @FXML
    private void sideBarButton(ActionEvent event) {
        slideEffect(sidebar,Duration.millis(300),-230);
    }

    @FXML
    private void menuBar(MouseEvent mouseEvent) {
        slideEffect(sidebar,Duration.millis(300),230);
    }







    //NOTE:Task notify
    void loadTaskNotifyCard(String projectName, String taskCount) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Home/UI/Cards/taskNotify.fxml"));
        AnchorPane card = loader.load();
        TaskNotifyController controller = loader.getController();
        controller.setValue(projectName,taskCount);
        taskShow.getChildren().add(card);
    }







    //NOTE: Notification Zone//
    //notification activate
    public void activateNotification(NotificationViewModel notificationViewModel, int userId) throws IOException {
        this.notificationViewModel = notificationViewModel;

        notificationViewModel.getNotifications().addListener((ListChangeListener< Notification>) a ->{
            //refresh
            try {
                loadNotificationCard();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        notificationViewModel.load(userId);
        loadNotificationCard();
    }
    //notification set in tiles
    public void setNotification(String title, String description, String time){
        this.emailBody.setText(description);
        this.sendTime.setText(": "+time);
        //static sender for testing
        this.sender.setText(": Mim Akter Bushra");
        this.project.setText(": Project Management System");
        this.role.setText(": Project Lead");
        showNotification();
    }
    //notification card
    void loadNotificationCard() throws IOException {
        notificationBar.getChildren().clear();
        for(Notification notification : notificationViewModel.getNotifications()){
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Home/UI/Cards/NotificationCard.fxml"));
                AnchorPane card = loader.load();
                NotificationCardController controller = loader.getController();
                //passing current instance
                controller.setData(this,notificationViewModel,notification);
                notificationBar.getChildren().add(card);

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

    }
    //notification pulse animation
    void showNotification(){
        if(notificationShow.getTranslateX() == 818.0){
            slideEffect(notificationShow,Duration.millis(400),-818);
            //for pulse effect
            PauseTransition wait = new PauseTransition(Duration.millis(400));
            wait.setOnFinished(e ->
                    slideEffect(notificationShow, Duration.millis(400), 818)
            );
            wait.play();
        } else {
            slideEffect(notificationShow,Duration.millis(400),818);
        }
    }

    //notification back text
    @FXML
    private void notificationBackText(MouseEvent mouseEvent) {
        slideEffect(notificationPane,Duration.millis(400),554);
        if(notificationShow.getTranslateX() == 818.0){
            closeNotificationDesc();
            closeNotificationInviteDesc();
        }
    }
    //notification clear all
    @FXML
    private void clearAll(){
        notificationBar.getChildren().clear();
        clearAllButton.setVisible(false);
    }
    //notification back icon
    @FXML
    private void notificationBackIcon(MouseEvent mouseEvent) {
        notificationBackText(mouseEvent);
    }
    //notification circle
    @FXML
    private void notificationCircle(MouseEvent mouseEvent) {
        slideEffect(notificationPane,Duration.millis(400),-554);
    }
    //notification icon
    @FXML
    private void noificationIcon(MouseEvent mouseEvent) {
        notificationCircle(mouseEvent);
    }
    //notification close description
    @FXML
    private void closeNotificationDesc(){
        slideEffect(notificationShow,Duration.millis(400),-818);
    }
    //notification invite section description close
    @FXML
    private void closeNotificationInviteDesc(){}







    //NOTE: Project Zone
    //accept project invitation
    @FXML
    private void acceptInvite(){}
    //decline project invitation
    @FXML
    private void declineInvite(){}
    //add new project
    @FXML
    private void newProjectAdd(MouseEvent event){}




    //NOTE: Helper functions
    void removeScrollBar(ScrollPane scrollPane){
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
    }
    //slide effect for nodess
    void slideEffect(Node node, Duration duration, double x){
        TranslateTransition moveSlide = new TranslateTransition();
        moveSlide.setNode(node);
        moveSlide.setDuration(duration);
        moveSlide.setByX(x);
        moveSlide.play();
    }
}
