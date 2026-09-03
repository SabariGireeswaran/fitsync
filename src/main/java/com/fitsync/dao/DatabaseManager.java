package com.fitsync.dao;

import com.fitsync.config.AppConfig;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Singleton owner of the single SQLite {@link Connection}. Creates the
 * database file, the schema (all five tables) and exposes a small health
 * check used at start-up.
 */
public class DatabaseManager {

    /** Every table the application depends on. */
    public static final String[] REQUIRED_TABLES = {
            "users", "workout_logs", "bmi_records", "weight_entries", "goals"
    };

    private static DatabaseManager instance;
    private Connection connection;

    private DatabaseManager() {
        initialise();
    }

    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    private void initialise() {
        try {
            File dbDir = new File(AppConfig.DB_DIR);
            if (!dbDir.exists() && !dbDir.mkdirs()) {
                System.err.println("Warning: could not create data directory " + AppConfig.DB_DIR);
            }

            String url = "jdbc:sqlite:" + AppConfig.DB_PATH;
            connection = DriverManager.getConnection(url);

            try (Statement pragma = connection.createStatement()) {
                pragma.execute("PRAGMA foreign_keys = ON");
            }

            System.out.println("Database connected: " + AppConfig.DB_PATH);

            createTables();
            verifySchema();

        } catch (SQLException e) {
            System.err.println("FATAL: database initialisation failed: " + e.getMessage());
            throw new IllegalStateException("Could not initialise the FitSync database", e);
        }
    }

    public Connection getConnection() {
        return connection;
    }

    /** @return {@code true} if the connection is open and usable. */
    public boolean isHealthy() {
        try {
            return connection != null && !connection.isClosed() && connection.isValid(2);
        } catch (SQLException e) {
            return false;
        }
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }

    private void createTables() throws SQLException {
        String createUsersTable =
                "CREATE TABLE IF NOT EXISTS users (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "name TEXT NOT NULL," +
                        "email TEXT NOT NULL UNIQUE," +
                        "password TEXT NOT NULL," +
                        "age INTEGER NOT NULL," +
                        "gender TEXT NOT NULL," +
                        "height_cm REAL NOT NULL," +
                        "weight_kg REAL NOT NULL," +
                        "created_at TEXT NOT NULL)";

        String createWorkoutTable =
                "CREATE TABLE IF NOT EXISTS workout_logs (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "user_id INTEGER NOT NULL," +
                        "exercise_type TEXT NOT NULL," +
                        "duration_minutes INTEGER NOT NULL," +
                        "calories_burned REAL NOT NULL," +
                        "logged_at TEXT NOT NULL)";

        String createBmiTable =
                "CREATE TABLE IF NOT EXISTS bmi_records (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "user_id INTEGER NOT NULL," +
                        "bmi_value REAL NOT NULL," +
                        "category TEXT NOT NULL," +
                        "recorded_at TEXT NOT NULL)";

        String createWeightTable =
                "CREATE TABLE IF NOT EXISTS weight_entries (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "user_id INTEGER NOT NULL," +
                        "weight_kg REAL NOT NULL," +
                        "recorded_at TEXT NOT NULL)";

        String createGoalsTable =
                "CREATE TABLE IF NOT EXISTS goals (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "user_id INTEGER NOT NULL," +
                        "goal_description TEXT NOT NULL," +
                        "target_value REAL NOT NULL," +
                        "current_value REAL NOT NULL," +
                        "created_at TEXT NOT NULL)";

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createUsersTable);
            stmt.execute(createWorkoutTable);
            stmt.execute(createBmiTable);
            stmt.execute(createWeightTable);
            stmt.execute(createGoalsTable);
            System.out.println("Schema ready (" + REQUIRED_TABLES.length + " tables).");
        }
    }

    /** Confirms every required table is present; logs a clear error otherwise. */
    private void verifySchema() throws SQLException {
        String sql = "SELECT name FROM sqlite_master WHERE type = 'table' AND name = ?";
        for (String table : REQUIRED_TABLES) {
            try (var stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, table);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (!rs.next()) {
                        throw new SQLException("Required table missing after creation: " + table);
                    }
                }
            }
        }
    }
}
