package com.fitsync.controller;

import com.fitsync.FitSyncApp;
import com.fitsync.model.User;
import com.fitsync.model.WeightEntry;
import com.fitsync.service.WeightService;
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

public class WeightController implements Initializable {

    @FXML private TextField weightField;
    @FXML private Label errorLabel;
    @FXML private Label successLabel;
    @FXML private Button logButton;
    @FXML private TableView<WeightEntry> weightTable;
    @FXML private TableColumn<WeightEntry, Number> weightCol;
    @FXML private TableColumn<WeightEntry, String> dateCol;

    private final WeightService weightService = new WeightService();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        weightCol.setCellValueFactory(data ->
                new SimpleDoubleProperty(data.getValue().getWeightKg()));
        dateCol.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getRecordedAt()));

        weightTable.setPlaceholder(new Label("No weight entries yet."));
        loadWeightHistory();
    }

    @FXML
    private void handleLogWeight() {
        String weightText = weightField.getText() == null ? "" : weightField.getText().trim();

        if (!ValidationUtil.isPositiveNumber(weightText)) {
            reject("Weight must be a number greater than zero.");
            return;
        }

        User currentUser = DashboardController.getCurrentUser();
        if (currentUser == null) {
            AlertUtil.showError("Session Expired", "Please log in again.");
            return;
        }

        setBusy(true);
        try {
            double weight = Double.parseDouble(weightText);
            boolean success = weightService.logWeight(currentUser.getId(), weight);

            if (success) {
                currentUser.setWeightKg(weight);
                successLabel.setText("Weight logged successfully!");
                errorLabel.setText("");
                weightField.clear();
                loadWeightHistory();
                AlertUtil.showSuccess("Weight Logged",
                        weight + " kg has been added to your history.");
            } else {
                reject("Could not log the weight. Please try again.");
            }
        } finally {
            setBusy(false);
        }
    }

    private void loadWeightHistory() {
        User currentUser = DashboardController.getCurrentUser();
        if (currentUser != null) {
            List<WeightEntry> entries = weightService.getWeightHistory(currentUser.getId());
            weightTable.setItems(FXCollections.observableArrayList(entries));
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
