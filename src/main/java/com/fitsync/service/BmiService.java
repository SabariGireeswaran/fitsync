package com.fitsync.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import com.fitsync.config.AppConfig;
import com.fitsync.dao.BmiDao;
import com.fitsync.model.BmiRecord;

public class BmiService {

    private final BmiDao bmiDao = new BmiDao();


    public double calculateBmi(double weightkg, double heightCm){
        double heightM = heightCm / 100.0;
        double bmi = weightkg / ( heightM * heightM );
        // Round to 2 decimal places
        return Math.round(bmi * 100.0) / 100.0;
    }

    public String classifyBmi(double bmi) {
        if (bmi < AppConfig.BMI_UNDERWEIGHT){
            return "Underweight";
        } else if (bmi <= AppConfig.BMI_NORMAL_MAX){
            return "Normal";
        } else if (bmi <= AppConfig.BMI_OVERWEIGHT_MAX){
            return "Overweight";
        } else {
            return "Obese";
        }
    }

    public String getAdvice(String category) {
        switch(category) {
            case "Underweight":
                return "Consider increasing calorie intake and strength training.";
            case "Normal":
                return "Great! Maintain your current lifestyle and exercise regularly.";
            case "Overweight":
                return "Consider cardio exercises and a balanced diet.";
            default:
                return "Consult a healthcare professional for a personalised plan.";
        }
    }

    public boolean saveBmiRecord(int userId, double bmi, String category) {
        String recordedAt = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        BmiRecord record = new BmiRecord(userId, bmi, category, recordedAt);
        return bmiDao.save(record);
    }

    public Optional<BmiRecord> getLatestBmi(int userId) {
        return bmiDao.findLatestByUserId(userId);
    }

    public List<BmiRecord> getBmiHistory(int userId) {
        return bmiDao.findByUserId(userId);
    }
}
