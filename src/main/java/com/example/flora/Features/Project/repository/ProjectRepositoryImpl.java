package com.example.flora.Features.Project.repository;

import com.example.flora.Features.Project.model.Project;
import com.example.flora.Features.Project.model.ProjectMembership;
import com.example.flora.Features.Project.model.ProjectRole;

import java.sql.*;
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
        String sql = "INSERT INTO projects (name, description, owner_id, created_at, devices, techs) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, project.getName());
            ps.setString(2, project.getDescription());
            ps.setString(3, project.getOwnerId());
            ps.setString(4, project.getCreatedAt());
            ps.setString(5, joinList(project.getDevices()));
            ps.setString(6, joinList(project.getTechs()));
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if(rs.next()) project.setId(Integer.toString(rs.getInt(1)));
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
            ps.setString(4, joinList(project.getDevices()));
            ps.setString(5, joinList(project.getTechs()));
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


    @Override
    public List<ProjectMembership> findByUserId(String userId) {
        String sql = """
                SELECT p.*, pm.role
                FROM projects p
                JOIN project_members pm ON pm.project_id = p.id
                WHERE pm.user_id = CAST(? AS UNSIGNED)
                ORDER BY p.created_at DESC
                """;
        return queryMemberships(sql, userId);
    }

    @Override
    public List<ProjectMembership> findByUserIdAndRole(String userId, ProjectRole role) {
        String sql = """
                SELECT p.*, pm.role
                FROM projects p
                JOIN project_members pm ON pm.project_id = p.id
                WHERE pm.user_id = CAST(? AS UNSIGNED)
                  AND pm.role    = ?
                ORDER BY p.created_at DESC
                """;
        List<ProjectMembership> results = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, userId);
            ps.setString(2, role.name());          // 'LEADER' or 'MEMBER'
            ResultSet rs = ps.executeQuery();
            while (rs.next()) results.add(mapMembershipRow(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find projects by user+role: " + e.getMessage(), e);
        }
        return results;
    }

    @Override
    public void addMember(String projectId, String userId, ProjectRole role) {
        String sql = """
                INSERT INTO project_members (project_id, user_id, role)
                VALUES (?, CAST(? AS UNSIGNED), ?)
                ON DUPLICATE KEY UPDATE role = VALUES(role)
                """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, projectId);
            ps.setString(2, userId);
            ps.setString(3, role.name());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to add member: " + e.getMessage(), e);
        }
    }

    @Override
    public void removeMember(String projectId, String userId) {
        String sql = "DELETE FROM project_members WHERE project_id = ? AND user_id = CAST(? AS UNSIGNED)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, projectId);
            ps.setString(2, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to remove member: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<ProjectRole> findUserRole(String projectId, String userId) {
        String sql = """
                SELECT role FROM project_members
                WHERE project_id = ? AND user_id = CAST(? AS UNSIGNED)
                """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, projectId);
            ps.setString(2, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return Optional.of(ProjectRole.valueOf(rs.getString("role")));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find user role: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    private List<ProjectMembership> queryMemberships(String sql, String userId) {
        List<ProjectMembership> results = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) results.add(mapMembershipRow(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Failed to query memberships: " + e.getMessage(), e);
        }
        return results;
    }

    private ProjectMembership mapMembershipRow(ResultSet rs) throws SQLException {
        Project project = mapRow(rs);
        ProjectRole role = ProjectRole.valueOf(rs.getString("role"));
        return new ProjectMembership(project, role);
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