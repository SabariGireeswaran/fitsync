package com.fitsync.controller;

import com.fitsync.FitSyncApp;
import com.fitsync.model.Goal;
import com.fitsync.model.User;
import com.fitsync.service.GoalService;
import javafx.beans.property.SimpleDoubleProperty;
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

public class GoalController implements Initializable {

    @FXML private TextField descriptionField;
    @FXML private TextField targetField;
    @FXML private TextField currentField;
    @FXML private Label errorLabel;
    @FXML private Label successLabel;
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

        loadGoals();
    }

    @FXML
    private void handleAddGoal() {
        String description = descriptionField.getText().trim();
        String targetText = targetField.getText().trim();
        String currentText = currentField.getText().trim();

        if (description.isEmpty() || targetText.isEmpty() || currentText.isEmpty()) {
            errorLabel.setText("Please fill in all fields.");
            successLabel.setText("");
            return;
        }

        try {
            double target = Double.parseDouble(targetText);
            double current = Double.parseDouble(currentText);

            User currentUser = DashboardController.getCurrentUser();
            if (currentUser == null) {
                errorLabel.setText("Session expired. Please login again.");
                return;
            }

            boolean success = goalService.createGoal(
                    currentUser.getId(), description, target, current);

            if (success) {
                successLabel.setText("Goal added successfully!");
                errorLabel.setText("");
                descriptionField.clear();
                targetField.clear();
                currentField.clear();
                loadGoals();
            } else {
                errorLabel.setText("Target must be greater than zero and current cannot be negative.");
                successLabel.setText("");
            }

        } catch (NumberFormatException e) {
            errorLabel.setText("Target and current values must be numbers.");
            successLabel.setText("");
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
    private void handleBack() throws IOException {
        FitSyncApp.showDashboardScreen();
    }
}
