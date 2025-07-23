package com.eat.jani.repositories.health

import com.eat.jani.api.health.HealthApiService
import com.eat.jani.models.calculations.DietPlanRequest
import com.eat.jani.models.calculations.DietPlanResponse
import com.eat.jani.models.calculations.HealthRequest
import com.eat.jani.models.calculations.HealthResponse
import com.eat.jani.models.calculations.ProgressResponse
import retrofit2.Response

class HealthRepository(private val api: HealthApiService) {
    suspend fun updateHealthInformation(request: HealthRequest) : Response<HealthResponse> {
        return api.updateHealthInformation(request)
    }

    suspend fun getHealthInfo() : Response<HealthResponse> {
        return api.fetchHealthInfo()
    }

    suspend fun planUserDiet(request: DietPlanRequest) : Response<List<DietPlanResponse>?> {
        return api.planUserDiet(request)
    }

    suspend fun checkPlanProgress() : Response<List<ProgressResponse>?> {
        return api.checkPlanProgress()
    }

    suspend fun getMealTimes(): Response<List<String>> {
        return api.getMealTimes()
    }

    suspend fun sendMealNotification(token: String): Response<Unit> {
        return api.sendMealNotification(token)
    }
}