package com.fitsync.controller;

import java.io.IOException;

import com.fitsync.FitSyncApp;
import com.fitsync.model.User;
import com.fitsync.service.BmiService;
import com.fitsync.util.AlertUtil;
import com.fitsync.util.ValidationUtil;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class BmiController {

    @FXML private TextField weightField;
    @FXML private TextField heightField;
    @FXML private Label bmiValueLabel;
    @FXML private Label bmiCategoryLabel;
    @FXML private Label bmiAdviceLabel;
    @FXML private Label errorLabel;
    @FXML private VBox resultBox;
    @FXML private Button calculateButton;

    private final BmiService bmiService = new BmiService();

    @FXML
    private void handleCalculate() {
        String weightText = weightField.getText() == null ? "" : weightField.getText().trim();
        String heightText = heightField.getText() == null ? "" : heightField.getText().trim();

        if (!ValidationUtil.isPositiveNumber(weightText)
                || !ValidationUtil.isPositiveNumber(heightText)) {
            String msg = "Enter weight and height as numbers greater than zero.";
            errorLabel.setText(msg);
            AlertUtil.showError("Invalid Input", msg);
            return;
        }

        setBusy(true);
        try {
            double weight = Double.parseDouble(weightText);
            double height = Double.parseDouble(heightText);

            double bmi = bmiService.calculateBmi(weight, height);
            String category = bmiService.classifyBmi(bmi);
            String advice = bmiService.getAdvice(category);

            bmiValueLabel.setText(String.valueOf(bmi));
            bmiCategoryLabel.setText(category);
            bmiAdviceLabel.setText(advice);
            resultBox.setVisible(true);
            resultBox.setManaged(true);
            errorLabel.setText("");

            User currentUser = DashboardController.getCurrentUser();
            if (currentUser != null) {
                boolean saved = bmiService.saveBmiRecord(currentUser.getId(), bmi, category);
                if (saved) {
                    currentUser.setWeightKg(weight);
                    AlertUtil.showSuccess("BMI Recorded",
                            String.format("Your BMI of %.2f (%s) has been saved to your history.",
                                    bmi, category));
                } else {
                    AlertUtil.showError("Save Failed",
                            "Your BMI was calculated but could not be saved. Please try again.");
                }
            }
        } finally {
            setBusy(false);
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

    private void setBusy(boolean busy) {
        if (calculateButton != null) {
            calculateButton.setDisable(busy);
        }
    }
}
