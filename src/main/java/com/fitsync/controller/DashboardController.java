package com.fitsync.controller;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import com.fitsync.FitSyncApp;
import com.fitsync.config.AppConfig;
import com.fitsync.model.BmiRecord;
import com.fitsync.model.User;
import com.fitsync.service.BmiService;
import com.fitsync.service.WeightService;
import com.fitsync.service.WorkoutService;
import com.fitsync.util.AlertUtil;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;

public class DashboardController implements Initializable {

    @FXML private Label welcomeLabel;
    @FXML private Label sidebarNameLabel;
    @FXML private Label sidebarEmailLabel;
    @FXML private Label versionLabel;
    @FXML private Label bmiLabel;
    @FXML private Label bmiCategoryLabel;
    @FXML private Label workoutCountLabel;
    @FXML private Label weightLabel;
    @FXML private ProgressIndicator loadingIndicator;

    private static User currentUser;

    private final BmiService bmiService = new BmiService();
    private final WorkoutService workoutService = new WorkoutService();
    private final WeightService weightService = new WeightService();

    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (versionLabel != null) {
            versionLabel.setText(AppConfig.APP_NAME + " v" + AppConfig.APP_VERSION);
        }
        bmiLabel.setText("--");
        bmiCategoryLabel.setText("Not calculated yet");
        workoutCountLabel.setText("0");
        weightLabel.setText("-- kg");

        if (currentUser == null) {
            return;
        }

        welcomeLabel.setText("Welcome back, " + currentUser.getName() + "!");
        if (sidebarNameLabel != null) {
            sidebarNameLabel.setText(currentUser.getName());
        }
        if (sidebarEmailLabel != null) {
            sidebarEmailLabel.setText(currentUser.getEmail());
        }

        loadStats();
    }

    /** Loads the dashboard figures off the UI thread with a progress spinner. */
    private void loadStats() {
        setLoading(true);

        Task<Stats> task = new Task<>() {
            @Override
            protected Stats call() {
                Stats s = new Stats();
                int userId = currentUser.getId();

                double latestWeight = weightService.getLatestWeight(userId);
                if (latestWeight <= 0) {
                    latestWeight = currentUser.getWeightKg();
                }
                s.weight = latestWeight;

                Optional<BmiRecord> latestBmi = bmiService.getLatestBmi(userId);
                if (latestBmi.isPresent()) {
                    s.bmi = latestBmi.get().getBmiValue();
                    s.bmiCategory = latestBmi.get().getCategory();
                } else if (latestWeight > 0 && currentUser.getHeightCm() > 0) {
                    s.bmi = bmiService.calculateBmi(latestWeight, currentUser.getHeightCm());
                    s.bmiCategory = bmiService.classifyBmi(s.bmi);
                }

                s.workouts = workoutService.getTotalWorkouts(userId);
                return s;
            }
        };

        task.setOnSucceeded(e -> {
            Stats s = task.getValue();
            weightLabel.setText(s.weight > 0
                    ? String.format("%.1f kg", s.weight) : "-- kg");
            if (s.bmi > 0) {
                bmiLabel.setText(String.valueOf(s.bmi));
                bmiCategoryLabel.setText(s.bmiCategory);
            }
            workoutCountLabel.setText(String.valueOf(s.workouts));
            setLoading(false);
        });

        task.setOnFailed(e -> {
            setLoading(false);
            AlertUtil.showError("Dashboard Error",
                    "Could not load your latest stats: "
                    + (task.getException() != null ? task.getException().getMessage() : "unknown error"));
        });

        Thread thread = new Thread(task, "dashboard-stats");
        thread.setDaemon(true);
        thread.start();
    }

    private void setLoading(boolean loading) {
        if (loadingIndicator != null) {
            Platform.runLater(() -> {
                loadingIndicator.setVisible(loading);
                loadingIndicator.setManaged(loading);
            });
        }
    }

    @FXML private void showDashboard() { }

    @FXML private void showBmi()            { navigate(FitSyncApp::showBmiScreen); }
    @FXML private void showWorkout()        { navigate(FitSyncApp::showWorkoutScreen); }
    @FXML private void showWeight()         { navigate(FitSyncApp::showWeightScreen); }
    @FXML private void showGoals()          { navigate(FitSyncApp::showGoalScreen); }
    @FXML private void showReport()         { navigate(FitSyncApp::showReportScreen); }
    @FXML private void showRecommendation() { navigate(FitSyncApp::showRecommendationScreen); }

    @FXML
    private void handleLogout() {
        boolean confirmed = AlertUtil.showConfirmation("Log Out",
                "Are you sure you want to log out?");
        if (!confirmed) {
            return;
        }
        currentUser = null;
        navigate(FitSyncApp::showLoginScreen);
    }

    private void navigate(Navigation action) {
        try {
            action.go();
        } catch (IOException e) {
            AlertUtil.showError("Navigation Error", e.getMessage());
        }
    }

    @FunctionalInterface
    private interface Navigation {
        void go() throws IOException;
    }

    private static final class Stats {
        double weight;
        double bmi;
        String bmiCategory = "Not calculated yet";
        int workouts;
    }
}
