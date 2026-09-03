package com.fitsync.model;

public class WeightEntry {

    private int id;
    private int userId;
    private double weightKg;
    private String recordedAt;

    //Constructor for saving new record (id not yet known)
    public WeightEntry(int userId, double weightKg, String recordedAt) {
        this.userId     = userId;
        this.weightKg   = weightKg;
        this.recordedAt = recordedAt;
    }

    //Constructor for loading from database (id is known)
    public WeightEntry(int id, int userId, double weightKg, String recordedAt) {
        this.id         = id;
        this.userId     = userId;
        this.weightKg   = weightKg;
        this.recordedAt = recordedAt;
    }

    //Getters
    public int getId()             { return id; }
    public int getUserId()         { return userId; }
    public double getWeightKg()    { return weightKg; }
    public String getRecordedAt()  { return recordedAt; }

    //Setters
    public void setId(int id)                { this.id = id; }
    public void setUserId(int userId)        { this.userId = userId; }
    public void setWeightKg(double weightKg) { this.weightKg = weightKg; }
    public void setRecordedAt(String r)      { this.recordedAt = r; }

    @Override
    public String toString() {
        return "WeightEntry{id=" + id +
               ", userId=" + userId +
               ", weightKg=" + weightKg +
               ", recordedAt=" + recordedAt + "}";
    }
}
