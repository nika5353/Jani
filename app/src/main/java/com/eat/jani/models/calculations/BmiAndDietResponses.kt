package com.eat.jani.models.calculations

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.LocalTime

data class HealthResponse(
    val bmi: Double,
    val maintenanceCalories: Int,
    val healthStatus: String,
    val ffmi: Double,
    val bodyFatPercentage: Double,
    val weight: Double
)

data class DietPlanResponse(
    val weekNumber: Int,
    val calories: Int,
    val proteinInGrams: Int,
    val carbsInGrams: Int,
    val fatsInGrams: Int,
    val mealPlan: MealPlan
)

data class MealPlan(
    val mealCount: Int,
    val meals: List<Meal>
)

data class Meal(
    val protein: Int,
    val carbs: Int,
    val fat: Int,
    val mealCalories: Int,
    val time: String,
    val label: String
)

@Parcelize
data class ProgressResponse(
    val weekNumber: Int,
    val calories: Int,
    val proteinInGrams: Int,
    val carbsInGrams: Int,
    val fatsInGrams: Int,
) : Parcelable

/*
    // Heath
    {
      "bmi": 18.5,
      "maintenanceCalories": 1804,
      "healthStatus": "Normal",
      "ffmi": 16,
      "bodyFatPercentage": 10.8
    }
    {
      "bmi": 18.1,
      "maintenanceCalories": 1773,
      "healthStatus": "Underweight",
      "ffmi": 15.8,
      "bodyFatPercentage": 10.4
    }
    // Diet PLan
    დასამთავრებელია
*/