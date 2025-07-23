package com.eat.jani.viewmodels.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eat.jani.models.enums.ActivityLevel
import com.eat.jani.models.enums.Gender
import com.eat.jani.models.calculations.HealthRequest
import com.eat.jani.models.calculations.HealthResponse
import com.eat.jani.repositories.health.HealthRepository
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.RoundingMode

class HealthViewModel(private val repository: HealthRepository) : ViewModel() {
    val age = MutableLiveData<Int>(18)
    val gender = MutableLiveData<Gender?>(Gender.Male)
    val height = MutableLiveData<Int>(50)
    val weight = MutableLiveData<String>("100.0")
    val activityLevel = MutableLiveData<ActivityLevel?>(ActivityLevel.Sedentary)
    val averageStepsPerDay = MutableLiveData("0")
    val waist = MutableLiveData<Int?>(null)
    val neck = MutableLiveData<Int?>(null)
    val hips = MutableLiveData<Int?>(null)

    //Responce
    private val _healthResponse = MutableLiveData<HealthResponse?>()
    val healthResponse: LiveData<HealthResponse?> get() = _healthResponse

    //Error
    val generalError = MutableLiveData<String?>()
    val stepsError = MutableLiveData<String?>()

    // State
    val isLoading = MutableLiveData<Boolean>()
    val successfulResponse = MutableLiveData<Boolean>(false)

    val activityLevelsDisplay = listOf(
        "დაბალი",
        "მსუბუქი",
        "საშუალო",
        "მაღალი",
        "ძალიან მაღალი"
    )
    val activityLevelMap = mapOf(
        "დაბალი" to ActivityLevel.Sedentary,
        "მსუბუქი" to ActivityLevel.LightActive,
        "საშუალო" to ActivityLevel.ModeratelyActive,
        "მაღალი" to ActivityLevel.VeryActive,
        "ძალიან მაღალი" to ActivityLevel.SuperActive
    )

    fun calculateHealthInfo() {
        isLoading.value = true
        generalError.value = null

        viewModelScope.launch {
            try {
                val request = HealthRequest(
                    age = age.value!!,
                    gender = gender.value!!,
                    height = height.value!!.toDouble(),
                    weight = weight.value!!.toDouble(),
                    activityLevel = activityLevel.value!!,
                    averageStepsPerDay = averageStepsPerDay.value!!.toInt(),
                    waist = waist.value?.toDouble(),
                    neck = neck.value?.toDouble(),
                    hips = hips.value?.toDouble()
                )
                Log.d("HEALTH_INFO", "$request")

                val responce = repository.updateHealthInformation(request)

                if(responce.isSuccessful) {
                    _healthResponse.value = responce.body()
                    Log.d("HEALTH_INFO", "${responce.body()}")
                    resetValues()
                    successfulResponse.value = true
                } else {
                    generalError.value = if (responce.code() == 400) {
                        "წონა უნდა იყოს 30-დან 200-მდე"
                    } else {
                        "მონაცემების მიღება ვერ მოხერხდა"
                    }
                    Log.e("HEALTH_INFO", "Error Body: ${responce.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                generalError.value = "პრობლემა, სცადეთ მოგვიანებით"
                Log.e("HEALTH_INFO", "Exception: ${e.localizedMessage}")
            } finally {
                isLoading.value = false
            }
        }
    }

    fun increaseWeight() {
        val current = BigDecimal(weight.value ?: "0.0")
        if (current > BigDecimal(199.9)) {
            weight.value = "30.0"
        } else {
            val updated = current.add(BigDecimal("0.1")).setScale(1, RoundingMode.HALF_UP)
            weight.value = updated.toPlainString()
        }
    }
    fun decreaseWeight() {
        val current = BigDecimal(weight.value ?: "0.0")
        if (current < BigDecimal(30.1)) {
            weight.value = "200.0"
        } else {
            val updated = current.subtract(BigDecimal("0.1")).setScale(1, RoundingMode.HALF_UP)
            weight.value = updated.toPlainString()
        }
    }
    fun updateGender(gen: Gender) {
        gender.value = gen
    }
    fun updateHeight(hei: Int) {
        height.value = hei
    }
    fun increaseAge() {
        val currentAge = age.value ?: return
        if(currentAge >= 100) {
            age.value = 18
        } else {
            age.value = age.value?.plus(1)
        }
    }
    fun decreaseAge() {
        val currentAge = age.value ?: return
        if (currentAge <= 0) {
            age.value = 100
        } else {
            age.value = currentAge - 1
        }
    }
    fun setActivityLevelByDisplay(displayText: String) {
        activityLevelMap[displayText]?.let {
            activityLevel.value = it
        }
    }
    fun updateWaist(wai: Int) {
        waist.value = wai
    }
    fun updateNeck(nec: Int) {
        neck.value = nec
    }
    fun updateHips(hip: Int) {
        hips.value = hip
    }
    fun resetValues() {
        age.value = 18
        gender.value = Gender.Male
        height.value = 50
        weight.value = "100.0"
        activityLevel.value = ActivityLevel.Sedentary
        waist.value = null
        neck.value = null
        hips.value = null
        averageStepsPerDay.value = "0"
    }
}