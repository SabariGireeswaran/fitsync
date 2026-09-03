package com.fitsync.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import com.fitsync.FitSyncApp;
import com.fitsync.model.User;
import com.fitsync.service.RecommendationService;
import com.fitsync.util.AlertUtil;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextArea;

public class RecommendationController implements Initializable {

    @FXML private Label loadingLabel;
    @FXML private ProgressIndicator loadingIndicator;
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
            setLoading(false);
            recommendationArea.setText("Session expired. Please login again.");
            return;
        }

        setLoading(true);
        recommendationArea.clear();

        Task<String> task = new Task<>() {
            @Override
            protected String call() {
                return recommendationService.getRecommendation(
                        currentUser,
                        progress -> Platform.runLater(() -> {
                            if (loadingLabel != null) {
                                loadingLabel.setText(progress);
                            }
                        }));
            }
        };

        task.setOnSucceeded(event -> {
            recommendationArea.setText(task.getValue());
            setLoading(false);
        });

        task.setOnFailed(event -> {
            Throwable ex = task.getException();
            recommendationArea.setText("Failed to load recommendation.\n\n"
                    + (ex != null ? ex.getMessage() : "Unknown error"));
            setLoading(false);
            AlertUtil.showError("AI Advisor",
                    "The recommendation could not be loaded. Please try again.");
        });

        Thread thread = new Thread(task, "ai-recommendation");
        thread.setDaemon(true);
        thread.start();
    }

    private void setLoading(boolean loading) {
        if (loadingLabel != null) {
            loadingLabel.setText(loading ? "Loading AI recommendation..." : "");
            loadingLabel.setVisible(loading);
            loadingLabel.setManaged(loading);
        }
        if (loadingIndicator != null) {
            loadingIndicator.setVisible(loading);
            loadingIndicator.setManaged(loading);
        }
        if (refreshButton != null) {
            refreshButton.setDisable(loading);
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
}
