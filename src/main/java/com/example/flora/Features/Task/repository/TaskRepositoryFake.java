package com.example.flora.Features.Task.repository;

import com.example.flora.Features.Task.model.Task;
import com.example.flora.Features.Task.model.TaskStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TaskRepositoryFake implements TaskRepository {

    public TaskRepositoryFake(){
        System.out.println("Fake repo called");
    }

    private final List<Task> store = new ArrayList<>(List.of(
            new Task("1", "Design login screen", "Create wireframes", TaskStatus.TODO,
                    "proj-1", "bushra", "27 May 2026", "20 May 2026"),
            new Task("2", "Implement API", "REST endpoints", TaskStatus.IN_PROGRESS,
                    "proj-1", "farhan", "30 May 2026", "20 May 2026"),
            new Task("3", "Write tests Reo", null, TaskStatus.DONE,
                    "proj-1", "reo", "02 Jun 2026", "20 May 2026"),
            new Task("4", "Draft: DB schema", null, TaskStatus.TODO,
                    "proj-1", null, null, "20 May 2026")  // draft
    ));

    @Override public void save(Task t)   { store.add(t); }
    @Override public void update(Task t) {
        store.replaceAll(existing -> existing.getId().equals(t.getId()) ? t : existing);
    }


    @Override public void delete(String id)         { store.removeIf(t -> t.getId().equals(id)); }
    @Override public Optional<Task> findById(String id) {
        return store.stream().filter(t -> t.getId().equals(id)).findFirst();
    }
    @Override public List<Task> findByProjectId(String projectId) {
        return store.stream().filter(t -> projectId.equals(t.getProjectId())).toList();
    }
    @Override public List<Task> findByAssignee(String assigneeId) {
        return store.stream().filter(t -> assigneeId.equals(t.getAssigneeId())).toList();
    }

    @Override public List<Task> findByStatus(String projectId, TaskStatus status) {
        return store.stream()
                .filter(t -> projectId.equals(t.getProjectId()) && status == t.getStatus()).toList();
    }
    @Override public List<Task> findAll() { return List.copyOf(store); }
}