package com.fitsync.model;

public class Goal {

    private int id;
    private int userId;
    private String goalDescription;
    private double targetValue;
    private double currentValue;
    private String createdAt;

    //Constructor for saving new record (id not yet known)
    public Goal(int userId, String goalDescription,
                double targetValue, double currentValue, String createdAt) {
        this.userId          = userId;
        this.goalDescription = goalDescription;
        this.targetValue     = targetValue;
        this.currentValue    = currentValue;
        this.createdAt       = createdAt;
    }

    //Constructor for loading from database (id is known)
    public Goal(int id, int userId, String goalDescription,
                double targetValue, double currentValue, String createdAt) {
        this.id              = id;
        this.userId          = userId;
        this.goalDescription = goalDescription;
        this.targetValue     = targetValue;
        this.currentValue    = currentValue;
        this.createdAt       = createdAt;
    }

    //Getters
    public int getId()                   { return id; }
    public int getUserId()               { return userId; }
    public String getGoalDescription()   { return goalDescription; }
    public double getTargetValue()       { return targetValue; }
    public double getCurrentValue()      { return currentValue; }
    public String getCreatedAt()         { return createdAt; }

    //Setters
    public void setId(int id)                     { this.id = id; }
    public void setUserId(int userId)             { this.userId = userId; }
    public void setGoalDescription(String d)      { this.goalDescription = d; }
    public void setTargetValue(double t)          { this.targetValue = t; }
    public void setCurrentValue(double c)         { this.currentValue = c; }
    public void setCreatedAt(String c)            { this.createdAt = c; }

    public double getProgressPercent() {
        if (targetValue == 0) {
            return 0.0;
        }
        double percent = (currentValue / targetValue) * 100.0;
        return Math.round(percent * 100.0) / 100.0;
    }

    @Override
    public String toString() {
        return "Goal{id=" + id +
               ", userId=" + userId +
               ", goalDescription=" + goalDescription +
               ", target=" + targetValue +
               ", current=" + currentValue + "}";
    }
}
