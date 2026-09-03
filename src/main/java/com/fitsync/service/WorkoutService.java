package com.fitsync.service;

import com.fitsync.dao.WorkoutDao;
import com.fitsync.model.WorkoutLog;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class WorkoutService {
    
    private final WorkoutDao workoutDao;

    public WorkoutService() {
        this.workoutDao = new WorkoutDao();
    }

    public boolean logWorkout(int userId, String exerciseType,
                            int durationMinutes, double caloriesBurned) {
        if (exerciseType == null  ||  exerciseType.trim().isEmpty()) {
            return false;
        }

        if (durationMinutes <= 0  ||  caloriesBurned < 0) {
            return false;
        }

        String loggedAt = LocalDateTime.now().
                format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        WorkoutLog log = new WorkoutLog(
                userId,
                exerciseType.trim(),
                durationMinutes,
                caloriesBurned,
                loggedAt
        );
        return workoutDao.save(log);
    }

    public List<WorkoutLog> getWorkoutHistory(int userId) {
        return workoutDao.findByUserId(userId);
    }

    public int getTotalWorkouts(int userId) {
        return workoutDao.countByUserId(userId);
    }

    public double getAverageCalories(int userId) {
        double avg = workoutDao.averageCaloriesByUserId(userId);
        return Math.round(avg * 100.0) / 100.0;
    }
    
}
