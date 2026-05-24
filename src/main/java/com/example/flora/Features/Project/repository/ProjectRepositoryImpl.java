package com.example.flora.Features.Project.repository;

import com.example.flora.Features.Project.model.Project;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ProjectRepositoryImpl implements ProjectRepository {

    private final Connection connection;

    public ProjectRepositoryImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void save(Project project) {
        String sql = "INSERT INTO projects (id, name, description, owner_id, created_at, devices, techs) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, project.getId());
            ps.setString(2, project.getName());
            ps.setString(3, project.getDescription());
            ps.setString(4, project.getOwnerId());
            ps.setString(5, project.getCreatedAt());
            ps.setString(6, joinList(project.getDevices()));   // NEW
            ps.setString(7, joinList(project.getTechs()));     // NEW
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save project: " + e.getMessage(), e);
        }
    }

    @Override
    public void update(Project project) {
        String sql = """
                UPDATE projects
                SET name = ?, description = ?, owner_id = ?, devices = ?, techs = ?
                WHERE id = ?
                """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, project.getName());
            ps.setString(2, project.getDescription());
            ps.setString(3, project.getOwnerId());
            ps.setString(4, joinList(project.getDevices()));   // NEW
            ps.setString(5, joinList(project.getTechs()));     // NEW
            ps.setString(6, project.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update project: " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(String id) {
        String sql = "DELETE FROM projects WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete project: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Project> findById(String id) {
        String sql = "SELECT * FROM projects WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Optional.of(mapRow(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find project by id: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public List<Project> findAll() {
        String sql = "SELECT * FROM projects ORDER BY created_at DESC";
        List<Project> projects = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) projects.add(mapRow(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find all projects: " + e.getMessage(), e);
        }
        return projects;
    }

    @Override
    public List<Project> findByOwnerId(String ownerId) {
        String sql = "SELECT * FROM projects WHERE owner_id = ? ORDER BY created_at DESC";
        List<Project> projects = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, ownerId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) projects.add(mapRow(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find projects by owner: " + e.getMessage(), e);
        }
        return projects;
    }


    private Project mapRow(ResultSet rs) throws SQLException {
        List<String> devices = splitList(safeGetString(rs, "devices"));
        List<String> techs = splitList(safeGetString(rs, "techs"));

        return new Project(
                rs.getString("id"),
                rs.getString("name"),
                rs.getString("description"),
                rs.getString("owner_id"),
                rs.getString("created_at"),
                devices,
                techs
        );
    }

    private String joinList(List<String> list) {
        if (list == null || list.isEmpty()) return "";
        return list.stream()
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.joining(","));
    }

    private List<String> splitList(String raw) {
        if (raw == null || raw.isBlank()) return new ArrayList<>();
        return Arrays.stream(raw.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private String safeGetString(ResultSet rs, String col) {
        try {
            return rs.getString(col);
        } catch (SQLException e) {
            return "";
        }
    }
}