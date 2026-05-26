package com.example.flora.Features.Project.UI;

import com.example.flora.Features.Bug.model.Bug;
import com.example.flora.Features.Project.ViewModel.ProjectDetailViewModel;
import com.example.flora.Features.Project.model.Project;
import com.example.flora.Features.Task.model.Task;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;


public class ProjectDetailUI_Controller implements Initializable {


    @FXML
    private AnchorPane projectDetailRoot;
    @FXML
    private Label detailProjectName;
    @FXML
    private Label roleBadge;


    @FXML
    private Button tabTasks;
    @FXML
    private Button tabMembers;
    @FXML
    private Button tabBugs;
    @FXML
    private AnchorPane tasksTab;
    @FXML
    private AnchorPane membersTab;
    @FXML
    private AnchorPane bugsTab;


    @FXML
    private Button addTaskToggleBtn;
    @FXML
    private MenuButton filterMenuBtn;
    @FXML
    private VBox addTaskPanel;
    @FXML
    private Button modeAssignBtn;
    @FXML
    private Button modeDraftBtn;
    @FXML
    private HBox assignFields;
    @FXML
    private HBox draftFields;
    @FXML
    private TextField taskTitleInput;
    @FXML
    private TextField taskAssigneeInput;
    @FXML
    private Label assignDeadlineChip;
    @FXML
    private TextField draftTitleInput;
    @FXML
    private Label draftDeadlineChip;
    @FXML
    private VBox taskList;
    @FXML
    private ScrollPane taskScroll;


    @FXML
    private HBox inviteRow;
    @FXML
    private TextField inviteSearchField;
    @FXML
    private Label inviteFeedback;
    @FXML
    private VBox memberList;


    @FXML
    private HBox reportBugRow;
    @FXML
    private Button reportBugToggleBtn;
    @FXML
    private VBox reportBugPanel;
    @FXML
    private TextField bugTitleInput;
    @FXML
    private TextField bugReporterInput;
    @FXML
    private ComboBox<String> bugSeverityCombo;
    @FXML
    private Label bugReportFeedback;
    @FXML
    private Button bugFilterAll;
    @FXML
    private Button bugFilterOpen;
    @FXML
    private Button bugFilterProgress;
    @FXML
    private Button bugFilterClosed;
    @FXML
    private VBox bugList;
    @FXML
    private ScrollPane bugScroll;


    @FXML
    private Label infoLeader;
    @FXML
    private Label infoCreated;
    @FXML
    private Label infoMemberCount;
    @FXML
    private Label infoDescription;
    @FXML
    private Label statTasks;
    @FXML
    private Label statBugs;


    private final ProjectDetailViewModel viewModel;
    private Runnable onClose;


    private TaskSectionHandler taskHandler;
    private MemberSectionHandler memberHandler;
    private BugSectionHandler bugHandler;

    public ProjectDetailUI_Controller(ProjectDetailViewModel viewModel) {
        this.viewModel = viewModel;
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        removeScrollBars(taskScroll);
        removeScrollBars(bugScroll);
        showTab(tasksTab, tabTasks);

        bugSeverityCombo.getItems().addAll("🔴 CRITICAL", "🟠 HIGH", "🟡 MEDIUM", "🟢 LOW");
        bugSeverityCombo.getSelectionModel().selectFirst();


        taskHandler = new TaskSectionHandler(viewModel);
        taskHandler.bind(addTaskToggleBtn, filterMenuBtn, addTaskPanel,
                modeAssignBtn, modeDraftBtn, assignFields, draftFields,
                taskTitleInput, taskAssigneeInput, assignDeadlineChip,
                draftTitleInput, draftDeadlineChip, taskList, taskScroll);

        memberHandler = new MemberSectionHandler(viewModel);
        memberHandler.bind(inviteSearchField, inviteFeedback, memberList);

        bugHandler = new BugSectionHandler(viewModel);
        bugHandler.bind(reportBugToggleBtn, reportBugPanel,
                bugTitleInput, bugReporterInput, bugSeverityCombo, bugReportFeedback,
                bugFilterAll, bugFilterOpen, bugFilterProgress, bugFilterClosed,
                bugList, bugScroll);


        viewModel.taskCountProperty().addListener((obs, o, n) -> statTasks.setText(String.valueOf(n)));
        viewModel.bugCountProperty().addListener((obs, o, n) -> statBugs.setText(String.valueOf(n)));
        viewModel.memberCountProperty().addListener((obs, o, n) -> infoMemberCount.setText(n + " members"));

        viewModel.activeTaskFilterProperty().addListener((obs, o, n) -> taskHandler.renderTasks());
        viewModel.getTasks().addListener(
                (javafx.collections.ListChangeListener<Task>) c -> taskHandler.renderTasks());

        viewModel.activeBugFilterProperty().addListener((obs, o, n) -> bugHandler.renderBugs());
        viewModel.getBugs().addListener(
                (javafx.collections.ListChangeListener<Bug>) c -> bugHandler.renderBugs());

        viewModel.getMembers().addListener(
                (javafx.collections.ListChangeListener<String>) c -> memberHandler.renderMembers());
    }


    public void openProject(Project project, String currentUserId,
                            boolean isLeader, Runnable onClose) {
        this.onClose = onClose;
        viewModel.init(project, isLeader);
        populateInfo(project);
        applyRoleVisibility(isLeader);
        showTab(tasksTab, tabTasks);
        slideIn();
    }


    @FXML
    private void showTasksTab(ActionEvent e) {
        showTab(tasksTab, tabTasks);
    }

    @FXML
    private void showMembersTab(ActionEvent e) {
        showTab(membersTab, tabMembers);
    }

    @FXML
    private void showBugsTab(ActionEvent e) {
        showTab(bugsTab, tabBugs);
    }

    private void showTab(AnchorPane active, Button btn) {
        tasksTab.setVisible(false);
        membersTab.setVisible(false);
        bugsTab.setVisible(false);
        active.setVisible(true);
        for (Button b : new Button[]{tabTasks, tabMembers, tabBugs}) {
            b.getStyleClass().removeAll("tab-btn-active", "tab-btn");
            b.getStyleClass().add("tab-btn");
        }
        btn.getStyleClass().removeAll("tab-btn");
        btn.getStyleClass().add("tab-btn-active");
    }


    @FXML
    private void toggleAddTaskPanel(ActionEvent e) {
        taskHandler.toggleAddTaskPanel(e);
    }

    @FXML
    private void switchToAssignMode(ActionEvent e) {
        taskHandler.switchToAssignMode(e);
    }

    @FXML
    private void switchToDraftMode(ActionEvent e) {
        taskHandler.switchToDraftMode(e);
    }

    @FXML
    private void toggleAssignDeadline(MouseEvent e) {
        taskHandler.toggleAssignDeadline(e);
    }

    @FXML
    private void toggleDraftDeadline(MouseEvent e) {
        taskHandler.toggleDraftDeadline(e);
    }

    @FXML
    private void filterMyTasks(ActionEvent e) {
        taskHandler.filterMyTasks(e);
    }

    @FXML
    private void filterAllTasks(ActionEvent e) {
        taskHandler.filterAllTasks(e);
    }

    @FXML
    private void filterCompleted(ActionEvent e) {
        taskHandler.filterCompleted(e);
    }

    @FXML
    private void filterDue(ActionEvent e) {
        taskHandler.filterDue(e);
    }

    @FXML
    private void filterDrafts(ActionEvent e) {
        taskHandler.filterDrafts(e);
    }

    @FXML
    private void filterByAssignee(ActionEvent e) {
        taskHandler.filterByAssignee(e);
    }

    @FXML
    private void assignTask(ActionEvent e) {
        taskHandler.assignTask(e);
    }

    @FXML
    private void saveDraftTask(ActionEvent e) {
        taskHandler.saveDraftTask(e);
    }


    @FXML
    private void sendInvite(ActionEvent e) {
        memberHandler.sendInvite(e);
    }


    @FXML
    private void toggleReportBugPanel(ActionEvent e) {
        bugHandler.toggleReportBugPanel(e);
    }

    @FXML
    private void reportBug(ActionEvent e) {
        bugHandler.reportBug(e);
    }

    @FXML
    private void bugFilterAll(ActionEvent e) {
        bugHandler.bugFilterAll(e);
    }

    @FXML
    private void bugFilterOpen(ActionEvent e) {
        bugHandler.bugFilterOpen(e);
    }

    @FXML
    private void bugFilterProgress(ActionEvent e) {
        bugHandler.bugFilterProgress(e);
    }

    @FXML
    private void bugFilterClosed(ActionEvent e) {
        bugHandler.bugFilterClosed(e);
    }


    @FXML
    private void closeDetail(ActionEvent event) {
        slideOut();
        if (onClose != null) onClose.run();
    }


    private void populateInfo(Project project) {
        detailProjectName.setText(project.getName());
        infoDescription.setText(
                project.getDescription() == null || project.getDescription().isBlank()
                        ? "No description." : project.getDescription());
        infoCreated.setText(project.getCreatedAt() != null ? project.getCreatedAt() : "—");
        infoLeader.setText("@" + project.getOwnerId());
        statTasks.setText(String.valueOf(viewModel.taskCountProperty().get()));
        statBugs.setText(String.valueOf(viewModel.bugCountProperty().get()));
        infoMemberCount.setText(viewModel.memberCountProperty().get() + " members");
    }

    private void applyRoleVisibility(boolean leader) {
        addTaskToggleBtn.setVisible(leader);
        addTaskToggleBtn.setManaged(leader);
        inviteRow.setVisible(leader);
        inviteRow.setManaged(leader);
        reportBugRow.setVisible(true);
        reportBugRow.setManaged(true);
        reportBugToggleBtn.setVisible(true);

        if (!leader) {
            filterMenuBtn.getItems().removeIf(item ->
                    item.getText() != null && item.getText().contains("Drafts"));
        }
        roleBadge.setText(leader ? "LEADER" : "MEMBER");
        roleBadge.getStyleClass().removeAll("role-badge-leader", "role-badge-member");
        roleBadge.getStyleClass().add(leader ? "role-badge-leader" : "role-badge-member");
    }

    private void slideIn() {
        TranslateTransition tt = new TranslateTransition(Duration.millis(420), projectDetailRoot);
        tt.setToX(0);
        tt.play();
    }

    private void slideOut() {
        TranslateTransition tt = new TranslateTransition(Duration.millis(380), projectDetailRoot);
        tt.setToX(1102);
        tt.play();
    }

    private void removeScrollBars(ScrollPane sp) {
        if (sp == null) return;
        sp.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        sp.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
    }
}