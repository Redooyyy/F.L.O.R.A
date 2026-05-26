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
import com.example.flora.Features.Project.ViewModel.ProjectViewModel;
import com.example.flora.Features.Project.model.Project;
import com.example.flora.Features.Settings.UI.SettingsUI_Controller;
import com.example.flora.Features.Task.UI.TaskUI_Controller;
import com.example.flora.Features.Task.model.TaskStatus;
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
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.ResourceBundle;

public class HomeUI_Controller implements Initializable {

    @FXML
    private AnchorPane contentPane;
    @FXML
    private VBox sidebar;

    @FXML
    private AnchorPane notificationPane;
    @FXML
    private VBox notificationBar;
    @FXML
    private ScrollPane notificationScroll;
    @FXML
    private Button clearAllButton;

    @FXML
    private AnchorPane notificationShow;
    @FXML
    private Label emailBody;
    @FXML
    private Label sendTime;
    @FXML
    private Label sender;
    @FXML
    private Label project;
    @FXML
    private Label role;

    @FXML
    private AnchorPane invitationPane;
    @FXML
    private Label emailBodyInvite;
    @FXML
    private Label sendTimeInvite;
    @FXML
    private Label senderInvite;
    @FXML
    private Label projectInvite;
    @FXML
    private Label roleInvite;

    @FXML
    private AnchorPane addProjectPanel;
    private AddProjectModal_Controller projectModalController;
    private boolean openAddProjectSlide = false;

    @FXML
    private AnchorPane slideTaskInfo;
    @FXML
    private Label taskTitle;
    @FXML
    private Label taskDetail;
    @FXML
    private Button currStatus;

    private static int buttonPressed = 0;
    private final String[] option = {
            TaskStatus.TODO.toString(),
            TaskStatus.IN_PROGRESS.toString(),
            TaskStatus.IN_REVIEW.toString(),
            TaskStatus.DONE.toString()
    };

    private boolean toogleBar = false;

    private boolean isNotificationDescOpen = false;
    private boolean isInvitePaneOpen = false;

    private Notification currentInviteNotification;

    private final AppContainer appContainer;
    private final NotificationViewModel notificationViewModel;

    private static final DateTimeFormatter TIME_FMT =
            DateTimeFormatter.ofPattern("HH:mm, dd MMM");

    public HomeUI_Controller(AppContainer appContainer,
                             NotificationViewModel notificationViewModel) {
        this.appContainer = appContainer;
        this.notificationViewModel = notificationViewModel;
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadStatus();
        removeScrollBar(notificationScroll);
        notificationScroll.setFitToWidth(true);

        try {
            overviewPage();
            loadProjectModal();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        notificationViewModel.getNotifications().addListener(
                (ListChangeListener<Notification>) c -> {
                    try {
                        loadNotificationCard();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });

        notificationViewModel.load();
        try {
            loadNotificationCard();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public void activateNotification(int userId) throws IOException {
        notificationViewModel.getNotifications().addListener(
                (ListChangeListener<Notification>) c -> {
                    try {
                        loadNotificationCard();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
        notificationViewModel.load();
        loadNotificationCard();
    }


    @FXML
    private void notificationCircle(MouseEvent mouseEvent) {
        slideEffect(notificationPane, Duration.millis(400), -554);
    }

    @FXML
    private void noificationIcon(MouseEvent mouseEvent) {
        notificationCircle(mouseEvent);
    }

    @FXML
    private void notificationBackText(MouseEvent mouseEvent) {
        slideEffect(notificationPane, Duration.millis(400), 554);
        closeNotificationDesc();
        closeInvitePane();
    }

    @FXML
    private void notificationBackIcon(MouseEvent mouseEvent) {
        notificationBackText(mouseEvent);
    }

    @FXML
    private void clearAll() {
        notificationBar.getChildren().clear();
        clearAllButton.setVisible(false);
        notificationViewModel.deleteAll();
    }

    void loadNotificationCard() throws IOException {
        notificationBar.getChildren().clear();
        clearAllButton.setVisible(!notificationViewModel.getNotifications().isEmpty());

        for (Notification notification : notificationViewModel.getNotifications()) {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/Home/UI/Cards/NotificationCard.fxml"));
            AnchorPane card = loader.load();
            NotificationCardController controller = loader.getController();
            controller.setData(this, notificationViewModel, notification);
            notificationBar.getChildren().add(card);
        }
    }


    public void setNotification(Notification n) {
        closeInvitePane();

        emailBody.setText(n.getDescription() != null ? n.getDescription() : "—");
        sendTime.setText(": " + n.getTime().format(TIME_FMT));
        sender.setText(": " + (n.getSenderName() != null && !n.getSenderName().isEmpty() ? n.getSenderName() : "—"));
        project.setText(": " + (n.getProjectName() != null && !n.getProjectName().isEmpty() ? n.getProjectName() : "—"));
        role.setText(": " + (n.getRole() != null && !n.getRole().isEmpty() ? n.getRole() : "—"));
    }

    public void openNotificationDesc() {
        if (isNotificationDescOpen) return;
        isNotificationDescOpen = true;
        slideEffect(notificationShow, Duration.millis(400), 818);
    }

    @FXML
    private void closeNotificationDesc() {
        if (!isNotificationDescOpen) return;
        isNotificationDescOpen = false;
        slideEffect(notificationShow, Duration.millis(400), -818);
    }

    public void pulseNotification() {
        if (isNotificationDescOpen) return;
        slideEffect(notificationShow, Duration.millis(400), -818);
        PauseTransition wait = new PauseTransition(Duration.millis(1200));
        wait.setOnFinished(e -> slideEffect(notificationShow, Duration.millis(400), 818));
        wait.play();
    }


    public void setInviteNotification(Notification n) {
        currentInviteNotification = n;
        closeNotificationDesc();

        emailBodyInvite.setText(n.getDescription() != null ? n.getDescription() : "—");
        sendTimeInvite.setText(": " + n.getTime().format(TIME_FMT));
        senderInvite.setText(": " + (n.getSenderName() != null && !n.getSenderName().isEmpty() ? n.getSenderName() : "—"));
        projectInvite.setText(": " + (n.getProjectName() != null && !n.getProjectName().isEmpty() ? n.getProjectName() : "—"));
        roleInvite.setText(": " + (n.getRole() != null && !n.getRole().isEmpty() ? n.getRole() : "—"));

    }

    public void openInvitePane() {
        if (isInvitePaneOpen) return;
        isInvitePaneOpen = true;
        slideEffect(invitationPane, Duration.millis(400), 818);
    }

    @FXML
    private void closeNotificationInviteDesc() {
        closeInvitePane();
    }

    private void closeInvitePane() {
        if (!isInvitePaneOpen) return;
        isInvitePaneOpen = false;
        currentInviteNotification = null;
        slideEffect(invitationPane, Duration.millis(400), -818);
    }


    @FXML
    private void acceptInvite() {
        if (currentInviteNotification == null) return;
        try {
            notificationViewModel.acceptInvite(currentInviteNotification);
        } catch (Exception e) {
            e.printStackTrace(); // TODO: show snackbar
        }
        closeInvitePane();
    }

    @FXML
    private void declineInvite() {
        if (currentInviteNotification == null) return;
        notificationViewModel.declineInvite(currentInviteNotification);
        closeInvitePane();
    }


    @FXML
    private void newProjectAdd(MouseEvent event) throws IOException {
        projectModalController.openPanel();
    }

    private void loadProjectModal() throws IOException {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/Project/UI/AddProjectModal.fxml"));
        AnchorPane panel = loader.load();
        projectModalController = loader.getController();
        projectModalController.setValue(this, appContainer.getProjectViewModel(), " ");
        ((AnchorPane) addProjectPanel).getChildren().add(panel);
    }


    @FXML
    private void sideBarButton(ActionEvent event) {
        slideEffect(sidebar, Duration.millis(300), -230);
    }

    @FXML
    private void menuBar(MouseEvent mouseEvent) {
        slideEffect(sidebar, Duration.millis(300), 230);
        toogleBar = true;
    }

    @FXML
    private void overviewPage() throws IOException {
        loadPage(Path.OVERVIEW, e -> new Overview_Controller());
        toggle();
    }

    @FXML
    private void projectPage() throws IOException {
        loadPage(Path.PROJECT, e -> new ProjectUI_Controller(appContainer.getProjectViewModel(), appContainer.getProjectDetailViewModel()));
        toggle();
    }

    @FXML
    private void taskPage() throws IOException {
        ProjectViewModel projectVM = appContainer.getProjectViewModel();
        if (projectVM.getProjects().isEmpty()) projectVM.loadProject();

        if (!projectVM.getProjects().isEmpty()) {
            Project firstProject = projectVM.getProjects().get(0);
            boolean isLeader = projectVM.isLeaderOf(firstProject);
            appContainer.getTaskViewModel().init(firstProject.getId(), isLeader);
        }
        loadPage(Path.TASK, e -> new TaskUI_Controller(appContainer.getTaskViewModel(), appContainer.getProjectViewModel(), this));
        toggle();
    }

    @FXML
    private void bugPage() throws IOException {
        loadPage(Path.BUG,
                e -> new BugUI_Controller(appContainer.getBugViewModel(), this));
        toggle();
    }

    @FXML
    private void settingPage() throws IOException {
        loadPage(Path.SETTINGS,
                e -> new SettingsUI_Controller(appContainer.getSettingsViewModel()));
        toggle();
    }

    @FXML
    private void logout() throws IOException {
        Stage stage = (Stage) contentPane.getScene().getWindow();
        new SceneTransition(stage).switchToLogin(
                Path.LOGIN, e -> new LoginUI_Controller(appContainer.getAuthViewModel(), appContainer));
    }


    public void statusIndicate(ActionEvent event) {
        buttonPressed++;
        if (buttonPressed > 3) buttonPressed = 0;
        loadStatus();
    }

    void loadStatus() {
        currStatus.setText(option[buttonPressed]);
        applyStatusStyle(buttonPressed);
    }

    public void updateStatus(ActionEvent event) {
    }

    public void close(ActionEvent event) {
        slideRight();
    }

    public void slideLeft() {
        slideEffect(slideTaskInfo, Duration.millis(500), -487);
    }

    void slideRight() {
        slideEffect(slideTaskInfo, Duration.millis(500), 487);
    }

    public void taskValue(String title, String detail, TaskStatus status) {
        taskDetail.setText(detail);
        taskTitle.setText(title);
        currStatus.setText(status.toString());
    }

    private void applyStatusStyle(int index) {
        currStatus.getStyleClass().removeAll(
                "status-todo", "status-progress", "status-review", "status-done");
        switch (index) {
            case 0 -> currStatus.getStyleClass().add("status-todo");
            case 1 -> currStatus.getStyleClass().add("status-progress");
            case 2 -> currStatus.getStyleClass().add("status-review");
            case 3 -> currStatus.getStyleClass().add("status-done");
        }
    }


    void removeScrollBar(ScrollPane scrollPane) {
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
    }

    void slideEffect(Node node, Duration duration, double x) {
        TranslateTransition t = new TranslateTransition();
        t.setNode(node);
        t.setDuration(duration);
        t.setToX(x);
        t.play();
    }

    private FXMLLoader loadPage(String path, Callback<Class<?>, Object> controllerFactory)
            throws IOException {
        FXMLLoader loader = new FXMLLoader(
                Objects.requireNonNull(getClass().getResource(path)));
        loader.setControllerFactory(controllerFactory);
        AnchorPane pane = loader.load();
        new SceneTransition(null).loadingContent(contentPane, pane);
        return loader;
    }

    void toggle() {
        if (toogleBar) sideBarButton(null);
    }
}