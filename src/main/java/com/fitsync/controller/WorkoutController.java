package com.fitsync.controller;

import com.fitsync.FitSyncApp;
import com.fitsync.model.User;
import com.fitsync.model.WorkoutLog;
import com.fitsync.service.WorkoutService;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class WorkoutController implements Initializable {

    @FXML private TextField exerciseField;
    @FXML private TextField durationField;
    @FXML private TextField caloriesField;
    @FXML private Label errorLabel;
    @FXML private Label successLabel;
    @FXML private TableView<WorkoutLog> workoutTable;
    @FXML private TableColumn<WorkoutLog, String> exerciseCol;
    @FXML private TableColumn<WorkoutLog, Number> durationCol;
    @FXML private TableColumn<WorkoutLog, Number> caloriesCol;
    @FXML private TableColumn<WorkoutLog, String> dateCol;

    private final WorkoutService workoutService = new WorkoutService();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        exerciseCol.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getExerciseType()));
        durationCol.setCellValueFactory(data ->
                new SimpleIntegerProperty(data.getValue().getDurationMinutes()));
        caloriesCol.setCellValueFactory(data ->
                new SimpleDoubleProperty(data.getValue().getCaloriesBurned()));
        dateCol.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getLoggedAt()));

        loadWorkoutHistory();
    }

    @FXML
    private void handleLogWorkout() {
        String exercise = exerciseField.getText().trim();
        String durationText = durationField.getText().trim();
        String caloriesText = caloriesField.getText().trim();

        if (exercise.isEmpty() || durationText.isEmpty() || caloriesText.isEmpty()) {
            errorLabel.setText("Please fill in all fields.");
            successLabel.setText("");
            return;
        }

        try {
            int duration = Integer.parseInt(durationText);
            double calories = Double.parseDouble(caloriesText);

            User currentUser = DashboardController.getCurrentUser();
            if (currentUser == null) {
                errorLabel.setText("Session expired. Please login again.");
                return;
            }

            boolean success = workoutService.logWorkout(
                    currentUser.getId(), exercise, duration, calories);

            if (success) {
                successLabel.setText("Workout logged successfully!");
                errorLabel.setText("");
                exerciseField.clear();
                durationField.clear();
                caloriesField.clear();
                loadWorkoutHistory();
            } else {
                errorLabel.setText("Failed to log workout. Please try again.");
            }

        } catch (NumberFormatException e) {
            errorLabel.setText("Duration and calories must be numbers.");
        }
    }

    private void loadWorkoutHistory() {
        User currentUser = DashboardController.getCurrentUser();
        if (currentUser != null) {
            List<WorkoutLog> logs = workoutService
                    .getWorkoutHistory(currentUser.getId());
            workoutTable.setItems(
                    FXCollections.observableArrayList(logs));
        }
    }

    @FXML
    private void handleBack() throws IOException {
        FitSyncApp.showDashboardScreen();
    }
}