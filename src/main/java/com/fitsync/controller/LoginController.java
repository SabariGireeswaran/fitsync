package com.fitsync.controller;

import java.io.IOException;
import java.util.Optional;

import com.fitsync.FitSyncApp;
import com.fitsync.model.User;
import com.fitsync.service.UserService;
import com.fitsync.util.AlertUtil;
import com.fitsync.util.ValidationUtil;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;
    @FXML private Button loginButton;

    private final UserService userService = new UserService();

    @FXML
    private void handleLogin() {
        String email    = emailField.getText() == null ? "" : emailField.getText().trim();
        String password = passwordField.getText() == null ? "" : passwordField.getText().trim();

        if (!ValidationUtil.isNotEmpty(email) || !ValidationUtil.isNotEmpty(password)) {
            fail("Please enter both your email and password.");
            return;
        }
        if (!ValidationUtil.isValidEmail(email)) {
            fail("That does not look like a valid email address.");
            return;
        }

        setBusy(true);
        try {
            Optional<User> result = userService.login(email, password);

            if (result.isPresent()) {
                errorLabel.setText("");
                DashboardController.setCurrentUser(result.get());
                FitSyncApp.showDashboardScreen();
            } else {
                fail("Invalid email or password.");
            }
        } catch (IOException e) {
            AlertUtil.showError("Navigation Error",
                    "Could not open the dashboard: " + e.getMessage());
        } finally {
            setBusy(false);
        }
    }

    @FXML
    private void handleRegister() {
        try {
            FitSyncApp.showRegisterScreen();
        } catch (IOException e) {
            AlertUtil.showError("Navigation Error",
                    "Could not open the registration screen: " + e.getMessage());
        }
    }

    private void fail(String message) {
        errorLabel.setText(message);
        AlertUtil.showError("Login Failed", message);
    }

    private void setBusy(boolean busy) {
        if (loginButton != null) {
            loginButton.setDisable(busy);
            loginButton.setText(busy ? "Signing in..." : "Login");
        }
    }
}
