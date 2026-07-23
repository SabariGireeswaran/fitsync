package com.fitsync.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import com.fitsync.FitSyncApp;
import com.fitsync.model.User;

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

    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    @Override 
    public void initialize(URL url, ResourceBundle resourceBundle){
        if(currentUser != null){
            welcomeLabel.setText("Welcome back, " + currentUser.getName() + "!");
            weightLabel.setText(currentUser.getWeightKg() + "kg");
        }
        bmiLabel.setText("--");
        bmiCategoryLabel.setText("Not calculated yet");
        workoutCountLabel.setText("0");
    }

    @FXML 
    private void showDashboard() {}
    
    @FXML
    private void showBmi() {
        System.out.println("BMI clicked");
    }

    @FXML
    private void showWorkout() {
        System.out.println("Workout clicked");
    }

    @FXML 
    private void handleLogout() throws IOException {
        currentUser = null;
        FitSyncApp.showLoginScreen();
    }
}
