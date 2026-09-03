package com.fitsync.service;

import java.util.Optional;
import java.util.function.Consumer;

import com.fitsync.model.BmiRecord;
import com.fitsync.model.User;
import com.fitsync.util.ApiClient;
import com.fitsync.util.PromptBuilder;

/**
 * Gathers the current user's wellness stats from the database, builds a
 * prompt, and asks the AI model for a personalised recommendation.
 */
public class RecommendationService {

    private final ApiClient apiClient = new ApiClient();
    private final PromptBuilder promptBuilder = new PromptBuilder();
    private final BmiService bmiService = new BmiService();
    private final WorkoutService workoutService = new WorkoutService();

    public String getRecommendation(User user) {
        return getRecommendation(user, msg -> { });
    }

    public String getRecommendation(User user, Consumer<String> onProgress) {
        if (user == null) {
            return "No user is currently logged in.";
        }

        double bmi;
        String category;

        Optional<BmiRecord> latestBmi = bmiService.getLatestBmi(user.getId());
        if (latestBmi.isPresent()) {
            bmi = latestBmi.get().getBmiValue();
            category = latestBmi.get().getCategory();
        } else if (user.getHeightCm() > 0 && user.getWeightKg() > 0) {
            bmi = bmiService.calculateBmi(user.getWeightKg(), user.getHeightCm());
            category = bmiService.classifyBmi(bmi);
        } else {
            bmi = 0.0;
            category = "Unknown";
        }

        int totalWorkouts = workoutService.getTotalWorkouts(user.getId());
        double avgCalories = workoutService.getAverageCalories(user.getId());

        String prompt = promptBuilder.buildWellnessPrompt(
                user, bmi, category, totalWorkouts, avgCalories);

        return apiClient.getRecommendation(prompt, onProgress);
    }
}
