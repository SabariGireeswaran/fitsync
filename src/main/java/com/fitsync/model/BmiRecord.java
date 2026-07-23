package com.fitsync.model;

public class BmiRecord {
    private int id;
    private int userId;
    private double bmiValue;
    private String category;
    private String recordedAt;

    //Constructor for saving new record(id not yet known)
    public BmiRecord(int userId, double bmiValue, String category, String recordedAt){
        this.userId = userId;
        this.bmiValue = bmiValue;
        this.category = category;
        this.recordedAt = recordedAt;
    }

    //Constructor for loading from database(id is known)
    public BmiRecord(int id, int userId, double bmiValue, String category, String recordedAt){
        this.id = id;
        this.userId = userId;
        this.bmiValue = bmiValue;
        this.category = category;
        this.recordedAt = recordedAt;
    }

    //Getters
    public int getId()              { return id; }
    public int getUserId()          { return userId; }
    public double getBmiValue()     { return bmiValue; }
    public String getCategory()     { return category; }
    public String getRecordedAt()   { return recordedAt; }

    //Setters
    public void setId(int id)                   { this.id = id; }
    public void setUserId(int userId)           { this.userId =  userId; }
    public void setBmiValue(double bmiValue)    { this.bmiValue = bmiValue; }
    public void setCategory(String category)    { this.category = category; }
    public void setRecordedAt(String r)         { this.recordedAt = r; }

    @Override
    public String toString() {
        return "BmiRecord{id=" + id +
               ", userId=" + userId +
               ", bmiValue+" + bmiValue +
               ", category=" + category + "}";
    }
}
