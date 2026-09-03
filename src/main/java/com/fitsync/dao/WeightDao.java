package com.fitsync.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.fitsync.model.WeightEntry;

public class WeightDao {

    private final Connection connection;

    public WeightDao() {
        this.connection = DatabaseManager.getInstance().getConnection();
    }

    public boolean save(WeightEntry entry) {
        String sql = "INSERT INTO weight_entries " +
                "(user_id, weight_kg, recorded_at) " +
                "VALUES (?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt   (1, entry.getUserId());
            stmt.setDouble(2, entry.getWeightKg());
            stmt.setString(3, entry.getRecordedAt());
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Failed to save weight entry: " + e.getMessage());
            return false;
        }
    }

    public List<WeightEntry> findByUserId(int userId) {
        String sql = "SELECT * FROM weight_entries " +
                "WHERE user_id = ? ORDER BY recorded_at DESC, id DESC";

        List<WeightEntry> entries = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                entries.add(new WeightEntry(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getDouble("weight_kg"),
                        rs.getString("recorded_at")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Failed to fetch weight entries: " + e.getMessage());
        }

        return entries;
    }
}
