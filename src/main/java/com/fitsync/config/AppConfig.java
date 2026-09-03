package com.fitsync.config;
public final class AppConfig{

    private AppConfig(){}

    //Application identity
    public static final String APP_NAME     = "FitSync";
    public static final String APP_VERSION  = "1.0.0";

    //Window dimension
    public static final double WINDOW_WIDTH   = 1100.0;
    public static final double WINDOW_HEIGHT  = 700.0;
    public static final double MIN_WIDTH      = 800.0;
    public static final double MIN_HEIGHT     = 550.0;

    //Database path
    public static final String DB_DIR  = System.getProperty("user.home") + "/.fitsync";
    public static final String DB_PATH = DB_DIR + "/fitsync.db";

    //FXML screen paths(classpath-relative)
    public static final String FXML_LOGIN      = "/com/fitsync/fxml/login.fxml";
    public static final String FXML_REGISTER   = "/com/fitsync/fxml/register.fxml";
    public static final String FXML_DASHBOARD  = "/com/fitsync/fxml/dashboard.fxml";

    //CSS path
    public static final String CSS_MAIN = "/com/fitsync/css/style.css";

    //BMI thresholds (WHO standard)
    public static final double BMI_UNDERWEIGHT        = 18.5;
    public static final double BMI_NORMAL_MAX         = 24.9;
    public static final double BMI_OVERWEIGHT_MAX     = 29.9;
}