package com.fitsync.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.fitsync.model.Goal;

public class GoalDao {

    private final Connection connection;

    public GoalDao() {
        this.connection = DatabaseManager.getInstance().getConnection();
    }

    public boolean save(Goal goal) {
        String sql = "INSERT INTO goals " +
                "(user_id, goal_description, target_value, current_value, created_at) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt   (1, goal.getUserId());
            stmt.setString(2, goal.getGoalDescription());
            stmt.setDouble(3, goal.getTargetValue());
            stmt.setDouble(4, goal.getCurrentValue());
            stmt.setString(5, goal.getCreatedAt());
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Failed to save goal: " + e.getMessage());
            return false;
        }
    }

    public List<Goal> findByUserId(int userId) {
        String sql = "SELECT * FROM goals " +
                "WHERE user_id = ? ORDER BY created_at DESC, id DESC";

        List<Goal> goals = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                goals.add(new Goal(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getString("goal_description"),
                        rs.getDouble("target_value"),
                        rs.getDouble("current_value"),
                        rs.getString("created_at")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Failed to fetch goals: " + e.getMessage());
        }

        return goals;
    }

    public boolean updateProgress(int goalId, double currentValue) {
        String sql = "UPDATE goals SET current_value = ? WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDouble(1, currentValue);
            stmt.setInt   (2, goalId);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Failed to update goal progress: " + e.getMessage());
            return false;
        }
    }
}
