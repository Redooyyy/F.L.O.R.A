package com.example.flora.Features.Bug.UI;

import com.example.flora.Features.Bug.model.Bug;
import com.example.flora.Features.Bug.model.BugSeverity;
import com.example.flora.Features.Bug.model.BugStatus;
import com.example.flora.Features.Home.UI.HomeUI_Controller;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

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

    // TODO: inject from session / DI
    private final String currentUser = "bushra";
    private final boolean isLeader = false;   // flip to true to test leader view

    private final Map<String, String> projectLeaders = Map.of(
            "HMS", "bushra",
            "Flora", "rafi",
            "EComm", "mehedi",
            "LMS", "bushra"
    );

    private final HomeUI_Controller homeController;
    private final List<Bug> allBugs = new ArrayList<>();

    private String activeSeverityFilter = "All";
    private String activeStatusFilter = "All";
    private String activeProject = null;
    private Bug selectedBug = null;
    private boolean detailOpen = false;

    public BugUI_Controller(HomeUI_Controller homeController) {
        this.homeController = homeController;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        removeScrollBars(leftScroll);
        removeScrollBars(bugScroll);
        loadDummyData();
        buildProjectSummaryPanel();
        applyFilters();
    }

    private void loadDummyData() {
        allBugs.addAll(List.of(
                new Bug("1", "HMS", "Login page crashes on empty password",
                        "If the user submits login with blank password the app throws NPE and freezes.",
                        BugSeverity.CRITICAL, BugStatus.OPEN, null, "bushra", "12 May 2025"),
                new Bug("2", "HMS", "Patient list pagination broken",
                        "After page 3 the table renders an empty set regardless of DB records.",
                        BugSeverity.HIGH, BugStatus.IN_PROGRESS, "rafi", "bushra", "13 May 2025"),
                new Bug("3", "HMS", "Date picker locale mismatch",
                        "Date picker shows MM/DD but backend expects DD/MM causing off-by-one errors.",
                        BugSeverity.MEDIUM, BugStatus.OPEN, null, "mehedi", "15 May 2025"),
                new Bug("4", "HMS", "Export PDF button invisible on dark theme",
                        "The button text colour matches background in dark mode.",
                        BugSeverity.LOW, BugStatus.CLOSED, "current_user", "mehedi", "10 May 2025"),
                new Bug("5", "Flora", "Notification panel overlaps sidebar",
                        "When sidebar is expanded the notification pane is partially hidden behind it.",
                        BugSeverity.HIGH, BugStatus.OPEN, null, "rafi", "16 May 2025"),
                new Bug("6", "Flora", "Task card scroll jitters on fast scroll",
                        "Rapid scrolling in the task list causes a layout jitter every ~200ms.",
                        BugSeverity.MEDIUM, BugStatus.IN_PROGRESS, "current_user", "rafi", "17 May 2025"),
                new Bug("7", "EComm", "Product images fail to load on slow connection",
                        "No loading placeholder shown; images show broken icon on first visit.",
                        BugSeverity.CRITICAL, BugStatus.OPEN, null, "rafi", "18 May 2025"),
                new Bug("8", "EComm", "Cart total rounding error",
                        "Total rounds down at exactly x.005 causing 1-cent discrepancies.",
                        BugSeverity.HIGH, BugStatus.OPEN, null, "mehedi", "19 May 2025"),
                new Bug("9", "EComm", "Search bar loses focus on mobile keyboard open",
                        "Keyboard open event triggers scroll that blurs the search input.",
                        BugSeverity.LOW, BugStatus.CLOSED, "rafi", "bushra", "14 May 2025"),
                new Bug("10", "LMS", "Quiz timer continues after submission",
                        "Timer keeps ticking and eventually shows negative seconds after quiz submit.",
                        BugSeverity.CRITICAL, BugStatus.IN_PROGRESS, "current_user", "bushra", "20 May 2025")
        ));
    }


    private void buildProjectSummaryPanel() {
        projectBugSummaryScroll.getChildren()
                .removeIf(n -> !(n instanceof Label l && l.getStyleClass().contains("left-panel-header")));

        projectBugSummaryScroll.getChildren().add(buildProjectCard(null, allBugs));

        allBugs.stream()
                .map(Bug::getProjectName).distinct().sorted()
                .forEach(p -> {
                    List<Bug> pb = allBugs.stream()
                            .filter(b -> b.getProjectName().equals(p))
                            .collect(Collectors.toList());
                    projectBugSummaryScroll.getChildren().add(buildProjectCard(p, pb));
                });
    }


    private VBox buildProjectCard(String projectName, List<Bug> bugs) {
        VBox card = new VBox(6);
        boolean isAll = (projectName == null);
        card.getStyleClass().add(isAll && activeProject == null
                ? "proj-summary-card-active" : "proj-summary-card");

        HBox topRow = new HBox(8);
        topRow.setAlignment(Pos.CENTER_LEFT);

        Label nameLabel = new Label(isAll ? "📂  All Projects" : "📁  " + projectName);
        nameLabel.getStyleClass().add("proj-name-label");

        long open = bugs.stream().filter(b -> b.getStatus() != BugStatus.CLOSED).count();
        Label countLabel = new Label(open + " open");
        countLabel.getStyleClass().add("proj-bug-count-label");

        Region spacer = new Region();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);
        topRow.getChildren().addAll(nameLabel, spacer, countLabel);

        if (!isAll) {
            String leader = projectLeaders.getOrDefault(projectName, "—");
            HBox leaderRow = new HBox(5);
            leaderRow.setAlignment(Pos.CENTER_LEFT);
            Label leaderKey = new Label("Leader:");
            leaderKey.setStyle("-fx-text-fill: #4A4060; -fx-font-size: 10px;");
            Label leaderVal = new Label("@" + leader);
            leaderVal.setStyle("-fx-text-fill: #A855F7; -fx-font-size: 10px; -fx-font-weight: bold;");
            leaderRow.getChildren().addAll(leaderKey, leaderVal);
            card.getChildren().addAll(topRow, leaderRow);
        } else {
            card.getChildren().add(topRow);
        }

        HBox barRow = new HBox(4);
        barRow.setAlignment(Pos.CENTER_LEFT);
        barRow.setPadding(new Insets(2, 0, 0, 0));

        long crit = bugs.stream().filter(b -> b.getSeverity() == BugSeverity.CRITICAL && b.getStatus() != BugStatus.CLOSED).count();
        long high = bugs.stream().filter(b -> b.getSeverity() == BugSeverity.HIGH && b.getStatus() != BugStatus.CLOSED).count();
        long med = bugs.stream().filter(b -> b.getSeverity() == BugSeverity.MEDIUM && b.getStatus() != BugStatus.CLOSED).count();
        long low = bugs.stream().filter(b -> b.getSeverity() == BugSeverity.LOW && b.getStatus() != BugStatus.CLOSED).count();

        if (crit > 0) barRow.getChildren().add(severityMini("🔴 " + crit + " Crit", "proj-bug-count-label"));
        if (high > 0) barRow.getChildren().add(severityMini("🟠 " + high, "severity-high"));
        if (med > 0) barRow.getChildren().add(severityMini("🟡 " + med, "severity-medium"));
        if (low > 0) barRow.getChildren().add(severityMini("🟢 " + low, "severity-low"));

        card.getChildren().add(barRow);

        card.setOnMouseClicked(e -> {
            activeProject = projectName;
            activeSectionLabel.setText(isAll ? "All Bugs" : projectName + "  Bugs");
            applyFilters();
            refreshProjectCardStyles();
            pulseCard(card);
        });

        return card;
    }

    private Label severityMini(String text, String styleClass) {
        Label l = new Label(text);
        l.getStyleClass().add(styleClass);
        l.setStyle("-fx-font-size: 10px; -fx-padding: 1 6 1 6;");
        return l;
    }

    private void refreshProjectCardStyles() {
        projectBugSummaryScroll.getChildren().forEach(n -> {
            if (n instanceof VBox card) {
                card.getStyleClass().removeAll("proj-summary-card", "proj-summary-card-active");
                // First child of the card is always the topRow HBox
                String text = ((Label) ((HBox) card.getChildren().get(0)).getChildren().get(0)).getText();
                boolean isThis = (activeProject == null && text.contains("All"))
                        || (activeProject != null && text.contains(activeProject));
                card.getStyleClass().add(isThis ? "proj-summary-card-active" : "proj-summary-card");
            }
        });
    }

    @FXML
    private void filterAll() {
        activeSeverityFilter = "All";
        severityFilter.setText("Severity ▾");
        applyFilters();
    }

    @FXML
    private void filterCritical() {
        activeSeverityFilter = "CRITICAL";
        severityFilter.setText("🔴 Critical");
        applyFilters();
    }

    @FXML
    private void filterHigh() {
        activeSeverityFilter = "HIGH";
        severityFilter.setText("🟠 High");
        applyFilters();
    }

    @FXML
    private void filterMedium() {
        activeSeverityFilter = "MEDIUM";
        severityFilter.setText("🟡 Medium");
        applyFilters();
    }

    @FXML
    private void filterLow() {
        activeSeverityFilter = "LOW";
        severityFilter.setText("🟢 Low");
        applyFilters();
    }

    @FXML
    private void statusAll() {
        activeStatusFilter = "All";
        setActiveStatusBtn(btnStatusAll);
        applyFilters();
    }

    @FXML
    private void statusOpen() {
        activeStatusFilter = "OPEN";
        setActiveStatusBtn(btnStatusOpen);
        applyFilters();
    }

    @FXML
    private void statusClaimed() {
        activeStatusFilter = "IN_PROGRESS";
        setActiveStatusBtn(btnStatusClaimed);
        applyFilters();
    }

    @FXML
    private void statusClosed() {
        activeStatusFilter = "CLOSED";
        setActiveStatusBtn(btnStatusClosed);
        applyFilters();
    }

    private void setActiveStatusBtn(Button active) {
        for (Button b : new Button[]{btnStatusAll, btnStatusOpen, btnStatusClaimed, btnStatusClosed}) {
            b.getStyleClass().removeAll("status-filter-btn", "status-filter-btn-active");
            b.getStyleClass().add(b == active ? "status-filter-btn-active" : "status-filter-btn");
        }
    }

    private void applyFilters() {
        List<Bug> filtered = allBugs.stream()
                .filter(b -> activeProject == null || b.getProjectName().equals(activeProject))
                .filter(b -> activeSeverityFilter.equals("All") || b.getSeverity().name().equals(activeSeverityFilter))
                .filter(b -> activeStatusFilter.equals("All") || b.getStatus().name().equals(activeStatusFilter))
                .collect(Collectors.toList());

        bugCountChip.setText(String.valueOf(filtered.size()));
        renderBugCards(filtered);
    }

    private void renderBugCards(List<Bug> bugs) {
        bugCardList.getChildren().clear();
        if (bugs.isEmpty()) {
            Label empty = new Label("✅  No bugs matching the current filter.");
            empty.setStyle("-fx-text-fill: #4A4060; -fx-font-size:13px; -fx-font-style:italic; -fx-padding: 20 0 0 0;");
            bugCardList.getChildren().add(empty);
            return;
        }
        int delay = 0;
        for (Bug bug : bugs) {
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

        Region spacer1 = new Region();
        HBox.setHgrow(spacer1, javafx.scene.layout.Priority.ALWAYS);

        Label sevBadge = new Label(bug.getSeverity().displayName());
        sevBadge.getStyleClass().add(severityBadgeClass(bug.getSeverity()));

        row1.getChildren().addAll(title, spacer1, sevBadge);

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

        Region spacer3 = new Region();
        HBox.setHgrow(spacer3, javafx.scene.layout.Priority.ALWAYS);

        Label projectChip = new Label("📁 " + bug.getProjectName());
        projectChip.getStyleClass().add("bug-card-meta");

        row3.getChildren().addAll(spacer3, projectChip);
        card.getChildren().addAll(row1, desc, row3);

        String leader = projectLeaders.getOrDefault(bug.getProjectName(), "—");
        HBox row4 = new HBox(10);
        row4.setAlignment(Pos.CENTER_LEFT);

        Label reporterLabel = new Label("👤 Reported by @" + bug.getReportedByUserId());
        reporterLabel.getStyleClass().add("bug-card-meta");

        Region spacer4 = new Region();
        HBox.setHgrow(spacer4, javafx.scene.layout.Priority.ALWAYS);

        Label leaderLabel = new Label("👑 @" + leader);
        leaderLabel.getStyleClass().add("leader-chip");

        Label dateLabel = new Label("📅 " + bug.getReportedDate());
        dateLabel.getStyleClass().add("bug-card-meta");

        row4.getChildren().addAll(reporterLabel, spacer4, leaderLabel, dateLabel);
        card.getChildren().add(row4);

        card.setOnMouseClicked(e -> openDetail(bug));
        return card;
    }


    private void openDetail(Bug bug) {
        selectedBug = bug;

        String projectLeader = projectLeaders.getOrDefault(bug.getProjectName(), "");
        boolean isProjectLeader = isLeader || currentUser.equals(projectLeader);

        //debug
        System.out.println(projectLeader);
        System.out.println(currentUser);

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

        boolean isFixer = !bug.isUnclaimed() && currentUser.equals(bug.getFixingUserId());

        if (!bug.isClosed()) {

            if (isFixer) {
                Button markFixed = new Button("✅  Mark as Fixed");
                markFixed.getStyleClass().add("btn-accept");
                markFixed.setPadding(new Insets(4, 14, 4, 14));
                markFixed.setOnAction(e -> {
                    bug.setStatus(BugStatus.CLOSED);
                    refreshAfterChange();
                });
                detailActionRow.getChildren().add(markFixed);

                Button toggleStatus = new Button(
                        bug.getStatus() == BugStatus.IN_PROGRESS ? "⏸  Pause" : "▶  Resume");
                toggleStatus.getStyleClass().add("update-btn");
                toggleStatus.setPadding(new Insets(4, 14, 4, 14));
                toggleStatus.setOnAction(e -> {
                    bug.setStatus(bug.getStatus() == BugStatus.IN_PROGRESS
                            ? BugStatus.OPEN : BugStatus.IN_PROGRESS);
                    refreshAfterChange();
                });
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

            } else if (!isProjectLeader && bug.isUnclaimed()) {
                Button claim = new Button("🔧  I'll fix it");
                claim.getStyleClass().add("btn-accept");
                claim.setPadding(new Insets(4, 14, 4, 14));
                claim.setOnAction(e -> {
                    bug.setFixingUserId(currentUser);
                    bug.setStatus(BugStatus.IN_PROGRESS);
                    refreshAfterChange();
                });
                detailActionRow.getChildren().add(claim);

            } else if (!isProjectLeader && !bug.isUnclaimed() && !isFixer) {
                Label locked = new Label("🔒  @" + bug.getFixingUserId() + " is working on this");
                locked.setStyle(
                        "-fx-text-fill: #4A4060; -fx-font-size: 11px;" +
                                "-fx-font-style: italic; -fx-padding: 4 0 0 0;"
                );
                detailActionRow.getChildren().add(locked);
            }
        }

        if (!detailOpen) {
            slideDetailIn();
            detailOpen = true;
        } else {
            ScaleTransition pop = new ScaleTransition(Duration.millis(150), bugDetailSlide);
            pop.setFromX(0.97);
            pop.setFromY(0.97);
            pop.setToX(1.0);
            pop.setToY(1.0);
            pop.play();
        }
    }

    @FXML
    private void closeDetail(ActionEvent e) {
        if (detailOpen) {
            slideDetailOut();
            detailOpen = false;
        }
    }

    @FXML
    private void confirmAssign(ActionEvent e) {
        if (selectedBug == null) return;
        String assignee = detailAssignInput.getText().trim();
        if (!assignee.isEmpty()) {
            selectedBug.setFixingUserId(assignee);
            selectedBug.setStatus(BugStatus.IN_PROGRESS);
            refreshAfterChange();
        }
        detailAssignRow.setVisible(false);
        detailAssignRow.setManaged(false);
    }

    @FXML
    private void cancelAssign(ActionEvent e) {
        detailAssignRow.setVisible(false);
        detailAssignRow.setManaged(false);
    }

    private void refreshAfterChange() {
        if (selectedBug != null) openDetail(selectedBug);
        applyFilters();
        buildProjectSummaryPanel();
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

    private void removeScrollBars(ScrollPane sp) {
        if (sp == null) return;
        sp.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        sp.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
    }
}