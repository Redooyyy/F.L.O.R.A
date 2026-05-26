package com.example.flora.Features.Bug.repository;

import com.example.flora.Features.Bug.model.Bug;
import com.example.flora.Features.Bug.model.BugSeverity;
import com.example.flora.Features.Bug.model.BugStatus;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * ---------------------------------------------------------------
 * CREATE TABLE bugs (
 *     id               VARCHAR(36)  PRIMARY KEY,
 *     project_name     VARCHAR(100) NOT NULL,
 *     title            VARCHAR(255) NOT NULL,
 *     description      TEXT,
 *     severity         ENUM('CRITICAL','HIGH','MEDIUM','LOW') NOT NULL,
 *     status           ENUM('OPEN','IN_PROGRESS','CLOSED')    NOT NULL DEFAULT 'OPEN',
 *     fixing_user_id   VARCHAR(100) NULL,
 *     reported_by      VARCHAR(100) NOT NULL,
 *     reported_date    VARCHAR(50)  NOT NULL
 * );
 * ---------------------------------------------------------------
 *
 */
public class BugRepositoryImpl implements BugRepository {

    private final Connection connection;

    public BugRepositoryImpl(Connection connection) {
        this.connection = connection;
    }


    @Override
    public void save(Bug bug) {
        String sql = """
            INSERT INTO bugs
                (project_name, title, description, severity, status, fixing_user_id, reported_by, reported_date)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            """;
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, bug.getProjectName());
            ps.setString(2, bug.getTitle());
            ps.setString(3, bug.getDescription());
            ps.setString(4, bug.getSeverity().name());
            ps.setString(5, bug.getStatus().name());
            ps.setString(6, bug.getFixingUserId());
            ps.setString(7, bug.getReportedByUserId());
            ps.setString(8, bug.getReportedDate());
            ps.executeUpdate();

            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) {
                bug.setId(keys.getInt(1));
            }
        } catch (SQLException e) {
            throw new RuntimeException("save failed: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Bug> findAll() {
        String sql = "SELECT * FROM bugs";
        List<Bug> bugs = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) bugs.add(mapRow(rs));
        } catch (SQLException e) {
            throw new RuntimeException("findAll failed: " + e.getMessage(), e);
        }
        return bugs;
    }

    @Override
    public List<Bug> findByProject(String projectName) {
        String sql = "SELECT * FROM bugs WHERE project_name = ?";
        List<Bug> bugs = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, projectName);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) bugs.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("findByProject failed: " + e.getMessage(), e);
        }
        return bugs;
    }

    @Override
    public Optional<Bug> findById(int id) {
        String sql = "SELECT * FROM bugs WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("findById failed: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public List<String> findDistinctProjectNames() {
        String sql = "SELECT DISTINCT project_name FROM bugs ORDER BY project_name";
        List<String> names = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) names.add(rs.getString("project_name"));
        } catch (SQLException e) {
            throw new RuntimeException("findDistinctProjectNames failed: " + e.getMessage(), e);
        }
        return names;
    }

    @Override
    public List<Bug> findFiltered(String projectName, BugSeverity severity, BugStatus status) {
        // Build query dynamically based on which filters are active
        StringBuilder sql = new StringBuilder("SELECT * FROM bugs WHERE 1=1");
        if (projectName != null) sql.append(" AND project_name = ?");
        if (severity    != null) sql.append(" AND severity = ?");
        if (status      != null) sql.append(" AND status = ?");

        List<Bug> bugs = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            int idx = 1;
            if (projectName != null) ps.setString(idx++, projectName);
            if (severity    != null) ps.setString(idx++, severity.name());
            if (status      != null) ps.setString(idx,   status.name());

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) bugs.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("findFiltered failed: " + e.getMessage(), e);
        }
        return bugs;
    }

    @Override
    public Optional<Bug> updateStatus(int bugId, BugStatus newStatus) {
        String sql = "UPDATE bugs SET status = ? WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, newStatus.name());
            ps.setInt(2, bugId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("updateStatus failed: " + e.getMessage(), e);
        }
        return findById(bugId);
    }

    @Override
    public Optional<Bug> assignFixer(int bugId, String fixerUserId) {
        String sql = "UPDATE bugs SET fixing_user_id = ?, status = ? WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, fixerUserId);
            ps.setString(2, BugStatus.IN_PROGRESS.name());
            ps.setInt(3, bugId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("assignFixer failed: " + e.getMessage(), e);
        }
        return findById(bugId);
    }

    @Override
    public String findProjectLeader(String projectName) {
        if (projectName == null) return "";
        String sql = "SELECT owner_id FROM projects WHERE name = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, projectName);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getString("owner_id");
            }
        } catch (SQLException e) {
            throw new RuntimeException("findProjectLeader failed: " + e.getMessage(), e);
        }
        return "";
    }

    private Bug mapRow(ResultSet rs) throws SQLException {
        return new Bug(
                rs.getInt("id"),
                rs.getString("project_name"),
                rs.getString("title"),
                rs.getString("description"),
                BugSeverity.valueOf(rs.getString("severity")),
                BugStatus.valueOf(rs.getString("status")),
                rs.getString("fixing_user_id"),   // may be null — that's fine
                rs.getString("reported_by"),
                rs.getString("reported_date")
        );
    }
}