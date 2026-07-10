package com.fitsync.controller;

import com.fitsync.FitSyncApp;
import com.fitsync.config.AppConfig;
import com.fitsync.service.UserService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class RegisterController implements Initializable {

    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private TextField ageField;
    @FXML private ComboBox<String> genderCombo;
    @FXML private TextField heightField;
    @FXML private TextField weightField;
    @FXML private Label errorLabel;

    private final UserService userService = new UserService();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        genderCombo.setItems(FXCollections.observableArrayList("Male", "Female", "Other"));
    }

    @FXML
    private void handleRegister() {
        String name     = nameField.getText().trim();
        String email    = emailField.getText().trim();
        String password = passwordField.getText().trim();
        String ageText  = ageField.getText().trim();
        String gender   = genderCombo.getValue();
        String heightText = heightField.getText().trim();
        String weightText = weightField.getText().trim();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty() ||
                ageText.isEmpty() || gender == null ||
                heightText.isEmpty() || weightText.isEmpty()) {
            errorLabel.setText("Please fill in all fields.");
            return;
        }

        try {
            int age       = Integer.parseInt(ageText);
            double height = Double.parseDouble(heightText);
            double weight = Double.parseDouble(weightText);

            boolean success = userService.register(name, email, password,
                    age, gender, height, weight);

            if (success) {
                handleLogin();
            } else {
                errorLabel.setText("Email already registered. Please login.");
            }

        } catch (NumberFormatException e) {
            errorLabel.setText("Age, height and weight must be numbers.");
        } catch (IOException e) {
            errorLabel.setText("Navigation error. Please try again.");
        }
    }

    @FXML
    private void handleLogin() throws IOException {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource(AppConfig.FXML_LOGIN)
        );
        Scene scene = new Scene(loader.load());
        FitSyncApp.getPrimaryStage().setScene(scene);
    }
}