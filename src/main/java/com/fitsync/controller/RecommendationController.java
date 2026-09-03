package com.fitsync.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import com.fitsync.FitSyncApp;
import com.fitsync.model.User;
import com.fitsync.service.RecommendationService;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

public class RecommendationController implements Initializable {

    @FXML private Label loadingLabel;
    @FXML private TextArea recommendationArea;
    @FXML private Button refreshButton;

    private final RecommendationService recommendationService = new RecommendationService();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadRecommendation();
    }

    @FXML
    private void handleRefresh() {
        loadRecommendation();
    }

    /**
     * Runs the (blocking) API call on a background JavaFX Task so the UI
     * thread stays responsive while the recommendation is fetched.
     */
    private void loadRecommendation() {
        User currentUser = DashboardController.getCurrentUser();
        if (currentUser == null) {
            loadingLabel.setVisible(false);
            recommendationArea.setText("Session expired. Please login again.");
            return;
        }

        loadingLabel.setText("Loading AI recommendation...");
        loadingLabel.setVisible(true);
        recommendationArea.clear();
        refreshButton.setDisable(true);

        Task<String> task = new Task<>() {
            @Override
            protected String call() {
                return recommendationService.getRecommendation(currentUser);
            }
        };

        task.setOnSucceeded(event -> {
            recommendationArea.setText(task.getValue());
            loadingLabel.setVisible(false);
            refreshButton.setDisable(false);
        });

        task.setOnFailed(event -> {
            Throwable ex = task.getException();
            recommendationArea.setText("Failed to load recommendation.\n\n"
                    + (ex != null ? ex.getMessage() : "Unknown error"));
            loadingLabel.setVisible(false);
            refreshButton.setDisable(false);
        });

        Thread thread = new Thread(task, "ai-recommendation");
        thread.setDaemon(true);
        thread.start();
    }

    @FXML
    private void handleBack() throws IOException {
        FitSyncApp.showDashboardScreen();
    }
}
