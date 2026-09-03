package com.fitsync.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import com.fitsync.FitSyncApp;
import com.fitsync.service.UserService;
import com.fitsync.util.AlertUtil;
import com.fitsync.util.ValidationUtil;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class RegisterController implements Initializable {

    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private TextField ageField;
    @FXML private ComboBox<String> genderCombo;
    @FXML private TextField heightField;
    @FXML private TextField weightField;
    @FXML private Label errorLabel;
    @FXML private Button registerButton;

    private final UserService userService = new UserService();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        genderCombo.setItems(FXCollections.observableArrayList("Male", "Female", "Other"));
    }

    @FXML
    private void handleRegister() {
        String name       = text(nameField);
        String email      = text(emailField);
        String password   = passwordField.getText() == null ? "" : passwordField.getText();
        String ageText    = text(ageField);
        String gender     = genderCombo.getValue();
        String heightText = text(heightField);
        String weightText = text(weightField);

        String problem = validate(name, email, password, ageText, gender, heightText, weightText);
        if (problem != null) {
            errorLabel.setText(problem);
            AlertUtil.showError("Registration Error", problem);
            return;
        }

        setBusy(true);
        try {
            boolean success = userService.register(name, email, password,
                    Integer.parseInt(ageText), gender,
                    Double.parseDouble(heightText), Double.parseDouble(weightText));

            if (success) {
                AlertUtil.showSuccess("Welcome to FitSync",
                        "Your account has been created. Please sign in to continue.");
                FitSyncApp.showLoginScreen();
            } else {
                errorLabel.setText("Email already registered. Please login.");
                AlertUtil.showError("Registration Error",
                        "That email is already registered. Please sign in instead.");
            }
        } catch (IOException e) {
            AlertUtil.showError("Navigation Error",
                    "Account created, but the login screen could not be opened: " + e.getMessage());
        } finally {
            setBusy(false);
        }
    }

    @FXML
    private void handleLogin() {
        try {
            FitSyncApp.showLoginScreen();
        } catch (IOException e) {
            AlertUtil.showError("Navigation Error",
                    "Could not open the login screen: " + e.getMessage());
        }
    }

    private String validate(String name, String email, String password,
                            String ageText, String gender,
                            String heightText, String weightText) {
        if (!ValidationUtil.isNotEmpty(name)
                || !ValidationUtil.isNotEmpty(email)
                || !ValidationUtil.isNotEmpty(password)
                || !ValidationUtil.isNotEmpty(ageText)
                || gender == null
                || !ValidationUtil.isNotEmpty(heightText)
                || !ValidationUtil.isNotEmpty(weightText)) {
            return "Please fill in every field.";
        }
        if (!ValidationUtil.isValidEmail(email)) {
            return "Please enter a valid email address.";
        }
        if (!ValidationUtil.isValidPassword(password)) {
            return "Password must be at least "
                    + ValidationUtil.MIN_PASSWORD_LENGTH + " characters long.";
        }
        if (!ValidationUtil.isPositiveNumber(ageText) || !isInteger(ageText)) {
            return "Age must be a whole number greater than zero.";
        }
        if (!ValidationUtil.isPositiveNumber(heightText)) {
            return "Height must be a number greater than zero.";
        }
        if (!ValidationUtil.isPositiveNumber(weightText)) {
            return "Weight must be a number greater than zero.";
        }
        return null;
    }

    private boolean isInteger(String text) {
        try {
            Integer.parseInt(text.trim());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private String text(TextField field) {
        return field.getText() == null ? "" : field.getText().trim();
    }

    private void setBusy(boolean busy) {
        if (registerButton != null) {
            registerButton.setDisable(busy);
            registerButton.setText(busy ? "Creating account..." : "Create Account");
        }
    }
}
