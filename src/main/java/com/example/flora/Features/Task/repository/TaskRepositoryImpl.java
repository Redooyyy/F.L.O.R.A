package com.example.flora.Features.Task.repository;

import com.example.flora.Features.Task.model.Task;
import com.example.flora.Features.Task.model.TaskStatus;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * TaskRepositoryImpl — JDBC implementation.
 * <p>
 * Expected table schema:
 * ┌─────────────────────────────────────────────────────────┐
 * │  CREATE TABLE tasks (                                   │
 * │    id          TEXT PRIMARY KEY,                        │
 * │    title       TEXT        NOT NULL,                    │
 * │    description TEXT,                                    │
 * │    status      TEXT        NOT NULL,                    │
 * │    project_id  TEXT        NOT NULL,                    │
 * │    assignee_id TEXT,                                    │
 * │    due_date    TEXT,                                    │
 * │    created_at  TEXT        NOT NULL                     │
 * │  );                                                     │
 * └─────────────────────────────────────────────────────────┘
 */
public class TaskRepositoryImpl implements TaskRepository {

    private final Connection connection;

    public TaskRepositoryImpl(Connection connection) {
        this.connection = connection;
    }


    @Override
    public void save(Task task) {
        String sql = """
                INSERT INTO tasks (id, title, description, status,
                                   project_id, assignee_id, due_date, created_at)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, task.getId());
            ps.setString(2, task.getTitle());
            ps.setString(3, task.getDescription());
            ps.setString(4, task.getStatus().name());
            ps.setString(5, task.getProjectId());
            ps.setString(6, task.getAssigneeId());   // nullable
            ps.setString(7, task.getDueDate());      // nullable
            ps.setString(8, task.getCreatedAt());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save task: " + task.getId(), e);
        }
    }


    @Override
    public void update(Task task) {
        String sql = """
                UPDATE tasks
                SET title       = ?,
                    description = ?,
                    status      = ?,
                    project_id  = ?,
                    assignee_id = ?,
                    due_date    = ?
                WHERE id = ?
                """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, task.getTitle());
            ps.setString(2, task.getDescription());
            ps.setString(3, task.getStatus().name());
            ps.setString(4, task.getProjectId());
            ps.setString(5, task.getAssigneeId());
            ps.setString(6, task.getDueDate());
            ps.setString(7, task.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update task: " + task.getId(), e);
        }
    }


    @Override
    public void delete(String id) {
        String sql = "DELETE FROM tasks WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete task: " + id, e);
        }
    }


    @Override
    public Optional<Task> findById(String id) {
        String sql = "SELECT * FROM tasks WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Optional.of(map(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find task by id: " + id, e);
        }
        return Optional.empty();
    }

    @Override
    public List<Task> findByProjectId(String projectId) {
        String sql = "SELECT * FROM tasks WHERE project_id = ? ORDER BY created_at DESC";
        return queryList(sql, projectId);
    }

    @Override
    public List<Task> findByAssignee(String assigneeId) {
        String sql = "SELECT * FROM tasks WHERE assignee_id = ? ORDER BY created_at DESC";
        return queryList(sql, assigneeId);
    }

    @Override
    public List<Task> findByStatus(String projectId, TaskStatus status) {
        String sql = "SELECT * FROM tasks WHERE project_id = ? AND status = ? ORDER BY created_at DESC";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, projectId);
            ps.setString(2, status.name());
            return mapAll(ps.executeQuery());
        } catch (SQLException e) {
            throw new RuntimeException("Failed to findByStatus", e);
        }
    }

    @Override
    public List<Task> findAll() {
        String sql = "SELECT * FROM tasks ORDER BY created_at DESC";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            return mapAll(ps.executeQuery());
        } catch (SQLException e) {
            throw new RuntimeException("Failed to findAll tasks", e);
        }
    }


    private List<Task> queryList(String sql, String param) {
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, param);
            return mapAll(ps.executeQuery());
        } catch (SQLException e) {
            throw new RuntimeException("Query failed: " + sql, e);
        }
    }

    private List<Task> mapAll(ResultSet rs) throws SQLException {
        List<Task> list = new ArrayList<>();
        while (rs.next()) list.add(map(rs));
        return list;
    }

    private Task map(ResultSet rs) throws SQLException {
        Task task = new Task();
        task.setId(rs.getString("id"));
        task.setTitle(rs.getString("title"));
        task.setDescription(rs.getString("description"));
        task.setStatus(TaskStatus.valueOf(rs.getString("status")));
        task.setProjectId(rs.getString("project_id"));
        task.setAssigneeId(rs.getString("assignee_id"));
        task.setDueDate(rs.getString("due_date"));
        task.setCreatedAt(rs.getString("created_at"));
        return task;
    }
}