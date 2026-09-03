package com.fitsync.controller;

import com.fitsync.FitSyncApp;
import com.fitsync.model.Goal;
import com.fitsync.model.User;
import com.fitsync.service.GoalService;
import com.fitsync.util.AlertUtil;
import com.fitsync.util.ValidationUtil;
import javafx.beans.property.SimpleDoubleProperty;
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

public class GoalController implements Initializable {

    @FXML private TextField descriptionField;
    @FXML private TextField targetField;
    @FXML private TextField currentField;
    @FXML private Label errorLabel;
    @FXML private Label successLabel;
    @FXML private Button addButton;
    @FXML private TableView<Goal> goalTable;
    @FXML private TableColumn<Goal, String> descriptionCol;
    @FXML private TableColumn<Goal, Number> targetCol;
    @FXML private TableColumn<Goal, Number> currentCol;
    @FXML private TableColumn<Goal, Number> progressCol;
    @FXML private TableColumn<Goal, String> dateCol;

    private final GoalService goalService = new GoalService();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        descriptionCol.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getGoalDescription()));
        targetCol.setCellValueFactory(data ->
                new SimpleDoubleProperty(data.getValue().getTargetValue()));
        currentCol.setCellValueFactory(data ->
                new SimpleDoubleProperty(data.getValue().getCurrentValue()));
        progressCol.setCellValueFactory(data ->
                new SimpleDoubleProperty(data.getValue().getProgressPercent()));
        dateCol.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getCreatedAt()));

        goalTable.setPlaceholder(new Label("No goals set yet."));
        loadGoals();
    }

    @FXML
    private void handleAddGoal() {
        String description = descriptionField.getText() == null ? "" : descriptionField.getText().trim();
        String targetText = targetField.getText() == null ? "" : targetField.getText().trim();
        String currentText = currentField.getText() == null ? "" : currentField.getText().trim();

        if (!ValidationUtil.isNotEmpty(description)) {
            reject("Please describe your goal.");
            return;
        }
        if (!ValidationUtil.isPositiveNumber(targetText)) {
            reject("Target value must be a number greater than zero.");
            return;
        }
        if (!ValidationUtil.isNotEmpty(currentText) || parseOrNegative(currentText) < 0) {
            reject("Current value must be zero or a positive number.");
            return;
        }

        User currentUser = DashboardController.getCurrentUser();
        if (currentUser == null) {
            AlertUtil.showError("Session Expired", "Please log in again.");
            return;
        }

        setBusy(true);
        try {
            double target = Double.parseDouble(targetText);
            double current = Double.parseDouble(currentText);

            boolean success = goalService.createGoal(
                    currentUser.getId(), description, target, current);

            if (success) {
                successLabel.setText("Goal added successfully!");
                errorLabel.setText("");
                descriptionField.clear();
                targetField.clear();
                currentField.clear();
                loadGoals();
                AlertUtil.showSuccess("Goal Added", "\"" + description + "\" is now being tracked.");
            } else {
                reject("Could not add the goal. Please try again.");
            }
        } finally {
            setBusy(false);
        }
    }

    private double parseOrNegative(String text) {
        try {
            return Double.parseDouble(text.trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private void loadGoals() {
        User currentUser = DashboardController.getCurrentUser();
        if (currentUser != null) {
            List<Goal> goals = goalService.getGoals(currentUser.getId());
            goalTable.setItems(FXCollections.observableArrayList(goals));
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
        if (addButton != null) {
            addButton.setDisable(busy);
        }
    }
}
