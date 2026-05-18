package com.example.flora.Core.DI;

import com.example.flora.Core.DataBase.DatabaseManager;
import com.example.flora.Features.Auth.ViewModel.AuthViewModel;
import com.example.flora.Features.Auth.repository.UserRepositoryImpl;
import com.example.flora.Features.Auth.service.AuthService;
import com.example.flora.Features.Home.ViewModel.NotificationViewModel;
import com.example.flora.Features.Home.repository.NotificationRepositoryImpl;
import com.example.flora.Features.Home.services.NotificationService;
import com.example.flora.Features.Project.ViewModel.ProjectViewModel;
import com.example.flora.Features.Project.repository.ProjectRepositoryImpl;
import com.example.flora.Features.Project.service.ProjectService;
import com.example.flora.Features.Task.ViewModel.TaskViewModel;
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

 private  final TaskServices taskServices;
 private final TaskViewModel taskViewModel;

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


 }

 //getters

    public AuthViewModel getAuthViewModel() {
        return authViewModel;
    }

    public NotificationViewModel getNotificationViewModel() {
        return notificationViewModel;
    }

    public ProjectViewModel getProjectViewModel(){
     return projectViewModel;
    }

    public TaskViewModel getTaskViewModel() {
        return taskViewModel;
    }
}
