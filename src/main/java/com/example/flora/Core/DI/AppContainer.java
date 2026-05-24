package com.example.flora.Core.DI;

import com.example.flora.Core.DataBase.DatabaseManager;
import com.example.flora.Features.Auth.ViewModel.AuthViewModel;
import com.example.flora.Features.Auth.repository.UserRepositoryImpl;
import com.example.flora.Features.Auth.service.AuthService;
import com.example.flora.Features.Bug.repository.BugRepositoryImpl;
import com.example.flora.Features.Bug.service.BugService;
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
import com.example.flora.Features.Task.repository.TaskRepositoryFake;
import com.example.flora.Features.Task.repository.TaskRepositoryImpl;
import com.example.flora.Features.Task.service.TaskServices;

import java.sql.SQLException;

public class AppContainer {
    private final DatabaseManager databaseManager;


    private final AuthService authService;
    private final AuthViewModel authViewModel;

    private final NotificationService notificationService;
    private final NotificationViewModel notificationViewModel;

    private final ProjectService projectService;
    private final ProjectViewModel projectViewModel;

    private final TaskServices taskServices;
    private final TaskViewModel taskViewModel;

    private final ProjectDetailViewModel projectDetailViewModel;

    private final BugServiceImpl bugServiceImpl;
    private final BugViewModel bugViewModel;

    private final SettingsService settingsService;
    private final SettingsViewModel settingsViewModel;

    //fake Zone
    //   private final TaskServices taskServicesFake;
    //    private final TaskViewModel taskViewModelFake;

    public AppContainer() throws SQLException {
        databaseManager = DatabaseManager.getDatabaseManager();

        UserRepositoryImpl userRepository = new UserRepositoryImpl(databaseManager.getConnection());
        authService = new AuthService(userRepository);
        authViewModel = new AuthViewModel(authService);

        NotificationRepositoryImpl notificationRepository = new NotificationRepositoryImpl(databaseManager.getConnection());
        notificationService = new NotificationService(notificationRepository);
        notificationViewModel = new NotificationViewModel(notificationService);

        ProjectRepositoryImpl projectRepository = new ProjectRepositoryImpl(databaseManager.getConnection());
        projectService = new ProjectService(projectRepository);
        projectViewModel = new ProjectViewModel(projectService);

        TaskRepositoryImpl taskRepository = new TaskRepositoryImpl(databaseManager.getConnection());
        taskServices = new TaskServices(taskRepository);
        taskViewModel = new TaskViewModel(taskServices);

        BugRepositoryImpl bugRepository = new BugRepositoryImpl(databaseManager.getConnection());
        bugServiceImpl = new BugServiceImpl(bugRepository);
        bugViewModel = new BugViewModel(bugServiceImpl); // need to work

        projectDetailViewModel = new ProjectDetailViewModel(taskViewModel, bugViewModel);

        SettingsRepositoryImpl settingsRepository = new SettingsRepositoryImpl(databaseManager.getConnection());
        settingsService = new SettingsService(settingsRepository);
        settingsViewModel = new SettingsViewModel(settingsService);

        //fake zone for testing
//     TaskRepositoryFake repositoryFake = new TaskRepositoryFake();
//     taskServicesFake = new TaskServices(repositoryFake);
//     taskViewModelFake = new TaskViewModel(taskServicesFake);


    }

    //getters

    public AuthViewModel getAuthViewModel() {
        return authViewModel;
    }

    public NotificationViewModel getNotificationViewModel() {
        return notificationViewModel;
    }

    public ProjectViewModel getProjectViewModel() {
        return projectViewModel;
    }

    public ProjectDetailViewModel getProjectDetailViewModel() {
        return projectDetailViewModel;
    }

    public TaskViewModel getTaskViewModel() {
        return taskViewModel;
    }

    public BugViewModel getBugViewModel() {
        return bugViewModel;
    }

    public SettingsViewModel getSettingsViewModel() {
        return settingsViewModel;
    }

    //  public TaskViewModel getTaskViewModelFake(){return taskViewModelFake;}
}
