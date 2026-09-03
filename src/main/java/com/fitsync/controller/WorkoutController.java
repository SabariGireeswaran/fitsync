package com.fitsync.controller;

import com.fitsync.FitSyncApp;
import com.fitsync.model.User;
import com.fitsync.model.WorkoutLog;
import com.fitsync.service.WorkoutService;
import com.fitsync.util.AlertUtil;
import com.fitsync.util.ValidationUtil;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
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
    @FXML private Button logButton;
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

        workoutTable.setPlaceholder(new Label("No workouts logged yet."));
        loadWorkoutHistory();
    }

    @FXML
    private void handleLogWorkout() {
        String exercise = exerciseField.getText() == null ? "" : exerciseField.getText().trim();
        String durationText = durationField.getText() == null ? "" : durationField.getText().trim();
        String caloriesText = caloriesField.getText() == null ? "" : caloriesField.getText().trim();

        if (!ValidationUtil.isNotEmpty(exercise)) {
            reject("Please enter the type of exercise.");
            return;
        }
        if (!ValidationUtil.isPositiveNumber(durationText)) {
            reject("Duration must be a number greater than zero.");
            return;
        }
        if (!ValidationUtil.isPositiveNumber(caloriesText)) {
            reject("Calories burned must be a number greater than zero.");
            return;
        }

        User currentUser = DashboardController.getCurrentUser();
        if (currentUser == null) {
            AlertUtil.showError("Session Expired", "Please log in again.");
            return;
        }

        setBusy(true);
        try {
            int duration = (int) Math.round(Double.parseDouble(durationText));
            double calories = Double.parseDouble(caloriesText);

            boolean success = workoutService.logWorkout(
                    currentUser.getId(), exercise, duration, calories);

            if (success) {
                successLabel.setText("Workout logged successfully!");
                errorLabel.setText("");
                exerciseField.clear();
                durationField.clear();
                caloriesField.clear();
                loadWorkoutHistory();
                AlertUtil.showSuccess("Workout Logged",
                        exercise + " (" + duration + " min) has been added to your history.");
            } else {
                reject("Could not log the workout. Please try again.");
            }
        } finally {
            setBusy(false);
        }
    }

    private void loadWorkoutHistory() {
        User currentUser = DashboardController.getCurrentUser();
        if (currentUser != null) {
            List<WorkoutLog> logs = workoutService.getWorkoutHistory(currentUser.getId());
            workoutTable.setItems(FXCollections.observableArrayList(logs));
        }
    }

    @FXML
    private void handleBack() {
        try {
            FitSyncApp.showDashboardScreen();
        } catch (IOException e) {
            AlertUtil.showError("Navigation Error", e.getMessage());
        }
    }

    private void reject(String message) {
        errorLabel.setText(message);
        successLabel.setText("");
        AlertUtil.showError("Invalid Input", message);
    }

    private void setBusy(boolean busy) {
        if (logButton != null) {
            logButton.setDisable(busy);
        }
    }
}
