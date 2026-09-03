package com.fitsync.controller;

import com.fitsync.FitSyncApp;
import com.fitsync.model.BmiRecord;
import com.fitsync.model.User;
import com.fitsync.model.WeightEntry;
import com.fitsync.service.BmiService;
import com.fitsync.service.WeightService;
import com.fitsync.service.WorkoutService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ReportController implements Initializable {

    @FXML private Label totalWorkoutsLabel;
    @FXML private Label avgCaloriesLabel;
    @FXML private Label weightChangeLabel;
    @FXML private Label weightRangeLabel;
    @FXML private ListView<String> bmiHistoryList;
    @FXML private Label emptyBmiLabel;

    private final WorkoutService workoutService = new WorkoutService();
    private final BmiService bmiService = new BmiService();
    private final WeightService weightService = new WeightService();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        User currentUser = DashboardController.getCurrentUser();
        if (currentUser == null) {
            return;
        }
        int userId = currentUser.getId();

        // Total workouts + average calories burned
        int totalWorkouts = workoutService.getTotalWorkouts(userId);
        totalWorkoutsLabel.setText(String.valueOf(totalWorkouts));
        avgCaloriesLabel.setText(workoutService.getAverageCalories(userId) + " kcal");

        // BMI history list
        List<BmiRecord> bmiHistory = bmiService.getBmiHistory(userId);
        if (bmiHistory.isEmpty()) {
            emptyBmiLabel.setText("No BMI records yet. Use the BMI Calculator to add one.");
        } else {
            emptyBmiLabel.setText("");
            List<String> rows = bmiHistory.stream()
                    .map(r -> r.getRecordedAt() + "   |   BMI " + r.getBmiValue()
                            + "   |   " + r.getCategory())
                    .toList();
            bmiHistoryList.setItems(FXCollections.observableArrayList(rows));
        }

        // Weight change over time (oldest recorded entry vs latest)
        List<WeightEntry> weightHistory = weightService.getWeightHistory(userId);
        if (weightHistory.size() < 2) {
            weightChangeLabel.setText("Not enough data");
            weightRangeLabel.setText("Log at least two weight entries to see progress.");
        } else {
            double latest = weightHistory.get(0).getWeightKg();
            double oldest = weightHistory.get(weightHistory.size() - 1).getWeightKg();
            double change = Math.round((latest - oldest) * 100.0) / 100.0;
            String direction = change > 0 ? "gained" : (change < 0 ? "lost" : "no change");
            weightChangeLabel.setText((change > 0 ? "+" : "") + change + " kg");
            weightRangeLabel.setText("From " + oldest + " kg (" +
                    weightHistory.get(weightHistory.size() - 1).getRecordedAt() +
                    ") to " + latest + " kg (" +
                    weightHistory.get(0).getRecordedAt() + ")  -  " + direction);
        }
    }

    @FXML
    private void handleBack() throws IOException {
        FitSyncApp.showDashboardScreen();
    }
}
