package com.fitsync.controller;

import com.fitsync.FitSyncApp;
import com.fitsync.model.User;
import com.fitsync.model.WeightEntry;
import com.fitsync.service.WeightService;
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

public class WeightController implements Initializable {

    @FXML private TextField weightField;
    @FXML private Label errorLabel;
    @FXML private Label successLabel;
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

        loadWeightHistory();
    }

    @FXML
    private void handleLogWeight() {
        String weightText = weightField.getText().trim();

        if (weightText.isEmpty()) {
            errorLabel.setText("Please enter your weight.");
            successLabel.setText("");
            return;
        }

        try {
            double weight = Double.parseDouble(weightText);

            User currentUser = DashboardController.getCurrentUser();
            if (currentUser == null) {
                errorLabel.setText("Session expired. Please login again.");
                return;
            }

            boolean success = weightService.logWeight(currentUser.getId(), weight);

            if (success) {
                currentUser.setWeightKg(weight);
                successLabel.setText("Weight logged successfully!");
                errorLabel.setText("");
                weightField.clear();
                loadWeightHistory();
            } else {
                errorLabel.setText("Weight must be greater than zero.");
                successLabel.setText("");
            }

        } catch (NumberFormatException e) {
            errorLabel.setText("Weight must be a valid number.");
            successLabel.setText("");
        }
    }

    private void loadWeightHistory() {
        User currentUser = DashboardController.getCurrentUser();
        if (currentUser != null) {
            List<WeightEntry> entries = weightService
                    .getWeightHistory(currentUser.getId());
            weightTable.setItems(
                    FXCollections.observableArrayList(entries));
        }
    }

    @FXML
    private void handleBack() throws IOException {
        FitSyncApp.showDashboardScreen();
    }
}
