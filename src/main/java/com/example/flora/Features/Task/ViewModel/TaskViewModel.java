package com.example.flora.Features.Task.ViewModel;

import com.example.flora.Features.Task.model.Task;
import com.example.flora.Features.Task.service.TaskServices;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class TaskViewModel {
    private final TaskServices taskServices;
    private ObservableList<Task>tasks = FXCollections.observableArrayList();
    private StringProperty title = new SimpleStringProperty("");
    private  StringProperty description = new SimpleStringProperty("");
    private  StringProperty dueDate = new SimpleStringProperty("");

    private String currProjectID;

    public TaskViewModel(TaskServices taskServices){
        this.taskServices = taskServices;
    }

    public void setCurrProjectID(String currProjectID) {
        this.currProjectID = currProjectID;
    }

    public void loadTask(){
        //TODO: get task via user id
        tasks.setAll(taskServices.getAllTasks());
    }

    public void createTask(){
        taskServices.createTask(title.get(),description.get(),currProjectID,dueDate.get());
        loadTask();
    }


    public ObservableList<Task> getTasks() {
        return tasks;
    }

    public StringProperty titleProperty() {
        return title;
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    public StringProperty dueDateProperty() {
        return dueDate;
    }

}
