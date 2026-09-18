package com.fitsync;

import com.fitsync.config.AppConfig;
import com.fitsync.dao.DatabaseManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class FitSyncApp extends Application {

    private static Stage primaryStage;

    /**
     * The single Scene reused for every screen. Screens are switched by
     * swapping this Scene's root node (see {@link #swapScene}) rather than
     * replacing the Scene itself, so a maximized window stays maximized
     * instead of flashing back to its unmaximized size on every navigation.
     */
    private static Scene primaryScene;

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

        URL iconUrl = FitSyncApp.class.getResource(AppConfig.APP_ICON);
        if (iconUrl != null) {
            stage.getIcons().add(new Image(iconUrl.toExternalForm()));
        }

        primaryScene = new Scene(loadFxml(AppConfig.FXML_LOGIN));
        applyStylesheet(primaryScene);
        stage.setScene(primaryScene);
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
     * Loads an FXML file and swaps it in as the root of the shared
     * {@link #primaryScene}. Reusing the same Scene (instead of building a
     * new one per screen) avoids a JavaFX/Windows quirk where assigning a
     * fresh Scene to a maximized Stage briefly collapses the window to its
     * unmaximized size - visible as a "minimize then blank" flash - before
     * re-maximizing.
     */
    private static void swapScene(String fxmlPath) throws IOException {
        Parent root = loadFxml(fxmlPath);
        if (primaryScene == null) {
            primaryScene = new Scene(root);
            applyStylesheet(primaryScene);
            primaryStage.setScene(primaryScene);
        } else {
            primaryScene.setRoot(root);
        }
    }

    private static Parent loadFxml(String fxmlPath) throws IOException {
        FXMLLoader loader = new FXMLLoader(FitSyncApp.class.getResource(fxmlPath));
        return loader.load();
    }

    private static void applyStylesheet(Scene scene) {
        URL css = FitSyncApp.class.getResource(AppConfig.CSS_MAIN);
        if (css != null) {
            scene.getStylesheets().add(css.toExternalForm());
        }
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }
}
