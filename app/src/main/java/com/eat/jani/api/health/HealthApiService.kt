package com.eat.jani.api.health

import com.eat.jani.models.calculations.DietPlanRequest
import com.eat.jani.models.calculations.DietPlanResponse
import com.eat.jani.models.calculations.HealthRequest
import com.eat.jani.models.calculations.HealthResponse
import com.eat.jani.models.calculations.ProgressResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface HealthApiService {
    @POST("BmiAndCalories/update-health-infomration")
    suspend fun updateHealthInformation(@Body request: HealthRequest) : Response<HealthResponse>
    @GET("DietPlan/user-health-info")
    suspend fun fetchHealthInfo() : Response<HealthResponse>
    @POST("DietPlan/setup")
    suspend fun planUserDiet(@Body request: DietPlanRequest) : Response<List<DietPlanResponse>?>
    @GET("DietPlan/check-progress")
    suspend fun checkPlanProgress() : Response<List<ProgressResponse>?>
}