package com.fitsync.service;

import com.fitsync.config.AppConfig;

public class BmiService {
    
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
                return "";
        }
    }
}
