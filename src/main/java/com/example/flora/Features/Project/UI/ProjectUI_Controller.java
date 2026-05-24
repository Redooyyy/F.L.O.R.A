package com.example.flora.Features.Project.UI;

import com.example.flora.Features.Project.UI.Card.ProjectCard_Controller;
import com.example.flora.Features.Project.ViewModel.ProjectDetailViewModel;
import com.example.flora.Features.Project.ViewModel.ProjectViewModel;
import com.example.flora.Features.Project.model.Project;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ProjectUI_Controller implements Initializable {


    @FXML private Text nickName;
    @FXML private Text totalProject;
    @FXML private Text completedProject;
    @FXML private Text inProgressProject;
    @FXML private Text onholdProject;
    @FXML private Text outOfScheduleProject;


    @FXML private ScrollPane projectCardView;
    @FXML private GridPane   projectGrid;


    @FXML private AnchorPane rootPane; // fx:id="rootPane" — add this to ProjectUI.fxml root

    private final ProjectViewModel projectViewModel;
    private final ProjectDetailViewModel projectDetailViewModel;

    private ProjectDetailUI_Controller detailController;


    private String currentUserId = "bushra"; // replace with real injection

    public ProjectUI_Controller(ProjectViewModel projectViewModel, ProjectDetailViewModel projectDetailViewModel) {
        this.projectViewModel = projectViewModel;
        this.projectDetailViewModel = projectDetailViewModel;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setNickname("Bushra");
        removeScrollBar(projectCardView);

        try {
            loadDetailPanel();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load ProjectDetailUI.fxml", e);
        }

        try {
            loadProjectCards();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private void loadDetailPanel() throws IOException {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/Project/UI/ProjectDetailUI.fxml")
        );
        loader.setControllerFactory(type -> new ProjectDetailUI_Controller(projectDetailViewModel));
        AnchorPane detailPanel = loader.load();
        detailController = loader.getController();

        AnchorPane.setTopAnchor(detailPanel, 0.0);
        AnchorPane.setBottomAnchor(detailPanel, 0.0);
        AnchorPane.setLeftAnchor(detailPanel, 0.0);
        AnchorPane.setRightAnchor(detailPanel, 0.0);

        rootPane.getChildren().add(detailPanel);
    }

    // ── Load cards ────────────────────────────────────────────────────────────

    private void loadProjectCards() throws IOException {
        projectGrid.getChildren().clear();

        // TODO: replace with real data from viewModel
         List<Project> projects = projectViewModel.getProjects();
         projectViewModel.loadProject();
        // List<Project> projects = sampleProjects(); // placeholder

        for (int i = 0; i < projects.size(); i++) {
            int col = i % 3;
            int row = i / 3;
            AnchorPane card = buildCard(projects.get(i));
            projectGrid.add(card, col, row);
        }

        projectGrid.setHgap(10);
        projectGrid.setVgap(10);
        projectGrid.setPadding(new Insets(10));
    }

    private AnchorPane buildCard(Project project) throws IOException {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/Project/UI/Card/ProjectCard.fxml")
        );
        AnchorPane card = loader.load();
        card.setMaxWidth(Double.MAX_VALUE);
        GridPane.setHgrow(card, javafx.scene.layout.Priority.ALWAYS);

        ProjectCard_Controller cardController = loader.getController();
        cardController.setData(project, currentUserId, detailController);

        return card;
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    public void setNickname(String name) {
        nickName.setText(name);
    }

    public void setCurrentUserId(String userId) {
        this.currentUserId = userId;
    }

    void removeScrollBar(ScrollPane scrollPane) {
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
    }

    // ── Placeholder data (remove when real VM is wired) ───────────────────────
    private List<Project> sampleProjects() {
        return List.of(
                new Project("1", "Hospital Management System", "Core hospital ops", "bushra", "2025-01-10"),
                new Project("2", "Flora Task Manager",          "This app!",          "farhan", "2025-03-01"),
                new Project("3", "E-Commerce Platform",         "Online store",       "bushra", "2025-04-15"),
                new Project("4", "Chat Application",            "Real-time chat",     "reo",    "2025-05-01")
        );
    }
}
