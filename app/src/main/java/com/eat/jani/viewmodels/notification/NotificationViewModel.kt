package com.eat.jani.viewmodels.notification

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eat.jani.notification.NotificationScheduler
import com.eat.jani.repositories.health.HealthRepository
import kotlinx.coroutines.launch

class NotificationViewModel(private val HealthRepository: HealthRepository) : ViewModel() {

    private val _notificationStatus = MutableLiveData<Boolean>()
    val notificationStatus: LiveData<Boolean> get() = _notificationStatus

    private val _mealTimes = MutableLiveData<List<String>>()
    val mealTimes: LiveData<List<String>> get() = _mealTimes

    fun sendMealNotification(token: String) {
        viewModelScope.launch {
            try {
                val response = HealthRepository.sendMealNotification(token)
                _notificationStatus.value = response.isSuccessful
            } catch (e: Exception) {
                _notificationStatus.value = false
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    fun getMealTimes(context: Context) {
        viewModelScope.launch {
            try {
                val response = HealthRepository.getMealTimes()
                if (response.isSuccessful) {
                    _mealTimes.value = response.body() ?: emptyList()
                    Log.d("NotificationViewModel", "Meal times fetched: ${_mealTimes.value}")
                } else {
                    _mealTimes.value = emptyList()
                    NotificationScheduler.scheduleMealNotifications(context, _mealTimes.value ?: emptyList())
                    Log.e("NotificationViewModel", "Failed to fetch meal times: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                _mealTimes.value = emptyList()
                Log.e("NotificationViewModel", "Error fetching meal times: ${e.message}")
            }
        }
    }
}