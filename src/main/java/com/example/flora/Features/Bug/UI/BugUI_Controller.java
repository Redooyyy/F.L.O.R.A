package com.example.flora.Features.Bug.UI;

import com.example.flora.Features.Bug.model.Bug;
import com.example.flora.Features.Bug.model.BugSeverity;
import com.example.flora.Features.Bug.model.BugStatus;
import com.example.flora.Features.Bug.service.BugService.ProjectSummary;
import com.example.flora.Features.Bug.viewmodel.BugViewModel;
import com.example.flora.Features.Home.UI.HomeUI_Controller;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;


public class BugUI_Controller implements Initializable {

    @FXML
    private ScrollPane leftScroll;
    @FXML
    private VBox projectBugSummaryScroll;
    @FXML
    private Label activeSectionLabel;
    @FXML
    private Label bugCountChip;
    @FXML
    private MenuButton severityFilter;
    @FXML
    private Button btnStatusAll;
    @FXML
    private Button btnStatusOpen;
    @FXML
    private Button btnStatusClaimed;
    @FXML
    private Button btnStatusClosed;
    @FXML
    private ScrollPane bugScroll;
    @FXML
    private VBox bugCardList;
    @FXML
    private AnchorPane bugDetailSlide;
    @FXML
    private Button closeDetailBtn;
    @FXML
    private Label detailSeverityBadge;
    @FXML
    private Label detailTitle;
    @FXML
    private Label detailDesc;
    @FXML
    private Label detailReporter;
    @FXML
    private Label detailAssignee;
    @FXML
    private Label detailStatus;
    @FXML
    private Label detailDate;
    @FXML
    private HBox detailActionRow;
    @FXML
    private HBox detailAssignRow;
    @FXML
    private TextField detailAssignInput;

    private final BugViewModel viewModel;
    private final HomeUI_Controller homeController;

    private boolean detailOpen = false;

    public BugUI_Controller(BugViewModel viewModel,HomeUI_Controller homeController) {
        this.homeController = homeController;
        this.viewModel = viewModel;
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        removeScrollBars(leftScroll);
        removeScrollBars(bugScroll);

        bindProperties();
        observeFilteredBugs();
        observeSelectedBug();
        buildProjectSummaryPanel();
    }


    private void bindProperties() {
        activeSectionLabel.textProperty().bind(viewModel.activeSectionLabel());
        bugCountChip.textProperty().bind(viewModel.bugCountProperty().asString());
    }

    private void observeFilteredBugs() {
        viewModel.filteredBugs().addListener(
                (javafx.collections.ListChangeListener<Bug>) change -> renderBugCards()
        );
        // Initial render
        renderBugCards();
    }

    private void observeSelectedBug() {
        viewModel.selectedBugProperty().addListener((obs, oldBug, newBug) -> {
            if (newBug == null) {
                if (detailOpen) {
                    slideDetailOut();
                    detailOpen = false;
                }
            } else {
                populateDetailPanel(newBug);
                if (!detailOpen) {
                    slideDetailIn();
                    detailOpen = true;
                } else {
                    popDetail();
                }
            }
        });
    }


    private void buildProjectSummaryPanel() {
        viewModel.projectSummaries().addListener((javafx.collections.ListChangeListener<ProjectSummary>) c -> {
            renderProjectSummaryPanel();
        });
        renderProjectSummaryPanel();
    }

    private void renderProjectSummaryPanel() {
        projectBugSummaryScroll.getChildren()
                .removeIf(n -> !(n instanceof Label l && l.getStyleClass().contains("left-panel-header")));

        viewModel.projectSummaries().forEach(summary ->
                projectBugSummaryScroll.getChildren().add(buildProjectCard(summary))
        );
    }

    private VBox buildProjectCard(ProjectSummary summary) {
        boolean isAll = (summary.projectName() == null);
        String activeProject = viewModel.getActiveProject();

        VBox card = new VBox(6);
        card.getStyleClass().add(
                (isAll && activeProject == null) || summary.projectName() != null && summary.projectName().equals(activeProject)
                        ? "proj-summary-card-active" : "proj-summary-card"
        );

        HBox topRow = new HBox(8);
        topRow.setAlignment(Pos.CENTER_LEFT);

        Label nameLabel = new Label(isAll ? "📂  All Projects" : "📁  " + summary.projectName());
        nameLabel.getStyleClass().add("proj-name-label");

        Label countLabel = new Label(summary.openCount() + " open");
        countLabel.getStyleClass().add("proj-bug-count-label");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        topRow.getChildren().addAll(nameLabel, spacer, countLabel);

        if (!isAll) {
            HBox leaderRow = new HBox(5);
            leaderRow.setAlignment(Pos.CENTER_LEFT);
            Label leaderKey = new Label("Leader:");
            leaderKey.setStyle("-fx-text-fill: #4A4060; -fx-font-size: 10px;");
            Label leaderVal = new Label("@" + viewModel.getUsernameById(summary.leaderUserId()));
            leaderVal.setStyle("-fx-text-fill: #A855F7; -fx-font-size: 10px; -fx-font-weight: bold;");
            leaderRow.getChildren().addAll(leaderKey, leaderVal);
            card.getChildren().addAll(topRow, leaderRow);
        } else {
            card.getChildren().add(topRow);
        }

        HBox barRow = new HBox(4);
        barRow.setPadding(new Insets(2, 0, 0, 0));
        barRow.setAlignment(Pos.CENTER_LEFT);
        if (summary.criticalOpen() > 0)
            barRow.getChildren().add(severityMini("🔴 " + summary.criticalOpen() + " Crit", "proj-bug-count-label"));
        if (summary.highOpen() > 0) barRow.getChildren().add(severityMini("🟠 " + summary.highOpen(), "severity-high"));
        if (summary.mediumOpen() > 0)
            barRow.getChildren().add(severityMini("🟡 " + summary.mediumOpen(), "severity-medium"));
        if (summary.lowOpen() > 0) barRow.getChildren().add(severityMini("🟢 " + summary.lowOpen(), "severity-low"));
        card.getChildren().add(barRow);

        card.setOnMouseClicked(e -> {
            viewModel.selectProject(isAll ? null : summary.projectName());
            refreshProjectCardStyles();
            pulseCard(card);
        });

        return card;
    }

    private void refreshProjectCardStyles() {
        String active = viewModel.getActiveProject();
        projectBugSummaryScroll.getChildren().forEach(n -> {
            if (n instanceof VBox card) {
                card.getStyleClass().removeAll("proj-summary-card", "proj-summary-card-active");
                String text = ((Label) ((HBox) card.getChildren().get(0)).getChildren().get(0)).getText();
                boolean isThis = (active == null && text.contains("All"))
                        || (active != null && text.contains(active));
                card.getStyleClass().add(isThis ? "proj-summary-card-active" : "proj-summary-card");
            }
        });
    }


    @FXML
    private void filterAll() {
        viewModel.setSeverityFilter(null);
        severityFilter.setText("Severity ▾");
    }

    @FXML
    private void filterCritical() {
        viewModel.setSeverityFilter(BugSeverity.CRITICAL);
        severityFilter.setText("🔴 Critical");
    }

    @FXML
    private void filterHigh() {
        viewModel.setSeverityFilter(BugSeverity.HIGH);
        severityFilter.setText("🟠 High");
    }

    @FXML
    private void filterMedium() {
        viewModel.setSeverityFilter(BugSeverity.MEDIUM);
        severityFilter.setText("🟡 Medium");
    }

    @FXML
    private void filterLow() {
        viewModel.setSeverityFilter(BugSeverity.LOW);
        severityFilter.setText("🟢 Low");
    }

    @FXML
    private void statusAll() {
        viewModel.setStatusFilter(null);
        setActiveStatusBtn(btnStatusAll);
    }

    @FXML
    private void statusOpen() {
        viewModel.setStatusFilter(BugStatus.OPEN);
        setActiveStatusBtn(btnStatusOpen);
    }

    @FXML
    private void statusClaimed() {
        viewModel.setStatusFilter(BugStatus.IN_PROGRESS);
        setActiveStatusBtn(btnStatusClaimed);
    }

    @FXML
    private void statusClosed() {
        viewModel.setStatusFilter(BugStatus.CLOSED);
        setActiveStatusBtn(btnStatusClosed);
    }

    private void setActiveStatusBtn(Button active) {
        for (Button b : new Button[]{btnStatusAll, btnStatusOpen, btnStatusClaimed, btnStatusClosed}) {
            b.getStyleClass().removeAll("status-filter-btn", "status-filter-btn-active");
            b.getStyleClass().add(b == active ? "status-filter-btn-active" : "status-filter-btn");
        }
    }


    private void renderBugCards() {
        bugCardList.getChildren().clear();
        if (viewModel.filteredBugs().isEmpty()) {
            Label empty = new Label("✅  No bugs matching the current filter.");
            empty.setStyle("-fx-text-fill: #4A4060; -fx-font-size:13px; -fx-font-style:italic; -fx-padding: 20 0 0 0;");
            bugCardList.getChildren().add(empty);
            return;
        }
        int delay = 0;
        for (Bug bug : viewModel.filteredBugs()) {
            VBox card = buildBugCard(bug);
            bugCardList.getChildren().add(card);
            staggerIn(card, delay);
            delay += 45;
        }
    }

    private VBox buildBugCard(Bug bug) {
        VBox card = new VBox(8);
        card.getStyleClass().addAll("bug-card", severityCardClass(bug.getSeverity()));
        card.setUserData(bug);

        HBox row1 = new HBox(8);
        row1.setAlignment(Pos.CENTER_LEFT);
        if (bug.isUnclaimed() && bug.getStatus() != BugStatus.CLOSED) {
            Region dot = new Region();
            dot.getStyleClass().add("pulse-dot");
            row1.getChildren().add(dot);
        }
        Label title = new Label(bug.getTitle());
        title.getStyleClass().add("bug-card-title");
        title.setMaxWidth(290);
        Region sp1 = new Region();
        HBox.setHgrow(sp1, Priority.ALWAYS);
        Label sevBadge = new Label(bug.getSeverity().displayName());
        sevBadge.getStyleClass().add(severityBadgeClass(bug.getSeverity()));
        row1.getChildren().addAll(title, sp1, sevBadge);

        Label desc = new Label(truncate(bug.getDescription(), 80));
        desc.getStyleClass().add("bug-card-desc");
        desc.setWrapText(true);

        HBox row3 = new HBox(8);
        row3.setAlignment(Pos.CENTER_LEFT);
        Label statusChip = new Label(bug.getStatus().displayName());
        statusChip.getStyleClass().add(statusBadgeClass(bug.getStatus()));
        row3.getChildren().add(statusChip);
        if (bug.isUnclaimed() && bug.getStatus() != BugStatus.CLOSED) {
            Label unclaimedChip = new Label("● Unclaimed");
            unclaimedChip.getStyleClass().add("unclaimed-chip");
            row3.getChildren().add(unclaimedChip);
        } else if (bug.getFixingUserId() != null) {
            Label assigneeChip = new Label("🔧 @" + bug.getFixingUserId());
            assigneeChip.getStyleClass().add("assignee-chip");
            row3.getChildren().add(assigneeChip);
        }
        Region sp3 = new Region();
        HBox.setHgrow(sp3, Priority.ALWAYS);
        Label projectChip = new Label("📁 " + bug.getProjectName());
        projectChip.getStyleClass().add("bug-card-meta");
        row3.getChildren().addAll(sp3, projectChip);

        HBox row4 = new HBox(10);
        row4.setAlignment(Pos.CENTER_LEFT);
        Label reporterLabel = new Label("👤 Reported by @" + bug.getReportedByUserId());
        reporterLabel.getStyleClass().add("bug-card-meta");
        Region sp4 = new Region();
        HBox.setHgrow(sp4, Priority.ALWAYS);
        Label leaderLabel = new Label("👑 @" + viewModel.getProjectLeader(bug.getProjectName()));
        leaderLabel.getStyleClass().add("leader-chip");
        Label dateLabel = new Label("📅 " + bug.getReportedDate());
        dateLabel.getStyleClass().add("bug-card-meta");
        row4.getChildren().addAll(reporterLabel, sp4, leaderLabel, dateLabel);

        card.getChildren().addAll(row1, desc, row3, row4);

        card.setOnMouseClicked(e -> viewModel.selectBug(bug));
        return card;
    }


    private void populateDetailPanel(Bug bug) {
        boolean isProjectLeader = viewModel.isProjectLeader(bug.getProjectName());
        boolean isFixer = viewModel.isCurrentUserFixer(bug);

        detailTitle.setText(bug.getTitle());
        detailDesc.setText(bug.getDescription().isBlank() ? "No description provided." : bug.getDescription());
        detailReporter.setText("@" + bug.getReportedByUserId());
        detailAssignee.setText(bug.isUnclaimed() ? "Unclaimed" : "@" + bug.getFixingUserId());
        detailDate.setText(bug.getReportedDate());

        detailSeverityBadge.setText(bug.getSeverity().displayName());
        detailSeverityBadge.getStyleClass().setAll(severityBadgeClass(bug.getSeverity()));

        detailStatus.setText(bug.getStatus().displayName());
        detailStatus.getStyleClass().setAll(statusBadgeClass(bug.getStatus()));

        detailActionRow.getChildren().clear();
        detailAssignRow.setVisible(false);
        detailAssignRow.setManaged(false);

        if (!bug.isClosed()) {
            if (isFixer) {
                Button markFixed = new Button("✅  Mark as Fixed");
                markFixed.getStyleClass().add("btn-accept");
                markFixed.setPadding(new Insets(4, 14, 4, 14));
                markFixed.setOnAction(e -> viewModel.markFixed(bug));
                detailActionRow.getChildren().add(markFixed);

                Button toggleStatus = new Button(
                        bug.getStatus() == BugStatus.IN_PROGRESS ? "⏸  Pause" : "▶  Resume");
                toggleStatus.getStyleClass().add("update-btn");
                toggleStatus.setPadding(new Insets(4, 14, 4, 14));
                toggleStatus.setOnAction(e -> viewModel.toggleProgress(bug));
                detailActionRow.getChildren().add(toggleStatus);
            }

            if (isProjectLeader) {
                String btnLabel = bug.isUnclaimed() ? "👤  Assign to…" : "🔄  Re-assign to…";
                Button assignBtn = new Button(btnLabel);
                assignBtn.getStyleClass().add("update-btn");
                assignBtn.setPadding(new Insets(4, 14, 4, 14));
                assignBtn.setOnAction(e -> {
                    detailAssignRow.setVisible(true);
                    detailAssignRow.setManaged(true);
                    detailAssignInput.clear();
                    detailAssignInput.requestFocus();
                    FadeTransition ft = new FadeTransition(Duration.millis(200), detailAssignRow);
                    ft.setFromValue(0);
                    ft.setToValue(1);
                    ft.play();
                });
                detailActionRow.getChildren().add(assignBtn);

            } else if (bug.isUnclaimed()) {
                Button claim = new Button("🔧  I'll fix it");
                claim.getStyleClass().add("btn-accept");
                claim.setPadding(new Insets(4, 14, 4, 14));
                claim.setOnAction(e -> viewModel.claimBug(bug));
                detailActionRow.getChildren().add(claim);

            } else if (!isFixer) {
                Label locked = new Label("🔒  @" + bug.getFixingUserId() + " is working on this");
                locked.setStyle("-fx-text-fill: #4A4060; -fx-font-size: 11px;" +
                        "-fx-font-style: italic; -fx-padding: 4 0 0 0;");
                detailActionRow.getChildren().add(locked);
            }
        }
    }

    @FXML
    private void closeDetail(ActionEvent e) {
        viewModel.closeDetail();   // ViewModel sets selectedBug = null → observer closes panel
    }

    @FXML
    private void confirmAssign(ActionEvent e) {
        Bug bug = viewModel.getSelectedBug();
        if (bug == null) return;
        String assignee = detailAssignInput.getText().trim();
        if (!assignee.isEmpty()) {
            viewModel.assignBug(bug, assignee);
        }
        detailAssignRow.setVisible(false);
        detailAssignRow.setManaged(false);
    }

    @FXML
    private void cancelAssign(ActionEvent e) {
        detailAssignRow.setVisible(false);
        detailAssignRow.setManaged(false);
    }


    private void staggerIn(VBox card, int delayMs) {
        card.setOpacity(0);
        card.setTranslateY(18);
        FadeTransition fade = new FadeTransition(Duration.millis(280), card);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.setDelay(Duration.millis(delayMs));
        TranslateTransition move = new TranslateTransition(Duration.millis(280), card);
        move.setFromY(18);
        move.setToY(0);
        move.setDelay(Duration.millis(delayMs));
        fade.play();
        move.play();
    }

    private void slideDetailIn() {
        TranslateTransition tt = new TranslateTransition(Duration.millis(350), bugDetailSlide);
        tt.setToX(0);
        tt.play();
    }

    private void slideDetailOut() {
        TranslateTransition tt = new TranslateTransition(Duration.millis(300), bugDetailSlide);
        tt.setToX(440);
        tt.play();
    }

    private void popDetail() {
        ScaleTransition pop = new ScaleTransition(Duration.millis(150), bugDetailSlide);
        pop.setFromX(0.97);
        pop.setFromY(0.97);
        pop.setToX(1.0);
        pop.setToY(1.0);
        pop.play();
    }

    private void pulseCard(VBox card) {
        ScaleTransition sc = new ScaleTransition(Duration.millis(120), card);
        sc.setFromX(1.0);
        sc.setToX(1.03);
        sc.setFromY(1.0);
        sc.setToY(1.03);
        sc.setAutoReverse(true);
        sc.setCycleCount(2);
        sc.play();
    }


    private String severityCardClass(BugSeverity s) {
        return switch (s) {
            case CRITICAL -> "bug-card-critical";
            case HIGH -> "bug-card-high";
            case MEDIUM -> "bug-card-medium";
            case LOW -> "bug-card-low";
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

    private String statusBadgeClass(BugStatus s) {
        return switch (s) {
            case OPEN -> "bug-status-open";
            case IN_PROGRESS -> "bug-status-progress";
            case CLOSED -> "bug-status-closed";
        };
    }

    private String truncate(String s, int max) {
        if (s == null) return "";
        return s.length() <= max ? s : s.substring(0, max) + "…";
    }

    private Label severityMini(String text, String styleClass) {
        Label l = new Label(text);
        l.getStyleClass().add(styleClass);
        l.setStyle("-fx-font-size: 10px; -fx-padding: 1 6 1 6;");
        return l;
    }

    private void removeScrollBars(ScrollPane sp) {
        if (sp == null) return;
        sp.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        sp.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
    }
}