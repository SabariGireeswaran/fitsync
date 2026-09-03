package com.fitsync.controller;

import com.fitsync.FitSyncApp;
import com.fitsync.model.BmiRecord;
import com.fitsync.model.User;
import com.fitsync.model.WeightEntry;
import com.fitsync.service.BmiService;
import com.fitsync.service.WeightService;
import com.fitsync.service.WorkoutService;
import com.fitsync.util.AlertUtil;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressIndicator;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ReportController implements Initializable {

    @FXML private Label totalWorkoutsLabel;
    @FXML private Label avgCaloriesLabel;
    @FXML private Label weightChangeLabel;
    @FXML private Label weightRangeLabel;
    @FXML private ListView<String> bmiHistoryList;
    @FXML private Label emptyBmiLabel;
    @FXML private ProgressIndicator loadingIndicator;

    private final WorkoutService workoutService = new WorkoutService();
    private final BmiService bmiService = new BmiService();
    private final WeightService weightService = new WeightService();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        User currentUser = DashboardController.getCurrentUser();
        if (currentUser == null) {
            return;
        }
        loadReport(currentUser.getId());
    }

    private void loadReport(int userId) {
        setLoading(true);

        Task<ReportData> task = new Task<>() {
            @Override
            protected ReportData call() {
                ReportData data = new ReportData();
                data.totalWorkouts = workoutService.getTotalWorkouts(userId);
                data.avgCalories = workoutService.getAverageCalories(userId);
                data.bmiHistory = bmiService.getBmiHistory(userId);
                data.weightHistory = weightService.getWeightHistory(userId);
                return data;
            }
        };

        task.setOnSucceeded(e -> {
            render(task.getValue());
            setLoading(false);
        });

        task.setOnFailed(e -> {
            setLoading(false);
            AlertUtil.showError("Report Error",
                    "Could not build your report: "
                    + (task.getException() != null ? task.getException().getMessage() : "unknown error"));
        });

        Thread thread = new Thread(task, "report-loader");
        thread.setDaemon(true);
        thread.start();
    }

    private void render(ReportData data) {
        totalWorkoutsLabel.setText(String.valueOf(data.totalWorkouts));
        avgCaloriesLabel.setText(data.avgCalories + " kcal");

        if (data.bmiHistory.isEmpty()) {
            emptyBmiLabel.setText("No BMI records yet. Use the BMI Calculator to add one.");
            bmiHistoryList.setItems(FXCollections.observableArrayList());
        } else {
            emptyBmiLabel.setText("");
            List<String> rows = new ArrayList<>();
            for (BmiRecord r : data.bmiHistory) {
                rows.add(r.getRecordedAt() + "     BMI " + r.getBmiValue()
                        + "     " + r.getCategory());
            }
            bmiHistoryList.setItems(FXCollections.observableArrayList(rows));
        }

        if (data.weightHistory.size() < 2) {
            weightChangeLabel.setText("Not enough data");
            weightRangeLabel.setText("Log at least two weight entries to see progress.");
        } else {
            WeightEntry newest = data.weightHistory.get(0);
            WeightEntry oldest = data.weightHistory.get(data.weightHistory.size() - 1);
            double change = Math.round((newest.getWeightKg() - oldest.getWeightKg()) * 100.0) / 100.0;
            String direction = change > 0 ? "gained" : (change < 0 ? "lost" : "no change");
            weightChangeLabel.setText((change > 0 ? "+" : "") + change + " kg");
            weightRangeLabel.setText("From " + oldest.getWeightKg() + " kg (" + oldest.getRecordedAt()
                    + ") to " + newest.getWeightKg() + " kg (" + newest.getRecordedAt()
                    + ")   -   " + direction);
        }
    }

    private void setLoading(boolean loading) {
        if (loadingIndicator != null) {
            loadingIndicator.setVisible(loading);
            loadingIndicator.setManaged(loading);
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

    private static final class ReportData {
        int totalWorkouts;
        double avgCalories;
        List<BmiRecord> bmiHistory = new ArrayList<>();
        List<WeightEntry> weightHistory = new ArrayList<>();
    }
}
