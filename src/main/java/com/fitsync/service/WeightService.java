package com.fitsync.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.fitsync.dao.WeightDao;
import com.fitsync.model.WeightEntry;

public class WeightService {

    private final WeightDao weightDao;

    public WeightService() {
        this.weightDao = new WeightDao();
    }

    public boolean logWeight(int userId, double weightKg) {
        if (weightKg <= 0) {
            return false;
        }

        String recordedAt = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        WeightEntry entry = new WeightEntry(userId, weightKg, recordedAt);
        return weightDao.save(entry);
    }

    public List<WeightEntry> getWeightHistory(int userId) {
        return weightDao.findByUserId(userId);
    }

    public double getLatestWeight(int userId) {
        List<WeightEntry> history = weightDao.findByUserId(userId);
        if (history.isEmpty()) {
            return 0.0;
        }
        return history.get(0).getWeightKg();
    }
}
