package com.eat.jani.viewmodels.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eat.jani.models.calculations.DietPlanRequest
import com.eat.jani.models.calculations.DietPlanResponse
import com.eat.jani.models.calculations.HealthResponse
import com.eat.jani.models.calculations.ProgressResponse
import com.eat.jani.models.enums.DietDistribution
import com.eat.jani.models.enums.PlanType
import com.eat.jani.repositories.health.HealthRepository
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.RoundingMode

class MealPlanningViewModel(private val repository: HealthRepository) : ViewModel() {
    val planType = MutableLiveData<PlanType>(PlanType.Gain)
    val targetWeight = MutableLiveData<String>("0.0")
    val timeFrame = MutableLiveData<Int>(1)
    val mealsPerDay = MutableLiveData<Int>(1)
    val doesPhysicalActivity = MutableLiveData<Boolean>(false)
    val activityTime = MutableLiveData<String?>()
    val dietDistribution = MutableLiveData<DietDistribution>(DietDistribution.Default)

    // For Time
    val hours = MutableLiveData<String>("18")
    val minutes = MutableLiveData<String>("00")

    // Calculated Health Info
    private val _healthInfo = MutableLiveData<HealthResponse?>()
    val healthInfo: LiveData<HealthResponse?> get() = _healthInfo

    // Responce
    private val _dietPlanResponse = MutableLiveData<List<DietPlanResponse>?>()
    val dietPlanResponse: LiveData<List<DietPlanResponse>?> get() = _dietPlanResponse

    private val _progressResponse = MutableLiveData<List<ProgressResponse>?>()
    val progressResponse: LiveData<List<ProgressResponse>?> get() = _progressResponse

    //Error
    val generalError = MutableLiveData<String?>()

    //States
    val isLoading = MutableLiveData<Boolean>()
    val getInfoSuccess = MutableLiveData<Boolean>()
    val dietPlanSuccess = MutableLiveData<Boolean>()
    val progressInfoSuccess = MutableLiveData<Boolean>()
    val isBtnVisible = MutableLiveData<Boolean>()

    val dietDistributionDisplay = listOf(
        "სტანდარტული",
        "დაბალი > საშუალო > მაღალი",
        "დაბალი > მაღალი > საშუალო",
        "საშუალო > დაბალი > მაღალი",
        "საშუალო > მაღალი > დაბალი",
        "მაღალი > დაბალი > საშუალო"
    )
    val dietDistributionMap = mapOf(
        "სტანდარტული" to DietDistribution.Default,
        "დაბალი > საშუალო > მაღალი" to DietDistribution.EnergyRiseThroughoutDay,
        "დაბალი > მაღალი > საშუალო" to DietDistribution.EnergyPeakAfternoon,
        "საშუალო > დაბალი > მაღალი" to DietDistribution.EnergyPeakNight,
        "საშუალო > მაღალი > დაბალი" to DietDistribution.EnergyPeakAfternoonLowNight,
        "მაღალი > დაბალი > საშუალო" to DietDistribution.EnergyPeakMorning
    )

    /*init {
        getInfo()
    }*/

    fun getInfo() {
        viewModelScope.launch {
            try {
                val responce = repository.getHealthInfo()
                if (responce.isSuccessful) {
                    getInfoSuccess.value = true
                    _healthInfo.value = responce.body()
                    targetWeight.value = healthInfo.value?.weight?.toString()
                } else {
                    getInfoSuccess.value = false
                    Log.d("DIET_INFO", "Error Body: ${responce.errorBody()?.string()}")
                }
            } catch (e : Exception) {
                generalError.value = "პრობლემა, სცადეთ მოგვიანებით"
                Log.d("DIET_INFO", "Exception: ${e.localizedMessage}")
            }
        }
    }

    fun planDiet() {
        isLoading.value = true
        generalError.value = null

        viewModelScope.launch {
            try {
                val request = DietPlanRequest(
                    planType = planType.value!!,
                    targetWeight = targetWeight.value!!.toDouble(),
                    timeFrame = timeFrame.value!!,
                    mealsPerDay = mealsPerDay.value!!,
                    doesPhysicalActivity = doesPhysicalActivity.value!!,
                    activityTime = if (doesPhysicalActivity.value == true) activityTime.value else null,
                    dietDistribution = dietDistribution.value!!
                )
                Log.d("DIET_INFO", "$request")

                val responce = repository.planUserDiet(request)

                if(responce.isSuccessful) {
                    _dietPlanResponse.value = responce.body()
                    Log.d("DIET_INFO", "Responce Body: ${dietPlanResponse.value}")
                    resetValues()
                    dietPlanSuccess.value = true
                    isBtnVisible.value = true
                } else {
                    dietPlanSuccess.value = false
                    isBtnVisible.value = false
                    generalError.value = "დიეტის დაგეგმვა ვერ მოხერხდა"
                    Log.e("DIET_INFO", "Error Body: ${responce.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                generalError.value = "პრობლემა, სცადეთ მოგვიანებით"
                Log.e("DIET_INFO", "Exception: ${e.localizedMessage}")
            } finally {
                isLoading.value = false
            }
        }
    }

    fun getProgressInfo() {
        generalError.value = null

        viewModelScope.launch {
            try {
                val response = repository.checkPlanProgress()
                if(response.isSuccessful) {
                    progressInfoSuccess.value = true
                    val progressInfo = response.body() ?: emptyList()
                    val sortedList = progressInfo.sortedBy { it.weekNumber }
                    _progressResponse.value = sortedList
                } else {
                    progressInfoSuccess.value = false
                    generalError.value = "მონაცემების წამოღება ვერ მოხდა"
                    Log.e("DIET_INFO", "Error Body: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                generalError.value = "პრობლემა, სცადეთ მოგვიანებით"
                Log.e("DIET_INFO", "Exception: ${e.localizedMessage}")
            }
        }
    }

    fun updatePlanType(plan: PlanType) {
        planType.value = plan
    }
    fun increaseTargetWeight() {
        val current = BigDecimal(targetWeight.value ?: "0.0")
        val maxWeight = if (planType.value == PlanType.Gain) {
            BigDecimal("500.0")
        } else if (planType.value == PlanType.Loss) {
            BigDecimal((healthInfo.value?.weight?.minus(1).toString()))
        } else {
            targetWeight.value = healthInfo.value?.weight?.toString()
            return
        }
        if (current > maxWeight) {
            targetWeight.value = "30.0"
        } else {
            val updated = current.add(BigDecimal("0.1")).setScale(1, RoundingMode.HALF_UP)
            targetWeight.value = updated.toPlainString()
        }
    }
    fun decreaseTargetWeight() {
        val current = BigDecimal(targetWeight.value ?: "0.0")
        val minWeight = if (planType.value == PlanType.Gain) {
            BigDecimal(healthInfo.value?.weight?.toString() ?: "0.0")
        } else if (planType.value == PlanType.Loss) {
            BigDecimal("30.1")
        } else {
            targetWeight.value = healthInfo.value?.weight?.toString()
            return
        }
        if (current < minWeight) {
            targetWeight.value = healthInfo.value?.weight?.toString() ?: "0.0"
        } else {
            val updated = current.subtract(BigDecimal("0.1")).setScale(1, RoundingMode.HALF_UP)
            targetWeight.value = updated.toPlainString()
        }
    }
    fun updateTimeFrame(time: Int) {
        timeFrame.value = time
    }
    fun updateMealsPerDay(mealsDay: Int) {
        mealsPerDay.value = mealsDay
    }
    fun setDietDistribution(displayText: String) {
        dietDistributionMap[displayText]?.let {
            dietDistribution.value = it
        }
    }
    fun increaseActivityTimeHours() {
        val currentHour = hours.value?.toIntOrNull() ?: 0
        val newHour = (currentHour + 1) % 24
        hours.value = newHour.toString().padStart(2, '0')
        updateActivityTime()
    }
    fun decreaseActivityTimeHours() {
        val currentHour = hours.value?.toIntOrNull() ?: 0
        val newHour = if (currentHour - 1 < 0) 23 else currentHour - 1
        hours.value = newHour.toString().padStart(2, '0')
        updateActivityTime()
    }
    fun increaseActivityTimeMinutes() {
        val currentMinute = minutes.value?.toIntOrNull() ?: 0
        val newMinute = (currentMinute + 1) % 60
        minutes.value = newMinute.toString().padStart(2, '0')
        updateActivityTime()
    }
    fun decreaseActivityTimeMinutes() {
        val currentMinute = minutes.value?.toIntOrNull() ?: 0
        val newMinute = if (currentMinute - 1 < 0) 59 else currentMinute - 1
        minutes.value = newMinute.toString().padStart(2, '0')
        updateActivityTime()
    }
    private fun updateActivityTime() {
        activityTime.value = "${hours.value}:${minutes.value}"
        Log.d("TIME_COUNTER", "${activityTime.value}")
    }
    private fun resetValues() {
        planType.value = PlanType.Gain
        targetWeight.value = healthInfo.value?.weight?.toString()
        timeFrame.value = 1
        mealsPerDay.value = 1
        doesPhysicalActivity.value = false
        activityTime.value = null
        dietDistribution.value = DietDistribution.Default
        hours.value = "18"
        minutes.value = "00"
    }
}
