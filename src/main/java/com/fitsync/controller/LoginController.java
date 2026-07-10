package com.fitsync.controller;

import com.fitsync.FitSyncApp;
import com.fitsync.config.AppConfig;
import com.fitsync.model.User;
import com.fitsync.service.UserService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.util.Optional;

public class LoginController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    private final UserService userService = new UserService();

    @FXML
    private void handleLogin() {
        String email    = emailField.getText().trim();
        String password = passwordField.getText().trim();

        if (email.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Please enter email and password.");
            return;
        }

        Optional<User> result = userService.login(email, password);

        if (result.isPresent()) {
            errorLabel.setText("");
            System.out.println("Login successful: " + result.get().getName());
            // Dashboard navigation will be added in next step
        } else {
            errorLabel.setText("Invalid email or password.");
        }
    }

    @FXML
    private void handleRegister() throws IOException {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource(AppConfig.FXML_REGISTER)
        );
        Scene scene = new Scene(loader.load());
        FitSyncApp.getPrimaryStage().setScene(scene);
    }
}