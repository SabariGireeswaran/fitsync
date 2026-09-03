package com.fitsync.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.fitsync.model.BmiRecord;

public class BmiDao {

    private final Connection connection;

    public BmiDao() {
        this.connection = DatabaseManager.getInstance().getConnection();
    }

    public boolean save(BmiRecord record) {
        String sql = "INSERT INTO bmi_records " +
                "(user_id, bmi_value, category, recorded_at) " +
                "VALUES (?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt   (1, record.getUserId());
            stmt.setDouble(2, record.getBmiValue());
            stmt.setString(3, record.getCategory());
            stmt.setString(4, record.getRecordedAt());
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Failed to save BMI record: " + e.getMessage());
            return false;
        }
    }

    public Optional<BmiRecord> findLatestByUserId(int userId) {
        String sql = "SELECT * FROM bmi_records " +
                "WHERE user_id = ? ORDER BY recorded_at DESC, id DESC LIMIT 1";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(new BmiRecord(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getDouble("bmi_value"),
                        rs.getString("category"),
                        rs.getString("recorded_at")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Failed to fetch BMI record: " + e.getMessage());
        }

        return Optional.empty();
    }

    public List<BmiRecord> findByUserId(int userId) {
        String sql = "SELECT * FROM bmi_records " +
                "WHERE user_id = ? ORDER BY recorded_at DESC, id DESC";

        List<BmiRecord> records = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                records.add(new BmiRecord(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getDouble("bmi_value"),
                        rs.getString("category"),
                        rs.getString("recorded_at")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Failed to fetch BMI records: " + e.getMessage());
        }

        return records;
    }
}
