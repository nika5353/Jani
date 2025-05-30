package com.eat.jani.models.calculations

import com.eat.jani.models.enums.ActivityLevel
import com.eat.jani.models.enums.DietDistribution
import com.eat.jani.models.enums.Gender
import com.eat.jani.models.enums.PlanType

data class HealthRequest(
    val age: Int,
    val gender: Gender,
    val height: Double,
    val weight: Double,
    val activityLevel: ActivityLevel,
    val averageStepsPerDay: Int,
    val waist: Double? = null,
    val neck: Double? = null,
    val hips: Double? = null
)

data class DietPlanRequest(
    val planType: PlanType,
    val targetWeight: Double,
    val timeFrame: Int, // 1, 3, or 6
    val mealsPerDay: Int, // range(1, 5)
    val doesPhysicalActivity: Boolean,
    val activityTime: String?,
    val dietDistribution: DietDistribution
)

/*
    // Health
    {
      "age": 21,
      "gender": "Male",
      "height": 170,
      "weight": 51,
      "activityLevel": "Sedentary",
      "averageStepsPerDay": 0,
      "waist": null,
      "neck": null,
      "hips": null
    }
    // Diet Plan
    {
      "planType": "Loss",
      "targetWeight": 500,
      "timeFrame": 6,
      "mealsPerDay": 5,
      "doesPhysicalActivity": true,
      "activityTime": "string"
    }
*/