package com.fitsync.util;

import com.fitsync.model.User;

/**
 * Assembles the natural-language prompt sent to the AI model for the
 * AI Wellness Advisor, based on the current user's stats.
 */
public class PromptBuilder {

    public String buildWellnessPrompt(User user, double bmi, String bmiCategory,
                                      int totalWorkouts, double avgCalories) {
        StringBuilder sb = new StringBuilder();

        sb.append("You are a professional wellness and fitness advisor. ")
          .append("Based on the following user profile, provide personalised, ")
          .append("practical guidance.\n\n");

        sb.append("USER PROFILE\n");
        sb.append("- Name: ").append(user.getName()).append("\n");
        sb.append("- Age: ").append(user.getAge()).append("\n");
        sb.append("- Gender: ").append(user.getGender()).append("\n");
        sb.append("- Height: ").append(user.getHeightCm()).append(" cm\n");
        sb.append("- Weight: ").append(user.getWeightKg()).append(" kg\n");
        sb.append("- BMI: ").append(bmi).append(" (").append(bmiCategory).append(")\n");
        sb.append("- Total workouts logged: ").append(totalWorkouts).append("\n");
        sb.append("- Average calories burned per workout session: ")
          .append(avgCalories).append("\n\n");

        sb.append("Please provide the following, using clear headings and bullet points:\n");
        sb.append("1. Personalised workout recommendations suited to this person's ")
          .append("age, fitness level and BMI.\n");
        sb.append("2. Diet suggestions appropriate for the ").append(bmiCategory)
          .append(" BMI category.\n");
        sb.append("3. A structured weekly fitness plan, laid out day by day.\n");
        sb.append("4. Health tips that are specific to the ").append(bmiCategory)
          .append(" BMI category.\n\n");

        sb.append("Keep the tone encouraging and supportive. ")
          .append("Do not give medical advice; recommend consulting a professional ")
          .append("where appropriate.");

        return sb.toString();
    }
}
