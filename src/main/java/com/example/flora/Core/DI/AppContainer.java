package com.example.flora.Core.DI;

import com.example.flora.Core.DataBase.DatabaseManager;
import com.example.flora.Core.session.UserSession;
import com.example.flora.Features.Auth.ViewModel.AuthViewModel;
import com.example.flora.Features.Auth.model.User;
import com.example.flora.Features.Auth.repository.UserRepositoryImpl;
import com.example.flora.Features.Auth.service.AuthService;
import com.example.flora.Features.Bug.repository.BugRepositoryImpl;
import com.example.flora.Features.Bug.service.BugServiceImpl;
import com.example.flora.Features.Bug.viewmodel.BugViewModel;
import com.example.flora.Features.Home.ViewModel.NotificationViewModel;
import com.example.flora.Features.Home.repository.NotificationRepositoryImpl;
import com.example.flora.Features.Home.services.NotificationService;
import com.example.flora.Features.Project.ViewModel.ProjectDetailViewModel;
import com.example.flora.Features.Project.ViewModel.ProjectViewModel;
import com.example.flora.Features.Project.repository.ProjectRepositoryImpl;
import com.example.flora.Features.Project.service.ProjectService;
import com.example.flora.Features.Settings.repository.SettingsRepositoryImpl;
import com.example.flora.Features.Settings.service.SettingsService;
import com.example.flora.Features.Settings.viewmodel.SettingsViewModel;
import com.example.flora.Features.Task.ViewModel.TaskViewModel;
import com.example.flora.Features.Task.repository.TaskRepositoryImpl;
import com.example.flora.Features.Task.service.TaskServices;

import java.sql.Connection;
import java.sql.SQLException;

public class AppContainer {
    private final DatabaseManager databaseManager;
    private final Connection connection;       // shared for all repositories

    private final AuthViewModel authViewModel;

    private NotificationViewModel notificationViewModel;
    private ProjectViewModel projectViewModel;
    private TaskViewModel taskViewModel;
    private BugViewModel bugViewModel;
    private ProjectDetailViewModel projectDetailViewModel;
    private SettingsViewModel settingsViewModel;


    public AppContainer() throws SQLException {
        databaseManager = DatabaseManager.getDatabaseManager();
        connection = databaseManager.getConnection();  // one shared connection

        UserRepositoryImpl userRepository = new UserRepositoryImpl(connection);

        AuthService authService = new AuthService(userRepository);

        authViewModel = new AuthViewModel(authService);
    }


    public void initUserSession() {
        User user = UserSession.getUser();
        if (user == null) {
            throw new IllegalStateException("initUserSession() called but no user in session.");
        }
        Integer userId = user.getId();
        if (userId == null) {
            throw new IllegalStateException("Logged-in user has no ID — cannot init session.");
        }
        String userIdStr = userId.toString();


        NotificationRepositoryImpl notificationRepository = new NotificationRepositoryImpl(connection);
        ProjectRepositoryImpl projectRepository = new ProjectRepositoryImpl(connection);
        TaskRepositoryImpl taskRepository = new TaskRepositoryImpl(connection);
        BugRepositoryImpl bugRepository = new BugRepositoryImpl(connection);
        SettingsRepositoryImpl settingsRepository = new SettingsRepositoryImpl(connection);


        NotificationService notificationService = new NotificationService(notificationRepository);
        ProjectService projectService = new ProjectService(projectRepository);
        TaskServices taskServices = new TaskServices(taskRepository);
        BugServiceImpl bugService = new BugServiceImpl(bugRepository);
        SettingsService settingsService = new SettingsService(settingsRepository);


        notificationViewModel = new NotificationViewModel(notificationService, userId);
        projectViewModel = new ProjectViewModel(projectService, userIdStr);
        taskViewModel = new TaskViewModel(taskServices, userIdStr);
        bugViewModel = new BugViewModel(bugService, userIdStr);
        projectDetailViewModel = new ProjectDetailViewModel(taskViewModel, bugViewModel,userIdStr);
        settingsViewModel = new SettingsViewModel(settingsService, userIdStr, user.getEmail());
    }


    public AuthViewModel getAuthViewModel() {
        return authViewModel;
    }

    public NotificationViewModel getNotificationViewModel() {
        requireSession();
        return notificationViewModel;
    }

    public ProjectViewModel getProjectViewModel() {
        requireSession();
        return projectViewModel;
    }

    public TaskViewModel getTaskViewModel() {
        requireSession();
        return taskViewModel;
    }

    public BugViewModel getBugViewModel() {
        requireSession();
        return bugViewModel;
    }

    public ProjectDetailViewModel getProjectDetailViewModel() {
        requireSession();
        return projectDetailViewModel;
    }

    public SettingsViewModel getSettingsViewModel() {
        requireSession();
        return settingsViewModel;
    }


    public void clearUserSession() {
        notificationViewModel = null;
        projectViewModel = null;
        taskViewModel = null;
        bugViewModel = null;
        projectDetailViewModel = null;
        settingsViewModel = null;
        UserSession.clear();
    }

    private void requireSession() {
        if (settingsViewModel == null) {
            throw new IllegalStateException(
                    "User-scoped ViewModels are not initialized. Call initUserSession() after login.");
        }
    }
}