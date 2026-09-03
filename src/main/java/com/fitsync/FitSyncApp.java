package com.fitsync;

import com.fitsync.config.AppConfig;
import com.fitsync.dao.DatabaseManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class FitSyncApp extends Application {

    private static Stage primaryStage;

    @Override
    public void init() throws Exception {
        //Runs before the UI appears
        //We will initialize the database here in the next step
        System.out.println("FitSync starting...");
        DatabaseManager.getInstance();
    }
    @Override
    public void start(Stage stage) throws IOException{
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
    public void stop() throws Exception{
        System.out.println("FitSync shutting down...");
    }

    public static void showLoginScreen() throws IOException{
        FXMLLoader loader = new FXMLLoader(
                FitSyncApp.class.getResource(AppConfig.FXML_LOGIN));
        Scene scene = new Scene(loader.load());
        primaryStage.setScene((scene));
    }

    public static Stage getPrimaryStage(){
        return primaryStage;
    }

}
