package com.example.flora.Features.Task.UI;

import com.example.flora.Features.Task.ViewModel.TaskViewModel;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class TaskUI_Controller {
    private final TaskViewModel taskViewModel;
    public Label projectNameInViewBox;
    public VBox projectCardScroll;
    public VBox TaskCardScroll;

    public TaskUI_Controller(TaskViewModel taskViewModel){
        this.taskViewModel = taskViewModel;
    }
}
