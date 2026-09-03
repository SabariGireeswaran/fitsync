package com.fitsync.model;

public class WorkoutLog {
    
    private int id;
    private int userId;
    private String exerciseType;
    private int durationMinutes;
    private double caloriesBurned;
    private String loggedAt;

    //Constructor for saving new record
    public WorkoutLog(int userId, String exerciseType,
                      int durationMinutes, double caloriesBurned,
                      String loggedAt) {
                    this.userId                 = userId;
                    this.exerciseType           = exerciseType;
                    this.durationMinutes        = durationMinutes;
                    this.caloriesBurned         = caloriesBurned;
                    this.loggedAt               = loggedAt;
    }

    //Constructor for loading from database
    public WorkoutLog(int id, int userId, String exerciseType,
                      int durationMinutes, double caloriesBurned,
                      String loggedAt){
                    this.id                     = id;
                    this.userId                 = userId;
                    this.exerciseType           = exerciseType;
                    this.durationMinutes        = durationMinutes;
                    this.caloriesBurned         = caloriesBurned;
                    this.loggedAt               = loggedAt;
    }

    //Getters
    public int getId()                            { return id; }
    public int getUserId()                        { return userId; }
    public String getExerciseType()               { return exerciseType; }
    public int getDurationMinutes()               { return durationMinutes; }
    public double getCaloriesBurned()             { return caloriesBurned; }
    public String getLoggedAt()                   { return loggedAt; }   
    
    //Setters
    public void setId(int id)                     { this.id = id; }
    public void setUserId(int userId)             { this.userId = userId; }
    public void setExerciseType(String e)         { this.exerciseType = e; }
    public void setDurationMinutes(int d)         { this.durationMinutes = d; }
    public void setcaloriesBurned(double c)       { this.caloriesBurned = c; }    
    public void setLoggedAt(String loggedAt)      { this.loggedAt = loggedAt; }
    
    @Override
    public String toString() {
        return "WorkoutLog(id=" + id +
               ", exerciseType=" + exerciseType +
               ", duration=" + durationMinutes + "mins}"; 
    }
}
