package com.example.flora.Features.Project.UI;

import com.example.flora.Core.Helper.DateAndTime;
import com.example.flora.Features.Bug.model.Bug;
import com.example.flora.Features.Bug.model.BugSeverity;
import com.example.flora.Features.Bug.model.BugStatus;
import com.example.flora.Features.Project.model.Project;
import com.example.flora.Features.Project.ViewModel.ProjectDetailViewModel;
import com.example.flora.Features.Task.ViewModel.TaskViewModel;
import com.example.flora.Features.Task.model.Task;
import com.example.flora.Features.Task.model.TaskStatus;

import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import javafx.util.StringConverter;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.LinkedHashMap;
import java.util.ArrayList;

import static com.example.flora.Features.Project.ViewModel.ProjectDetailViewModel.DATE_FMT;


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
    private boolean addPanelOpen = false;
    private boolean reportBugPanelOpen = false;
    private Runnable onClose;

    public ProjectDetailUI_Controller(ProjectDetailViewModel viewModel) {
        this.viewModel = viewModel;
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        removeScrollBars(taskScroll);
        removeScrollBars(bugScroll);
        showTab(tasksTab, tabTasks);

        bugSeverityCombo.getItems().addAll(
                "🔴 CRITICAL", "🟠 HIGH", "🟡 MEDIUM", "🟢 LOW"
        );
        bugSeverityCombo.getSelectionModel().selectFirst();

        viewModel.taskCountProperty().addListener((obs, o, n) -> statTasks.setText(String.valueOf(n)));
        viewModel.bugCountProperty().addListener((obs, o, n) -> statBugs.setText(String.valueOf(n)));
        viewModel.memberCountProperty().addListener((obs, o, n) -> infoMemberCount.setText(n + " members"));

        viewModel.activeTaskFilterProperty().addListener((obs, o, n) -> renderTasks());
        viewModel.getTasks().addListener(
                (javafx.collections.ListChangeListener<Task>) c -> renderTasks());

        viewModel.activeBugFilterProperty().addListener((obs, o, n) -> renderBugs());
        viewModel.getBugs().addListener(
                (javafx.collections.ListChangeListener<Bug>) c -> renderBugs());

        viewModel.getMembers().addListener(
                (javafx.collections.ListChangeListener<String>) c -> renderMembers());
    }


    public void openProject(Project project, String currentUserId,
                            boolean isLeader, Runnable onClose) {
        this.onClose = onClose;
        viewModel.init(project, currentUserId, isLeader);
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
        addPanelOpen = !addPanelOpen;
        addTaskPanel.setVisible(addPanelOpen);
        addTaskPanel.setManaged(addPanelOpen);
        addTaskToggleBtn.setText(addPanelOpen ? "✕  Cancel" : "➕  Add Task");
        addTaskToggleBtn.getStyleClass().removeAll("btn-accept", "slide-close-btn");
        addTaskToggleBtn.getStyleClass().add(addPanelOpen ? "slide-close-btn" : "btn-accept");
        AnchorPane.setTopAnchor(taskScroll, addPanelOpen ? 170.0 : 52.0);
    }

    @FXML
    private void switchToAssignMode(ActionEvent e) {
        assignFields.setVisible(true);
        assignFields.setManaged(true);
        draftFields.setVisible(false);
        draftFields.setManaged(false);
        setModeActive(modeAssignBtn, modeDraftBtn);
    }

    @FXML
    private void switchToDraftMode(ActionEvent e) {
        assignFields.setVisible(false);
        assignFields.setManaged(false);
        draftFields.setVisible(true);
        draftFields.setManaged(true);
        setModeActive(modeDraftBtn, modeAssignBtn);
    }

    private void setModeActive(Button active, Button inactive) {
        active.getStyleClass().removeAll("task-filter-btn", "task-filter-btn-active");
        active.getStyleClass().add("task-filter-btn-active");
        inactive.getStyleClass().removeAll("task-filter-btn", "task-filter-btn-active");
        inactive.getStyleClass().add("task-filter-btn");
    }

    @FXML
    private void toggleAssignDeadline(MouseEvent e) {
        toggleDeadlineRow("assign-dl-row", assignFields, assignDeadlineChip, true);
    }

    @FXML
    private void toggleDraftDeadline(MouseEvent e) {
        toggleDeadlineRow("draft-dl-row", draftFields, draftDeadlineChip, false);
    }

    private void toggleDeadlineRow(String id, HBox anchor, Label chip, boolean isAssign) {
        int anchorIdx = addTaskPanel.getChildren().indexOf(anchor);
        boolean exists = addTaskPanel.getChildren().stream()
                .anyMatch(n -> id.equals(n.getUserData()));
        if (exists) {
            addTaskPanel.getChildren().removeIf(n -> id.equals(n.getUserData()));
            chip.setText("📅  Set deadline");
            if (isAssign) viewModel.clearPendingAssignDeadline();
            else viewModel.clearPendingDraftDeadline();
        } else {
            addTaskPanel.getChildren().add(anchorIdx + 1,
                    buildInlineDateRow(id, chip, isAssign));
        }
    }

    private HBox buildInlineDateRow(String id, Label chip, boolean isAssign) {
        HBox row = new HBox(8);
        row.setUserData(id);
        row.setAlignment(Pos.CENTER_LEFT);

        DatePicker dp = new DatePicker();
        dp.setPromptText("Pick a date...");
        dp.setPrefWidth(170);
        dp.setPrefHeight(32);
        dp.getStyleClass().add("modal-text-field");
        dp.setStyle("-fx-font-size: 12px;");
        dp.setConverter(dateConverter());

        Button minus7 = quickAdjust("−7d", dp, -7);
        Button minus1 = quickAdjust("−1d", dp, -1);
        Button plus1 = quickAdjust("+1d", dp, 1);
        Button plus7 = quickAdjust("+7d", dp, 7);

        Button okBtn = new Button("✔  Set");
        okBtn.getStyleClass().add("btn-accept");
        okBtn.setPadding(new Insets(3, 10, 3, 10));
        okBtn.setOnAction(ev -> {
            LocalDate chosen = dp.getValue();
            if (isAssign) viewModel.setPendingAssignDeadline(chosen);
            else viewModel.setPendingDraftDeadline(chosen);
            chip.setText(chosen != null ? "📅  " + chosen.format(DATE_FMT) : "📅  Set deadline");
            addTaskPanel.getChildren().removeIf(n -> id.equals(n.getUserData()));
        });

        row.getChildren().addAll(minus7, minus1, dp, plus1, plus7, okBtn);
        return row;
    }


    @FXML
    private void filterMyTasks(ActionEvent e) {
        setTaskFilter("👤  My Tasks", "MY");
    }

    @FXML
    private void filterAllTasks(ActionEvent e) {
        setTaskFilter("📋  All Tasks", "ALL");
    }

    @FXML
    private void filterCompleted(ActionEvent e) {
        setTaskFilter("✅  Completed", "COMPLETED");
    }

    @FXML
    private void filterDue(ActionEvent e) {
        setTaskFilter("⏳  Due / Overdue", "DUE");
    }

    @FXML
    private void filterDrafts(ActionEvent e) {
        setTaskFilter("✏  Drafts", "DRAFTS");
    }

    @FXML
    private void filterByAssignee(ActionEvent e) {
        filterMenuBtn.setText("👥  By Assignee");
        renderGroupedByAssignee();
    }

    private void setTaskFilter(String label, String mode) {
        filterMenuBtn.setText(label);
        viewModel.setActiveTaskFilter(mode);
    }


    private void renderTasks() {
        taskList.getChildren().clear();
        List<Task> list = viewModel.getFilteredTasks();
        if (list.isEmpty()) {
            taskList.getChildren().add(emptyLabel("No tasks for this filter."));
        } else {
            for (Task t : list) taskList.getChildren().add(buildTaskCard(t));
        }
    }

    private void renderGroupedByAssignee() {
        taskList.getChildren().clear();
        Map<String, List<Task>> grouped = new LinkedHashMap<>();
        viewModel.getTasks().stream()
                .filter(t -> !viewModel.isDraft(t))
                .forEach(t -> grouped.computeIfAbsent(t.getAssigneeId(), k -> new ArrayList<>()).add(t));

        if (grouped.isEmpty()) {
            taskList.getChildren().add(emptyLabel("No tasks yet."));
            return;
        }
        for (Map.Entry<String, List<Task>> entry : grouped.entrySet()) {
            taskList.getChildren().add(buildAssigneeHeader(entry.getKey(), entry.getValue().size()));
            for (Task t : entry.getValue()) {
                VBox card = buildTaskCard(t);
                card.setPadding(new Insets(0, 0, 0, 18));
                taskList.getChildren().add(card);
            }
        }
    }

    private VBox buildTaskCard(Task t) {
        boolean draft = viewModel.isDraft(t);

        VBox card = new VBox(0);
        card.getStyleClass().add("detail-list-row");
        card.setPrefWidth(750);

        HBox titleRow = new HBox(10);
        titleRow.setPadding(new Insets(10, 14, 4, 14));
        titleRow.setAlignment(Pos.CENTER_LEFT);

        Label titleLbl = new Label(draft ? "✏ " + t.getTitle() : t.getTitle());
        titleLbl.setTextFill(Color.web(draft ? "#FBB024" : "#EDE9F6"));
        titleLbl.getStyleClass().add("row-title");
        HBox.setHgrow(titleLbl, Priority.ALWAYS);
        titleRow.getChildren().add(titleLbl);

        if (!draft && t.getCreatedAt() != null && !t.getCreatedAt().isBlank()) {
            Label createdChip = new Label("🗓 " + t.getCreatedAt());
            createdChip.getStyleClass().add("created-at-chip");
            createdChip.setPadding(new Insets(3, 8, 3, 8));
            titleRow.getChildren().add(createdChip);
        }

        HBox metaRow = new HBox(10);
        metaRow.setPadding(new Insets(0, 14, 10, 14));
        metaRow.setAlignment(Pos.CENTER_LEFT);

        HBox chips = new HBox(10);
        chips.setAlignment(Pos.CENTER_LEFT);
        HBox actions = new HBox(8);
        actions.setAlignment(Pos.CENTER_RIGHT);
        HBox.setHgrow(actions, Priority.ALWAYS);

        Label deadlineChip = buildDeadlineChip(t);

        Label assigneeLbl = new Label(draft ? "Unassigned" : "@" + t.getAssigneeId());
        assigneeLbl.getStyleClass().add(draft ? "fixer-unclaimed" : "pill-label");
        assigneeLbl.setPadding(new Insets(3, 8, 3, 8));

        Label statusLbl = new Label(t.getStatus().name());
        statusLbl.getStyleClass().add(statusStyleClass(t.getStatus()));
        statusLbl.setPadding(new Insets(3, 8, 3, 8));
        statusLbl.setPrefWidth(95);
        statusLbl.setAlignment(Pos.CENTER);

        chips.getChildren().addAll(deadlineChip, assigneeLbl, statusLbl);

        VBox expandPanel = new VBox(8);
        expandPanel.setPadding(new Insets(0, 14, 10, 14));
        expandPanel.setVisible(false);
        expandPanel.setManaged(false);
        expandPanel.setStyle("-fx-border-color: #2D2845; -fx-border-width: 1 0 0 0;");

        boolean isAssignee = !draft
                && t.getAssigneeId() != null
                && t.getAssigneeId().equalsIgnoreCase(viewModel.getCurrentUserId());

        if (isAssignee && t.getStatus() != TaskStatus.DONE) {
            Button changeStatusBtn = new Button("⟳ Status");
            changeStatusBtn.getStyleClass().add("update-btn");
            changeStatusBtn.setPadding(new Insets(3, 8, 3, 8));
            changeStatusBtn.setOnAction(ev -> {
                boolean open = expandPanel.isVisible();
                expandPanel.getChildren().clear();
                if (!open || !"cs".equals(expandPanel.getUserData())) {
                    expandPanel.getChildren().add(buildChangeStatusRow(t, statusLbl, expandPanel));
                    expandPanel.setUserData("cs");
                    expandPanel.setVisible(true);
                    expandPanel.setManaged(true);
                    FadeTransition ft = new FadeTransition(Duration.millis(200), expandPanel);
                    ft.setFromValue(0);
                    ft.setToValue(1);
                    ft.play();
                } else {
                    expandPanel.setVisible(false);
                    expandPanel.setManaged(false);
                }
            });
            actions.getChildren().add(changeStatusBtn);
        }

        if (viewModel.isLeader()) {
            Button doneBtn = new Button("✔ Done");
            doneBtn.getStyleClass().add("btn-accept");
            doneBtn.setPadding(new Insets(3, 8, 3, 8));
            doneBtn.setOnAction(ev -> {
                viewModel.markTaskDone(t);
                statusLbl.setText(TaskStatus.DONE.name());
                statusLbl.getStyleClass().removeAll("status-todo", "status-progress", "status-review");
                statusLbl.getStyleClass().add("status-done");
            });

            Button editDlBtn = new Button("📅 Deadline");
            editDlBtn.getStyleClass().add("update-btn");
            editDlBtn.setPadding(new Insets(3, 8, 3, 8));
            editDlBtn.setOnAction(ev -> {
                boolean open = expandPanel.isVisible();
                expandPanel.getChildren().clear();
                if (!open || !"dl".equals(expandPanel.getUserData())) {
                    expandPanel.getChildren().add(buildDeadlineExpandRow(t, deadlineChip, expandPanel));
                    expandPanel.setUserData("dl");
                    expandPanel.setVisible(true);
                    expandPanel.setManaged(true);
                } else {
                    expandPanel.setVisible(false);
                    expandPanel.setManaged(false);
                }
            });

            Button reassignBtn = new Button("↺ Reassign");
            reassignBtn.getStyleClass().add("update-btn");
            reassignBtn.setPadding(new Insets(3, 8, 3, 8));
            reassignBtn.setOnAction(ev -> {
                boolean open = expandPanel.isVisible();
                expandPanel.getChildren().clear();
                if (!open || !"ra".equals(expandPanel.getUserData())) {
                    expandPanel.getChildren().add(buildReassignExpandRow(t, assigneeLbl, expandPanel));
                    expandPanel.setUserData("ra");
                    expandPanel.setVisible(true);
                    expandPanel.setManaged(true);
                } else {
                    expandPanel.setVisible(false);
                    expandPanel.setManaged(false);
                }
            });

            actions.getChildren().addAll(editDlBtn, reassignBtn, doneBtn);
        }

        metaRow.getChildren().addAll(chips, actions);
        card.getChildren().addAll(titleRow, metaRow, expandPanel);
        return card;
    }

    private HBox buildDeadlineExpandRow(Task t, Label deadlineChip, VBox expandPanel) {
        HBox row = new HBox(8);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(8, 0, 2, 0));

        Label lbl = new Label("New deadline:");
        lbl.setTextFill(Color.web("#9D8FBF"));
        lbl.setStyle("-fx-font-size: 12px;");

        LocalDate current = DateAndTime.parseDate(t.getDueDate());
        DatePicker dp = new DatePicker(current != null ? current : LocalDate.now());
        dp.setPrefWidth(160);
        dp.setPrefHeight(30);
        dp.getStyleClass().add("modal-text-field");
        dp.setStyle("-fx-font-size: 12px;");
        dp.setConverter(dateConverter());

        Button saveBtn = new Button("✔ Save");
        saveBtn.getStyleClass().add("btn-accept");
        saveBtn.setPadding(new Insets(3, 10, 3, 10));
        saveBtn.setOnAction(ev -> {
            viewModel.updateTaskDeadline(t, dp.getValue());
            refreshDeadlineChip(deadlineChip, t);
            expandPanel.setVisible(false);
            expandPanel.setManaged(false);
        });
        Button cancelBtn = new Button("✕");
        cancelBtn.getStyleClass().add("slide-close-btn");
        cancelBtn.setPadding(new Insets(3, 8, 3, 8));
        cancelBtn.setOnAction(ev -> {
            expandPanel.setVisible(false);
            expandPanel.setManaged(false);
        });

        row.getChildren().addAll(lbl, quickAdjust("−7d", dp, -7), quickAdjust("−1d", dp, -1),
                dp, quickAdjust("+1d", dp, 1), quickAdjust("+7d", dp, 7), saveBtn, cancelBtn);
        return row;
    }

    private HBox buildReassignExpandRow(Task t, Label assigneeLabel, VBox expandPanel) {
        HBox row = new HBox(8);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(8, 0, 2, 0));

        Label lbl = new Label("Reassign to:");
        lbl.setTextFill(Color.web("#9D8FBF"));
        lbl.setStyle("-fx-font-size: 12px;");

        TextField field = new TextField(t.getAssigneeId() != null ? t.getAssigneeId() : "");
        field.setPromptText("Username...");
        field.setPrefWidth(200);
        field.setPrefHeight(30);
        field.getStyleClass().add("modal-text-field");

        Button saveBtn = new Button("✔ Reassign");
        saveBtn.getStyleClass().add("btn-accept");
        saveBtn.setPadding(new Insets(3, 10, 3, 10));
        saveBtn.setOnAction(ev -> {
            String newMember = field.getText().trim();
            viewModel.reassignTask(t, newMember);
            assigneeLabel.setText("@" + newMember);
            assigneeLabel.getStyleClass().removeAll("fixer-unclaimed");
            assigneeLabel.getStyleClass().add("pill-label");
            expandPanel.setVisible(false);
            expandPanel.setManaged(false);
        });
        Button cancelBtn = new Button("✕");
        cancelBtn.getStyleClass().add("slide-close-btn");
        cancelBtn.setPadding(new Insets(3, 8, 3, 8));
        cancelBtn.setOnAction(ev -> {
            expandPanel.setVisible(false);
            expandPanel.setManaged(false);
        });

        row.getChildren().addAll(lbl, field, saveBtn, cancelBtn);
        return row;
    }

    private HBox buildChangeStatusRow(Task t, Label statusLbl, VBox expandPanel) {
        HBox row = new HBox(8);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(8, 0, 4, 0));

        Label lbl = new Label("Set status:");
        lbl.setTextFill(Color.web("#9D8FBF"));
        lbl.setStyle("-fx-font-size: 12px;");

        TaskStatus[] allStatuses = TaskStatus.values();
        Button[] statusBtns = new Button[allStatuses.length];
        for (int i = 0; i < allStatuses.length; i++) {
            TaskStatus s = allStatuses[i];
            Button sb = new Button(statusLabel(s));
            sb.getStyleClass().add(statusStyleClass(s));
            sb.setPadding(new Insets(4, 10, 4, 10));
            if (s == t.getStatus()) sb.setStyle("-fx-cursor: hand; -fx-border-width: 2;");
            else sb.setStyle("-fx-cursor: hand; -fx-opacity: 0.50;");
            sb.setOnAction(ev -> {
                viewModel.updateTaskStatus(t, s);
                statusLbl.setText(s.name());
                statusLbl.getStyleClass().removeAll("status-todo", "status-progress", "status-review", "status-done");
                statusLbl.getStyleClass().add(statusStyleClass(s));
                ScaleTransition pop = new ScaleTransition(Duration.millis(200), statusLbl);
                pop.setFromX(0.80);
                pop.setFromY(0.80);
                pop.setToX(1.0);
                pop.setToY(1.0);
                pop.play();
                FadeTransition ft = new FadeTransition(Duration.millis(180), expandPanel);
                ft.setFromValue(1);
                ft.setToValue(0);
                ft.setOnFinished(e -> {
                    expandPanel.setVisible(false);
                    expandPanel.setManaged(false);
                    expandPanel.setOpacity(1);
                });
                ft.play();
            });
            statusBtns[i] = sb;
        }
        row.getChildren().add(lbl);
        row.getChildren().addAll(statusBtns);
        return row;
    }

    private String statusLabel(TaskStatus s) {
        return switch (s) {
            case TODO -> "📌 To Do";
            case IN_PROGRESS -> "⚙ In Progress";
            case IN_REVIEW -> "👁 In Review";
            case DONE -> "✅ Done";
        };
    }


    @FXML
    private void assignTask(ActionEvent event) {
        viewModel.assignTask(
                taskTitleInput.getText().trim(),
                taskAssigneeInput.getText().trim(),
                viewModel.getPendingAssignDeadline());
        taskTitleInput.clear();
        taskAssigneeInput.clear();
        assignDeadlineChip.setText("📅  Set deadline");
        addTaskPanel.getChildren().removeIf(n -> "assign-dl-row".equals(n.getUserData()));
    }

    @FXML
    private void saveDraftTask(ActionEvent event) {
        viewModel.saveDraftTask(draftTitleInput.getText().trim(), viewModel.getPendingDraftDeadline());
        draftTitleInput.clear();
        draftDeadlineChip.setText("📅  Set deadline");
        addTaskPanel.getChildren().removeIf(n -> "draft-dl-row".equals(n.getUserData()));
    }


    @FXML
    private void sendInvite(ActionEvent event) {
        String username = inviteSearchField.getText().trim();
        boolean ok = viewModel.sendInvite(username);
        inviteFeedback.setText(ok ? "✔ Invite sent to @" + username : "✖ Could not send invite.");
        inviteFeedback.getStyleClass().removeAll("feedback-ok", "feedback-err");
        inviteFeedback.getStyleClass().add(ok ? "feedback-ok" : "feedback-err");
        inviteSearchField.clear();
        javafx.animation.PauseTransition w = new javafx.animation.PauseTransition(Duration.seconds(2.5));
        w.setOnFinished(e -> inviteFeedback.setText(""));
        w.play();
    }

    private void renderMembers() {
        memberList.getChildren().clear();
        String leaderId = viewModel.getCurrentProject().getOwnerId();
        for (String username : viewModel.getMembers()) {
            memberList.getChildren().add(buildMemberRow(username, username.equals(leaderId)));
        }
    }

    private HBox buildMemberRow(String username, boolean isProjectLeader) {
        HBox row = new HBox(12);
        row.getStyleClass().add("detail-list-row");
        row.setPadding(new Insets(10, 14, 10, 14));
        row.setAlignment(Pos.CENTER_LEFT);

        Label avatar = new Label(username.substring(0, 1).toUpperCase());
        avatar.getStyleClass().add("member-avatar");
        avatar.setPrefSize(36, 36);
        avatar.setAlignment(Pos.CENTER);

        Label name = new Label("@" + username);
        name.setTextFill(Color.web("#EDE9F6"));
        name.getStyleClass().add("row-title");

        HBox spacer = new HBox();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label badge = new Label(isProjectLeader ? "LEADER" : "MEMBER");
        badge.getStyleClass().add(isProjectLeader ? "role-badge-leader" : "role-badge-member");
        badge.setPadding(new Insets(3, 8, 3, 8));

        row.getChildren().addAll(avatar, name, spacer, badge);

        if (viewModel.isLeader() && !username.equals(viewModel.getCurrentUserId())) {
            Button removeBtn = new Button("Remove");
            removeBtn.getStyleClass().add("slide-close-btn");
            removeBtn.setOnAction(e -> viewModel.removeMember(username));
            row.getChildren().add(removeBtn);
        }
        return row;
    }


    @FXML
    private void toggleReportBugPanel(ActionEvent e) {
        reportBugPanelOpen = !reportBugPanelOpen;
        reportBugPanel.setVisible(reportBugPanelOpen);
        reportBugPanel.setManaged(reportBugPanelOpen);

        double filterTop = reportBugPanelOpen ? 190.0 : 52.0;
        double scrollTop = reportBugPanelOpen ? 230.0 : 92.0;
        AnchorPane.setTopAnchor(bugScroll, scrollTop);

        if (reportBugPanelOpen) {
            reportBugPanel.setOpacity(0);
            reportBugPanel.setTranslateY(-12);
            FadeTransition ft = new FadeTransition(Duration.millis(220), reportBugPanel);
            ft.setFromValue(0);
            ft.setToValue(1);
            ft.play();
            javafx.animation.TranslateTransition tt =
                    new javafx.animation.TranslateTransition(Duration.millis(220), reportBugPanel);
            tt.setFromY(-12);
            tt.setToY(0);
            tt.play();
            reportBugToggleBtn.setText("✕  Cancel");
            reportBugToggleBtn.getStyleClass().removeAll("btn-decline");
            reportBugToggleBtn.getStyleClass().add("slide-close-btn");

            bugTitleInput.clear();
            bugReporterInput.clear();
            bugSeverityCombo.getSelectionModel().selectFirst();
            bugReportFeedback.setText("");
        } else {
            reportBugToggleBtn.setText("🐛  Report Bug");
            reportBugToggleBtn.getStyleClass().removeAll("slide-close-btn");
            reportBugToggleBtn.getStyleClass().add("btn-decline");
        }
    }


    @FXML
    private void reportBug(ActionEvent event) {
        String title = bugTitleInput.getText().trim();
        String reporter = bugReporterInput.getText().trim();
        String severityRaw = bugSeverityCombo.getValue();

        if (title.isEmpty()) {
            showBugFeedback("⚠  Please enter a bug title.", false);
            return;
        }
        if (reporter.isEmpty()) {
            showBugFeedback("⚠  Reporter username is required.", false);
            return;
        }
        if (severityRaw == null) {
            showBugFeedback("⚠  Please select a severity.", false);
            return;
        }

        BugSeverity severity = parseSeverity(severityRaw);

        viewModel.reportBug(title, reporter, severity);

        showBugFeedback("✔  Bug reported successfully!", true);

        bugTitleInput.clear();
        bugReporterInput.clear();
        bugSeverityCombo.getSelectionModel().selectFirst();

        javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(Duration.seconds(1.4));
        pause.setOnFinished(e -> {
            if (reportBugPanelOpen) toggleReportBugPanel(null);
        });
        pause.play();
    }

    private void showBugFeedback(String msg, boolean ok) {
        bugReportFeedback.setText(msg);
        bugReportFeedback.setStyle("-fx-text-fill: " + (ok ? "#34D399" : "#F87171") + "; -fx-font-size:12px;");
    }

    private BugSeverity parseSeverity(String raw) {
        if (raw == null) return BugSeverity.MEDIUM;
        String upper = raw.toUpperCase();
        if (upper.contains("CRITICAL")) return BugSeverity.CRITICAL;
        if (upper.contains("HIGH")) return BugSeverity.HIGH;
        if (upper.contains("LOW")) return BugSeverity.LOW;
        return BugSeverity.MEDIUM;
    }


    @FXML
    private void bugFilterAll(ActionEvent e) {
        setBugFilter("ALL", bugFilterAll);
    }

    @FXML
    private void bugFilterOpen(ActionEvent e) {
        setBugFilter("OPEN", bugFilterOpen);
    }

    @FXML
    private void bugFilterProgress(ActionEvent e) {
        setBugFilter("IN_PROGRESS", bugFilterProgress);
    }

    @FXML
    private void bugFilterClosed(ActionEvent e) {
        setBugFilter("CLOSED", bugFilterClosed);
    }

    private void setBugFilter(String filter, Button active) {
        viewModel.setActiveBugFilter(filter);
        for (Button b : new Button[]{bugFilterAll, bugFilterOpen, bugFilterProgress, bugFilterClosed}) {
            b.getStyleClass().removeAll("task-filter-btn-active", "task-filter-btn");
            b.getStyleClass().add(b == active ? "task-filter-btn-active" : "task-filter-btn");
        }
    }


    private void renderBugs() {
        bugList.getChildren().clear();
        List<Bug> list = viewModel.getFilteredBugs();
        if (list.isEmpty()) {
            bugList.getChildren().add(emptyLabel("No bugs in this view."));
            return;
        }
        int delay = 0;
        for (Bug bug : list) {
            VBox card = buildBugCard(bug);
            bugList.getChildren().add(card);
            staggerIn(card, delay);
            delay += 40;
        }
    }


    private VBox buildBugCard(Bug bug) {
        VBox card = new VBox(0);
        card.getStyleClass().add("detail-list-row");
        card.setStyle(severityLeftBorder(bug.getSeverity()));


        HBox line1 = new HBox(10);
        line1.setPadding(new Insets(10, 14, 10, 14));
        line1.setAlignment(Pos.CENTER_LEFT);


        if (bug.isUnclaimed() && !bug.isClosed()) {
            Label dot = new Label("●");
            dot.setStyle("-fx-text-fill: #F87171; -fx-font-size: 10px;"
                    + "-fx-effect: dropshadow(gaussian, rgba(248,113,113,0.70), 6,0,0,0);");
            line1.getChildren().add(dot);
        }

        Label titleLbl = new Label("🐛  " + bug.getTitle());
        titleLbl.setTextFill(Color.web("#EDE9F6"));
        titleLbl.setWrapText(true);
        titleLbl.getStyleClass().add("row-title");
        HBox.setHgrow(titleLbl, Priority.ALWAYS);


        Label sevBadge = new Label(bug.getSeverity().displayName());
        sevBadge.getStyleClass().add(severityBadgeClass(bug.getSeverity()));
        sevBadge.setPadding(new Insets(3, 8, 3, 8));

        Label reporterChip = new Label("👤 @" + bug.getReportedByUserId());
        reporterChip.getStyleClass().add("unclaimed-chip");
        reporterChip.setPadding(new Insets(3, 8, 3, 8));

        Label fixerLbl = new Label(bug.isUnclaimed() ? "Unclaimed" : "🔧 @" + bug.getFixingUserId());
        fixerLbl.getStyleClass().add(bug.isUnclaimed() ? "fixer-unclaimed" : "pill-label");
        fixerLbl.setPadding(new Insets(3, 8, 3, 8));

        Label statusLbl = new Label(bug.getStatus().displayName());
        statusLbl.getStyleClass().add(bugStatusStyleClass(bug.getStatus()));
        statusLbl.setPadding(new Insets(3, 8, 3, 8));
        statusLbl.setAlignment(Pos.CENTER);

        line1.getChildren().addAll(titleLbl, reporterChip, sevBadge, fixerLbl, statusLbl);

        VBox expandPanel = new VBox(8);
        expandPanel.setPadding(new Insets(4, 14, 10, 14));
        expandPanel.setStyle("-fx-border-color: #2D2845; -fx-border-width: 1 0 0 0;");
        expandPanel.setVisible(false);
        expandPanel.setManaged(false);

        String currentUserId = viewModel.getCurrentUserId();
        boolean isFixer = !bug.isUnclaimed() && currentUserId.equals(bug.getFixingUserId());
        boolean isLeader = viewModel.isLeader();

        if (!bug.isClosed()) {

            HBox actionRow = new HBox(8);
            actionRow.setAlignment(Pos.CENTER_LEFT);
            actionRow.setPadding(new Insets(6, 0, 2, 0));

            if (isFixer) {
                Button markFixed = new Button("✅  Mark as Fixed");
                markFixed.getStyleClass().add("btn-accept");
                markFixed.setPadding(new Insets(4, 12, 4, 12));
                markFixed.setOnAction(e -> {
                    viewModel.markBugFixed(bug);
                    // Animate card close
                    FadeTransition ft = new FadeTransition(Duration.millis(300), card);
                    ft.setFromValue(1);
                    ft.setToValue(0);
                    ft.setOnFinished(ev -> renderBugs());
                    ft.play();
                });
                actionRow.getChildren().add(markFixed);

                Button updateStatusBtn = new Button("⟳ Update Status");
                updateStatusBtn.getStyleClass().add("update-btn");
                updateStatusBtn.setPadding(new Insets(4, 12, 4, 12));
                updateStatusBtn.setOnAction(e -> {
                    BugStatus next = bug.getStatus() == BugStatus.IN_PROGRESS
                            ? BugStatus.OPEN : BugStatus.IN_PROGRESS;
                    viewModel.updateBugStatus(bug, next);
                    statusLbl.setText(next.displayName());
                    statusLbl.getStyleClass().removeAll("bug-status-open", "bug-status-progress", "bug-status-closed", "status-todo", "status-progress", "status-done");
                    statusLbl.getStyleClass().add(bugStatusStyleClass(next));
                    ScaleTransition pop = new ScaleTransition(Duration.millis(180), statusLbl);
                    pop.setFromX(0.8);
                    pop.setFromY(0.8);
                    pop.setToX(1.0);
                    pop.setToY(1.0);
                    pop.play();
                });
                actionRow.getChildren().add(updateStatusBtn);
            }

            if (!isLeader && bug.isUnclaimed()) {
                Button claimBtn = new Button("🔧  I'll fix it");
                claimBtn.getStyleClass().add("btn-accept");
                claimBtn.setPadding(new Insets(4, 12, 4, 12));
                claimBtn.setOnAction(e -> {
                    viewModel.claimBug(bug);
                    fixerLbl.setText("🔧 @" + currentUserId);
                    fixerLbl.getStyleClass().removeAll("fixer-unclaimed");
                    fixerLbl.getStyleClass().add("pill-label");
                    statusLbl.setText(BugStatus.IN_PROGRESS.displayName());
                    statusLbl.getStyleClass().removeAll("bug-status-open", "status-todo");
                    statusLbl.getStyleClass().add(bugStatusStyleClass(BugStatus.IN_PROGRESS));
                    expandPanel.setVisible(false);
                    expandPanel.setManaged(false);
                    renderBugs();
                });
                actionRow.getChildren().add(claimBtn);
            }

            if (isLeader) {
                String btnLabel = bug.isUnclaimed() ? "👤  Assign →" : "🔄  Re-assign →";
                Button assignBugBtn = new Button(btnLabel);
                assignBugBtn.getStyleClass().add("update-btn");
                assignBugBtn.setPadding(new Insets(4, 12, 4, 12));
                assignBugBtn.setOnAction(e -> {
                    boolean open = expandPanel.getUserData() != null
                            && "assign".equals(expandPanel.getUserData())
                            && expandPanel.isVisible();
                    expandPanel.getChildren().clear();
                    if (!open) {
                        expandPanel.getChildren().add(buildBugAssignRow(bug, expandPanel, fixerLbl, statusLbl));
                        expandPanel.setUserData("assign");
                        expandPanel.setVisible(true);
                        expandPanel.setManaged(true);
                        FadeTransition ft = new FadeTransition(Duration.millis(200), expandPanel);
                        ft.setFromValue(0);
                        ft.setToValue(1);
                        ft.play();
                    } else {
                        expandPanel.setVisible(false);
                        expandPanel.setManaged(false);
                    }
                });
                actionRow.getChildren().add(assignBugBtn);
            }

            if (!actionRow.getChildren().isEmpty())
                expandPanel.getChildren().add(actionRow);
        }

        card.getChildren().addAll(line1, expandPanel);

        line1.setOnMouseClicked(e -> {
            if (expandPanel.getChildren().isEmpty()) return;
            boolean open = expandPanel.isVisible();
            if (!open) {
                expandPanel.setOpacity(0);
                expandPanel.setVisible(true);
                expandPanel.setManaged(true);
                FadeTransition ft = new FadeTransition(Duration.millis(200), expandPanel);
                ft.setFromValue(0);
                ft.setToValue(1);
                ft.play();
            } else {
                FadeTransition ft = new FadeTransition(Duration.millis(180), expandPanel);
                ft.setFromValue(1);
                ft.setToValue(0);
                ft.setOnFinished(ev -> {
                    expandPanel.setVisible(false);
                    expandPanel.setManaged(false);
                    expandPanel.setOpacity(1);
                });
                ft.play();
            }
            line1.setStyle("-fx-cursor: hand;");
        });
        line1.setStyle("-fx-cursor: hand;");

        return card;
    }

    private HBox buildBugAssignRow(Bug bug, VBox expandPanel, Label fixerLbl, Label statusLbl) {
        HBox aRow = new HBox(8);
        aRow.setAlignment(Pos.CENTER_LEFT);
        aRow.setPadding(new Insets(2, 0, 2, 0));

        boolean isReassign = !bug.isUnclaimed();
        Label lbl = new Label(isReassign ? "Re-assign to:" : "Assign to member:");
        lbl.setTextFill(Color.web("#9D8FBF"));
        lbl.setStyle("-fx-font-size:12px;");

        TextField f = new TextField(isReassign ? bug.getFixingUserId() : "");
        f.setPromptText("Username...");
        f.setPrefWidth(200);
        f.setPrefHeight(30);
        f.getStyleClass().add("modal-text-field");

        String okLabel = isReassign ? "✔ Re-assign" : "✔ Assign";
        Button ok = new Button(okLabel);
        ok.getStyleClass().add("btn-accept");
        ok.setPadding(new Insets(3, 10, 3, 10));
        ok.setOnAction(ev -> {
            String assignee = f.getText().trim();
            if (!assignee.isEmpty()) {
                viewModel.assignBug(bug, assignee);
                fixerLbl.setText("🔧 @" + assignee);
                fixerLbl.getStyleClass().removeAll("fixer-unclaimed");
                fixerLbl.getStyleClass().add("pill-label");
                statusLbl.setText(BugStatus.IN_PROGRESS.displayName());
                statusLbl.getStyleClass().removeAll("bug-status-open", "status-todo");
                statusLbl.getStyleClass().add(bugStatusStyleClass(BugStatus.IN_PROGRESS));
                // Animate the fixer label so it's clear something changed
                ScaleTransition pop = new ScaleTransition(Duration.millis(180), fixerLbl);
                pop.setFromX(0.8);
                pop.setFromY(0.8);
                pop.setToX(1.0);
                pop.setToY(1.0);
                pop.play();
            }
            expandPanel.setVisible(false);
            expandPanel.setManaged(false);
        });

        Button cx = new Button("✕");
        cx.getStyleClass().add("slide-close-btn");
        cx.setPadding(new Insets(3, 8, 3, 8));
        cx.setOnAction(ev -> {
            expandPanel.setVisible(false);
            expandPanel.setManaged(false);
        });

        aRow.getChildren().addAll(lbl, f, ok, cx);
        return aRow;
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
        reportBugRow.setManaged(true); // all users can see bug row
        reportBugToggleBtn.setVisible(true); // but form inside is gated by leader check in toggleReportBugPanel

        if (!leader) {
            filterMenuBtn.getItems().removeIf(item ->
                    item.getText() != null && item.getText().contains("Drafts"));
        }
        roleBadge.setText(leader ? "LEADER" : "MEMBER");
        roleBadge.getStyleClass().removeAll("role-badge-leader", "role-badge-member");
        roleBadge.getStyleClass().add(leader ? "role-badge-leader" : "role-badge-member");
    }

    private void staggerIn(VBox card, int delayMs) {
        card.setOpacity(0);
        card.setTranslateY(14);
        FadeTransition ft = new FadeTransition(Duration.millis(260), card);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.setDelay(Duration.millis(delayMs));
        ft.play();
        javafx.animation.TranslateTransition tt =
                new javafx.animation.TranslateTransition(Duration.millis(260), card);
        tt.setFromY(14);
        tt.setToY(0);
        tt.setDelay(Duration.millis(delayMs));
        tt.play();
    }

    private Label buildDeadlineChip(Task t) {
        Label chip = new Label();
        refreshDeadlineChip(chip, t);
        return chip;
    }

    private void refreshDeadlineChip(Label chip, Task t) {
        LocalDate deadline = DateAndTime.parseDate(t.getDueDate());
        if (deadline == null) {
            chip.setText("No deadline");
            chip.setStyle(chipStyle("#4A4060", "rgba(74,64,96,0.10)", "rgba(74,64,96,0.25)"));
        } else {
            long days = java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), deadline);
            String col, bg, br;
            if (days < 0) {
                col = "#F87171";
                bg = "rgba(248,113,113,0.12)";
                br = "rgba(248,113,113,0.35)";
            } else if (days <= 3) {
                col = "#FBB024";
                bg = "rgba(251,176,36,0.12)";
                br = "rgba(251,176,36,0.35)";
            } else {
                col = "#34D399";
                bg = "rgba(52,211,153,0.10)";
                br = "rgba(52,211,153,0.30)";
            }
            chip.setText((days < 0 ? "⚠ " : "📅 ") + deadline.format(DATE_FMT));
            chip.setStyle(chipStyle(col, bg, br));
        }
        chip.setPadding(new Insets(3, 8, 3, 8));
    }

    private String chipStyle(String col, String bg, String br) {
        return "-fx-background-color:" + bg + ";"
                + "-fx-background-radius:20;"
                + "-fx-border-color:" + br + ";"
                + "-fx-border-radius:20;"
                + "-fx-border-width:1;"
                + "-fx-text-fill:" + col + ";"
                + "-fx-font-size:11px;"
                + "-fx-font-weight:bold;";
    }

    private String statusStyleClass(TaskStatus s) {
        if (s == null) return "status-todo";
        return switch (s) {
            case DONE -> "status-done";
            case IN_PROGRESS -> "status-progress";
            case IN_REVIEW -> "status-review";
            default -> "status-todo";
        };
    }

    private String bugStatusStyleClass(BugStatus s) {
        if (s == null) return "status-todo";
        return switch (s) {
            case CLOSED -> "status-done";
            case IN_PROGRESS -> "status-progress";
            default -> "status-todo";
        };
    }

    private String severityBadgeClass(BugSeverity s) {
        return switch (s) {
            case CRITICAL -> "severity-critical";
            case HIGH -> "severity-high";
            case MEDIUM -> "severity-medium";
            case LOW -> "severity-low";
        };
    }

    private String severityLeftBorder(BugSeverity s) {
        String color = switch (s) {
            case CRITICAL -> "rgba(248,113,113,0.70)";
            case HIGH -> "rgba(251,146,60,0.70)";
            case MEDIUM -> "rgba(251,183,36,0.60)";
            case LOW -> "rgba(52,211,153,0.50)";
        };
        return "-fx-border-color: " + color + " transparent transparent transparent;"
                + "-fx-border-width: 0 0 0 3;"
                + "-fx-background-radius: 12;"
                + "-fx-border-radius: 12;";
    }

    private HBox buildAssigneeHeader(String assignee, int count) {
        HBox h = new HBox(10);
        h.setAlignment(Pos.CENTER_LEFT);
        h.setPadding(new Insets(12, 14, 4, 14));

        Label av = new Label(assignee.substring(0, 1).toUpperCase());
        av.getStyleClass().add("member-avatar");
        av.setPrefSize(26, 26);
        av.setAlignment(Pos.CENTER);

        Label name = new Label("@" + assignee);
        name.setTextFill(Color.web("#C4B5F5"));
        name.setStyle("-fx-font-weight: bold; -fx-font-size: 13px;");

        Label cnt = new Label(count + (count == 1 ? " task" : " tasks"));
        cnt.getStyleClass().add("card-section-label");
        cnt.setPadding(new Insets(2, 8, 2, 8));
        cnt.setStyle("-fx-background-color: rgba(109,79,194,0.15);"
                + "-fx-background-radius: 10; -fx-border-color: rgba(109,79,194,0.30);"
                + "-fx-border-radius: 10; -fx-border-width: 1;");

        h.getChildren().addAll(av, name, cnt);
        return h;
    }

    private Button quickAdjust(String label, DatePicker dp, long days) {
        Button b = new Button(label);
        b.getStyleClass().add("update-btn");
        b.setPadding(new Insets(3, 6, 3, 6));
        b.setStyle("-fx-font-size:10px;");
        b.setOnAction(ev -> {
            LocalDate cur = dp.getValue() != null ? dp.getValue() : LocalDate.now();
            dp.setValue(cur.plusDays(days));
        });
        return b;
    }

    private StringConverter<LocalDate> dateConverter() {
        return new StringConverter<>() {
            @Override
            public String toString(LocalDate d) {
                return d == null ? "" : d.format(DATE_FMT);
            }

            @Override
            public LocalDate fromString(String s) {
                try {
                    return (s == null || s.isBlank()) ? null : LocalDate.parse(s, DATE_FMT);
                } catch (Exception e) {
                    return null;
                }
            }
        };
    }

    private Label emptyLabel(String msg) {
        Label l = new Label(msg);
        l.setTextFill(Color.web("#4A4060"));
        l.setStyle("-fx-font-size:13px; -fx-font-style:italic;");
        l.setPadding(new Insets(20, 14, 10, 14));
        return l;
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