package com.fitsync;

import com.fitsync.config.AppConfig;
import com.fitsync.dao.DatabaseManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class FitSyncApp extends Application {

    private static Stage primaryStage;

    @Override
    public void init() {
        System.out.println("FitSync starting...");
        DatabaseManager.getInstance();
    }

    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;
        stage.setTitle(AppConfig.APP_NAME + " " + AppConfig.APP_VERSION);
        stage.setWidth(AppConfig.WINDOW_WIDTH);
        stage.setHeight(AppConfig.WINDOW_HEIGHT);
        stage.setMinWidth(AppConfig.MIN_WIDTH);
        stage.setMinHeight(AppConfig.MIN_HEIGHT);
        stage.setResizable(true);
        showLoginScreen();
        stage.show();
    }

    @Override
    public void stop() {
        System.out.println("FitSync shutting down...");
        DatabaseManager.getInstance().closeConnection();
    }

    public static void showLoginScreen()          throws IOException { swapScene(AppConfig.FXML_LOGIN); }
    public static void showDashboardScreen()       throws IOException { swapScene(AppConfig.FXML_DASHBOARD); }
    public static void showBmiScreen()             throws IOException { swapScene(AppConfig.FXML_BMI); }
    public static void showWorkoutScreen()         throws IOException { swapScene(AppConfig.FXML_WORKOUT); }
    public static void showWeightScreen()          throws IOException { swapScene(AppConfig.FXML_WEIGHT); }
    public static void showGoalScreen()            throws IOException { swapScene(AppConfig.FXML_GOAL); }
    public static void showReportScreen()          throws IOException { swapScene(AppConfig.FXML_REPORT); }
    public static void showRegisterScreen()        throws IOException { swapScene(AppConfig.FXML_REGISTER); }
    public static void showRecommendationScreen()  throws IOException { swapScene(AppConfig.FXML_RECOMMENDATION); }

    /**
     * Loads an FXML file, wraps it in a Scene with the shared stylesheet
     * attached, and installs it on the primary stage.
     */
    private static void swapScene(String fxmlPath) throws IOException {
        FXMLLoader loader = new FXMLLoader(FitSyncApp.class.getResource(fxmlPath));
        Parent root = loader.load();

        Scene scene = new Scene(root);
        URL css = FitSyncApp.class.getResource(AppConfig.CSS_MAIN);
        if (css != null) {
            scene.getStylesheets().add(css.toExternalForm());
        }
        primaryStage.setScene(scene);
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }
}
