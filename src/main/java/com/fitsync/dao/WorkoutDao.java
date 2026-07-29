package com.fitsync.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.fitsync.model.WorkoutLog;

public class WorkoutDao {
    
    private final Connection connection;

    public WorkoutDao() {
        this.connection = DatabaseManager.getInstance().getConnection();
    }

    public boolean save(WorkoutLog log) {
        String sql = """
                INSERT INTO workout_logs
                (user_id, exercise_type, duration_minutes,
                calories_burned, logged_at)
                """;
        
        try(PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, log.getUserId());
            stmt.setString(2, log.getExerciseType());
            stmt.setInt(3, log.getDurationMinutes());
            stmt.setDouble(4, log.getCaloriesBurned());
            stmt.setString(5, log.getLoggedAt());
            stmt.executeUpdate();
            return true;
        } catch(SQLException e) {
            System.err.println("Failed to save workout: " + e.getMessage());
            return false;
        }
    }

    public List<WorkoutLog> findByUserId(int userId) {
        String sql = """
                SELECT * FROM workout_logs
                WHERE user_id = ?
                ORDER BY logged_at DESC
                """;
        
        List<WorkoutLog> logs = new ArrayList<>();

        try(PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                logs.add(new WorkoutLog(
                        rs.getInt(""),
                        rs.getInt("user_id"),
                        rs.getString("exercise_type"),
                        rs.getInt("duration_minutes"),
                        rs.getDouble("calories_burned"),
                        rs.getString("logged_at")
                ));
            }
        } catch(SQLException e) {
            System.err.println("Failed to fetch workouts: " + e.getMessage());
        }

        return logs;
    }

    public int countByUserId(int userId) {
        String sql = "SELECT COUNT(*) FROM workout_logs WHERE user_id = ?";

        try(PreparedStatement stmt = connection.prepareStatement((sql))) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()) {
                return rs.getInt(1);
            }
        } catch(SQLException e) {
            System.err.println("Failed to count workouts: " + e.getMessage()); 
        }
        return 0;
    }
}
