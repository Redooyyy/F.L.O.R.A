package com.example.flora.Features.Home.UI;

import com.example.flora.Core.Constants.Path;
import com.example.flora.Core.DI.AppContainer;
import com.example.flora.Core.Transition.SceneTransition;
import com.example.flora.Features.Auth.UI.LoginUI_Controller;
import com.example.flora.Features.Bug.UI.BugUI_Controller;
import com.example.flora.Features.Home.UI.Cards.NotificationCardController;
import com.example.flora.Features.Home.ViewModel.NotificationViewModel;
import com.example.flora.Features.Home.model.Notification;
import com.example.flora.Features.Overview.UI.Overview_Controller;
import com.example.flora.Features.Project.UI.AddProjectModal_Controller;
import com.example.flora.Features.Project.UI.ProjectUI_Controller;
import com.example.flora.Features.Task.UI.TaskUI_Controller;
import com.example.flora.Features.Task.ViewModel.TaskViewModel;
import com.example.flora.Features.Task.model.TaskStatus;
import com.example.flora.Features.Task.repository.TaskRepositoryFake;
import com.example.flora.Features.Task.service.TaskServices;
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
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class HomeUI_Controller implements Initializable {
    @FXML
    private AnchorPane contentPane;
    @FXML
    private VBox sidebar;

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

    //NOTE:Project variables
    @FXML
    private AnchorPane addProjectPanel;
    private AddProjectModal_Controller projectModalController;
    private boolean openAddProjectSlide = false;


    private final AppContainer appContainer;
    private final NotificationViewModel notificationViewModel;

private boolean toogleBar=false;

public HomeUI_Controller(AppContainer appContainer, NotificationViewModel notificationViewModel){
    this.appContainer = appContainer;
    this.notificationViewModel = notificationViewModel;
}

    //NOTE: Init zone
    @Override
    public void initialize(URL location, ResourceBundle resources) {
    loadStatus();
        removeScrollBar(this.notificationScroll);
        notificationScroll.setFitToWidth(true); // for disabling horizontal scroll

        //call homePage so after login it'll appear
        try {
            overviewPage();
            loadProjectModal();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        setNotification("Invitation","Hey I am here to invite you to my project", "9:30");

    }




    //NOTE:sidebar open close
    @FXML
    private void sideBarButton(ActionEvent event) {
        slideEffect(sidebar,Duration.millis(300),-230);
    }

    @FXML
    private void menuBar(MouseEvent mouseEvent) {
        slideEffect(sidebar,Duration.millis(300),230);
        toogleBar = true;
    }




    //NOTE: Notification Zone//
    //notification activate
    public void activateNotification(int userId) throws IOException {
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
    //TODO: update notification model for sender, project lead, project name
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
    private void newProjectAdd(MouseEvent event) throws IOException {
    projectModalController.openPanel();
    }

    private void loadProjectModal() throws IOException {
    FXMLLoader loader = new FXMLLoader(getClass().getResource("/Project/UI/AddProjectModal.fxml"));
        AnchorPane panel = loader.load();
        projectModalController = loader.getController();
        //TODO: pass current user
        projectModalController.setValue(this,appContainer.getProjectViewModel()," ");
        ((AnchorPane) addProjectPanel).getChildren().add(panel);
    }



    //NOTE: navigation Zone
    @FXML
    private void overviewPage() throws IOException {
        loadPage(Path.OVERVIEW,e-> new Overview_Controller());
        toggle();
    }

    @FXML
    private void projectPage() throws IOException {
        loadPage(Path.PROJECT, e->new ProjectUI_Controller(appContainer.getProjectViewModel(), appContainer.getProjectDetailViewModel()));
        toggle();
    }

    @FXML
    private void taskPage() throws IOException {
    loadPage(Path.TASK,e->new TaskUI_Controller(appContainer.getTaskViewModel(),this));
    toggle();
    }

    @FXML
    private void bugPage() throws IOException {
        loadPage(Path.BUG, e -> new BugUI_Controller(appContainer.getBugViewModel(),this));
        toggle();
    }


    @FXML
    private void settingPage(){}

    @FXML
    private void logout() throws IOException {
        Stage stage = (Stage) contentPane.getScene().getWindow();
        SceneTransition transition = new SceneTransition(stage);
        transition.switchToLogin(
                Path.LOGIN,
                e -> new LoginUI_Controller(
                        appContainer.getAuthViewModel(),
                        appContainer
                )
        );

    }

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
        moveSlide.setToX(x);
        moveSlide.play();
    }

    private FXMLLoader loadPage(String path, Callback<Class<?>, Object> controllerFactory) throws IOException {
        FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource(path)));
        loader.setControllerFactory(controllerFactory);
        AnchorPane pane = loader.load();
        SceneTransition sceneTransition = new SceneTransition(null); // no stage needed for content swap
        sceneTransition.loadingContent(contentPane, pane);
        return  loader;
    }

    void toggle(){
        if(toogleBar) sideBarButton(null);
    }



    //NOTE: TASK part
    @FXML
    private AnchorPane slideTaskInfo;
    @FXML
    private Label taskTitle;
    @FXML
    private Label taskDetail;
    @FXML
    private Button currStatus;
    private static int buttonPressed =0;
    private final String[] option= {
            TaskStatus.TODO.toString(),
            TaskStatus.IN_PROGRESS.toString(),
            TaskStatus.IN_REVIEW.toString(),
            TaskStatus.DONE.toString()
    };

    public void statusIndicate(ActionEvent event) {
        buttonPressed++;
        if(buttonPressed>3) buttonPressed = 0;
        loadStatus();
    }
    void loadStatus(){
        currStatus.setText(option[buttonPressed]);
        applyStatusStyle(buttonPressed);
    }

    public void updateStatus(ActionEvent event) {
    }

    public void close(ActionEvent event) {
    slideRight(); // working
    }
    public void slideLeft(){
        slideEffect(slideTaskInfo,Duration.millis(500),-487);
    }
    void slideRight(){
        slideEffect(slideTaskInfo,Duration.millis(500),487);
    }
    public void taskValue(String taskTitle, String taskDetail, TaskStatus status){
        this.taskDetail.setText(taskDetail);
        this.taskTitle.setText(taskTitle);
        this.currStatus.setText(status.toString());
    }

    private void applyStatusStyle(int index) {
        // Remove all status classes first
        currStatus.getStyleClass().removeAll(
                "status-todo",
                "status-progress",
                "status-review",
                "status-done"
        );

        // Add the right one
        switch (index) {
            case 0 -> currStatus.getStyleClass().add("status-todo");       // TODO
            case 1 -> currStatus.getStyleClass().add("status-progress");   // IN_PROGRESS
            case 2 -> currStatus.getStyleClass().add("status-review");     // IN_REVIEW
            case 3 -> currStatus.getStyleClass().add("status-done");       // DONE
        }
    }
}
