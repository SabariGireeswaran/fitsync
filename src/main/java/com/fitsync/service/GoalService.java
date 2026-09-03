package com.fitsync.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.fitsync.dao.GoalDao;
import com.fitsync.model.Goal;

public class GoalService {

    private final GoalDao goalDao;

    public GoalService() {
        this.goalDao = new GoalDao();
    }

    public boolean createGoal(int userId, String goalDescription,
                              double targetValue, double currentValue) {
        if (goalDescription == null || goalDescription.trim().isEmpty()) {
            return false;
        }

        if (targetValue <= 0 || currentValue < 0) {
            return false;
        }

        String createdAt = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        Goal goal = new Goal(
                userId,
                goalDescription.trim(),
                targetValue,
                currentValue,
                createdAt
        );
        return goalDao.save(goal);
    }

    public List<Goal> getGoals(int userId) {
        return goalDao.findByUserId(userId);
    }

    public boolean updateProgress(int goalId, double currentValue) {
        if (currentValue < 0) {
            return false;
        }
        return goalDao.updateProgress(goalId, currentValue);
    }
}
