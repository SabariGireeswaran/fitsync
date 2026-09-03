package com.fitsync.dao;

import com.fitsync.config.AppConfig;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
    private static DatabaseManager instance;
    private Connection connection;

    private DatabaseManager(){
        initialise();
    }

    public static DatabaseManager getInstance(){
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    private void initialise() {
        try {
            File dbDir = new File(AppConfig.DB_DIR);
            if (!dbDir.exists()){
                dbDir.mkdirs();
            }

            String url = "jdbc:sqlite:" + AppConfig.DB_PATH;
            connection = DriverManager.getConnection(url);

            System.out.println("Databaase connected: " + AppConfig.DB_PATH);

            createTables();

        }catch(SQLException e) {
            System.err.println("Database connection failed: " + e.getMessage());
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed.");
            }
        } catch(SQLException e) {
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
            System.out.println("Tables ready.");
        }
    }
}
