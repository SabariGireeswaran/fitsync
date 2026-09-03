package com.fitsync.controller;

import java.io.IOException;

import com.fitsync.FitSyncApp;
import com.fitsync.model.User;
import com.fitsync.service.BmiService;

import javafx.fxml.FXML;
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

    private final BmiService bmiService = new BmiService();

    @FXML 
    private void handleCalculate() {
        String weightText = weightField.getText().trim();
        String heightText = heightField.getText().trim();

        if(weightText.isEmpty() || heightText.isEmpty()) {
            errorLabel.setText("Please enter both weight and height.");
            return;
        }

        try {
            double weight = Double.parseDouble(weightText);
            double height = Double.parseDouble(heightText);

            if(weight<= 0 || height <= 0) {
                errorLabel.setText("weight and height must be greater than zero.");
                return;
            }

            double bmi = bmiService.calculateBmi(weight, height);
            String category = bmiService.classifyBmi(bmi);
            String advice = bmiService.getAdvice(category);

            bmiValueLabel.setText(String.valueOf(bmi));
            bmiCategoryLabel.setText(category);
            bmiAdviceLabel.setText(advice);
            resultBox.setVisible(true);
            errorLabel.setText("");

            User currentUser = DashboardController.getCurrentUser();
            if (currentUser != null) {
                boolean saved = bmiService.saveBmiRecord(
                        currentUser.getId(), bmi, category);
                if (saved) {
                    currentUser.setWeightKg(weight);
                }
            }
        } catch (NumberFormatException e) {
            errorLabel.setText("Please enter valid numbers");
        }
    }
    @FXML
        private void handleBack() throws IOException {
            FitSyncApp.showDashboardScreen();
        }
}
