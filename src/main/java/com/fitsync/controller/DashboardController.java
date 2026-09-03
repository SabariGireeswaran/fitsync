package com.fitsync.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import com.fitsync.FitSyncApp;
import com.fitsync.model.BmiRecord;
import com.fitsync.model.User;
import com.fitsync.service.BmiService;
import com.fitsync.service.WeightService;
import com.fitsync.service.WorkoutService;

import java.util.Optional;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

public class DashboardController implements Initializable{
    @FXML private Label welcomeLabel;
    @FXML private Label bmiLabel;
    @FXML private Label bmiCategoryLabel;
    @FXML private Label workoutCountLabel;
    @FXML private Label weightLabel;

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
    public void initialize(URL url, ResourceBundle resourceBundle){
        bmiLabel.setText("--");
        bmiCategoryLabel.setText("Not calculated yet");
        workoutCountLabel.setText("0");
        weightLabel.setText("-- kg");

        if (currentUser == null) {
            return;
        }

        welcomeLabel.setText("Welcome back, " + currentUser.getName() + "!");

        // Live current weight: latest logged entry, falling back to profile weight
        double latestWeight = weightService.getLatestWeight(currentUser.getId());
        if (latestWeight <= 0) {
            latestWeight = currentUser.getWeightKg();
        }
        weightLabel.setText(latestWeight + " kg");

        // Live BMI: latest stored BMI record, otherwise computed from latest weight
        Optional<BmiRecord> latestBmi = bmiService.getLatestBmi(currentUser.getId());
        if (latestBmi.isPresent()) {
            BmiRecord record = latestBmi.get();
            bmiLabel.setText(String.valueOf(record.getBmiValue()));
            bmiCategoryLabel.setText(record.getCategory());
        } else if (latestWeight > 0 && currentUser.getHeightCm() > 0) {
            double bmi = bmiService.calculateBmi(latestWeight, currentUser.getHeightCm());
            bmiLabel.setText(String.valueOf(bmi));
            bmiCategoryLabel.setText(bmiService.classifyBmi(bmi));
        }

        int workoutCount = workoutService.getTotalWorkouts(currentUser.getId());
        workoutCountLabel.setText(String.valueOf(workoutCount));
    }

    @FXML 
    private void showDashboard() {}
    
    @FXML
    private void showBmi() throws IOException {
        FitSyncApp.showBmiScreen();
    }

    @FXML
    private void showWorkout() throws IOException {
        FitSyncApp.showWorkoutScreen();
    }

    @FXML
    private void showWeight() throws IOException {
        FitSyncApp.showWeightScreen();
    }

    @FXML
    private void showGoals() throws IOException {
        FitSyncApp.showGoalScreen();
    }

    @FXML
    private void showReport() throws IOException {
        FitSyncApp.showReportScreen();
    }

    @FXML
    private void showRecommendation() throws IOException {
        FitSyncApp.showRecommendationScreen();
    }
    
    @FXML 
    private void handleLogout() throws IOException {
        currentUser = null;
        FitSyncApp.showLoginScreen();
    }
}
