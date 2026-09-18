package com.fitsync.config;
public final class AppConfig{

    private AppConfig(){}

    //Application identity
    public static final String APP_NAME     = "FitSync";
    public static final String APP_VERSION  = "5.0.0";

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
    public static final String FXML_BMI        = "/com/fitsync/fxml/bmi.fxml";
    public static final String FXML_WORKOUT    = "/com/fitsync/fxml/workout.fxml";
    public static final String FXML_WEIGHT     = "/com/fitsync/fxml/weight.fxml";
    public static final String FXML_GOAL       = "/com/fitsync/fxml/goal.fxml";
    public static final String FXML_REPORT     = "/com/fitsync/fxml/report.fxml";
    public static final String FXML_RECOMMENDATION = "/com/fitsync/fxml/recommendation.fxml";

    //CSS path
    public static final String CSS_MAIN = "/com/fitsync/css/style.css";

    //App icon (window/taskbar icon; also used as the jpackage installer icon)
    public static final String APP_ICON = "/icons/fitsync.png";

    //Google Gemini API (AI Wellness Advisor)
    public static final String GEMINI_API_KEY = System.getenv("GEMINI_API_KEY") != null
            ? System.getenv("GEMINI_API_KEY").trim()
            : "your-gemini-key-here";
    public static final String GEMINI_API_URL =
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-flash-latest:generateContent";
    // Backup model, tried when the primary returns HTTP 503/429.
    public static final String GEMINI_API_URL_FALLBACK =
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-flash-lite-latest:generateContent";

    //Groq API (backup AI provider - used only if Gemini is not configured or
    //fails outright after exhausting its own retries/fallback model)
    public static final String GROQ_API_KEY = System.getenv("GROQ_API_KEY") != null
            ? System.getenv("GROQ_API_KEY").trim()
            : "your-groq-key-here";
    public static final String GROQ_API_URL =
            "https://api.groq.com/openai/v1/chat/completions";
    public static final String GROQ_MODEL = "openai/gpt-oss-120b";

    //BMI thresholds (WHO standard)
    public static final double BMI_UNDERWEIGHT        = 18.5;
    public static final double BMI_NORMAL_MAX         = 24.9;
    public static final double BMI_OVERWEIGHT_MAX     = 29.9;
}