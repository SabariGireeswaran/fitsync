package com.fitsync.dao;

import com.fitsync.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class UserDao {

    private final Connection connection;

    public UserDao() {
        this.connection = DatabaseManager.getInstance().getConnection();
    }

    public boolean save(User user) {
        String sql = """
                INSERT INTO users (name, email, password, age, gender, height_cm, weight_kg, created_at)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, user.getName());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPassword());
            stmt.setInt   (4, user.getAge());
            stmt.setString(5, user.getGender());
            stmt.setDouble(6, user.getHeightCm());
            stmt.setDouble(7, user.getWeightKg());
            stmt.setString(8, user.getCreatedAt());
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Failed to save user: " + e.getMessage());
            return false;
        }
    }

    public Optional<User> findByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                User user = new User(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getInt("age"),
                        rs.getString("gender"),
                        rs.getDouble("height_cm"),
                        rs.getDouble("weight_kg"),
                        rs.getString("created_at")
                );
                return Optional.of(user);
            }

        } catch (SQLException e) {
            System.err.println("Failed to find user: " + e.getMessage());
        }

        return Optional.empty();
    }

    public boolean emailExists(String email) {
        String sql = "SELECT COUNT(*) FROM users WHERE email = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            System.err.println("Failed to check email: " + e.getMessage());
        }

        return false;
    }
}
