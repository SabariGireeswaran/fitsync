package com.fitsync.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import com.fitsync.FitSyncApp;
import com.fitsync.model.BmiRecord;
import com.fitsync.model.User;
import com.fitsync.service.BmiService;
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
        weightLabel.setText(currentUser.getWeightKg() + " kg");

        Optional<BmiRecord> latestBmi = bmiService.getLatestBmi(currentUser.getId());
        if (latestBmi.isPresent()) {
            BmiRecord record = latestBmi.get();
            bmiLabel.setText(String.valueOf(record.getBmiValue()));
            bmiCategoryLabel.setText(record.getCategory());
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
    private void handleLogout() throws IOException {
        currentUser = null;
        FitSyncApp.showLoginScreen();
    }
}
