package com.example.flora.Features.Auth.repository;

import com.example.flora.Features.Auth.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserRepositoryImpl implements UserRepository {

    private final Connection connection;

    public UserRepositoryImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void saveUser(User user) {
        String sql = "INSERT INTO users (email, password, username) VALUES (?, ?, ?)";

        try (PreparedStatement ps =
                     connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, user.getEmail());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getUsername());

            ps.executeUpdate();

            ResultSet keys = ps.getGeneratedKeys();

            if (keys.next()) {
                user.setId(keys.getInt(1));
            }

        } catch (SQLException e) {
            e.printStackTrace();

            throw new RuntimeException(
                    "Failed to save user: " + e.getMessage(), e
            );
        }
    }

    @Override
    public User findByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find user by username: " + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public User findByID(Integer id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find user by id: " + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public User findByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find user by email: " + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public boolean userExist(String email) {
        return findByEmail(email) != null;
    }

    private User mapRow(ResultSet rs) throws SQLException {
        return new User(
                rs.getString("email"),
                rs.getString("password"),
                rs.getInt("id")
        );
    }

    @Override
    public List<String> searchByUsernameLike(String query) {
        List<String> users = new ArrayList<>();
        String sql = """
            SELECT username
            FROM users
            WHERE username LIKE ?
            ORDER BY username
            LIMIT 6
            """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, "%" + query + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                users.add(rs.getString("username"));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to search usernames: " + e.getMessage(), e);
        }
        return users;
    }
}