package com.eat.jani.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.eat.jani.JaniApp
import com.eat.jani.api.RetrofitInstance
import com.eat.jani.repositories.health.HealthRepository
import com.eat.jani.viewmodels.NotificationViewModelFactory
import com.eat.jani.viewmodels.notification.NotificationViewModel

class NotificationReceiver : BroadcastReceiver() {
    private lateinit var viewModel: NotificationViewModel
    override fun onReceive(context: Context, intent: Intent) {
        viewModel = NotificationViewModelFactory(
            HealthRepository(RetrofitInstance.healthApi)
        ).create(NotificationViewModel::class.java)

        val token = JaniApp.sharedPreferences.getString("firebase_token", null)
        if (token != null) {
            viewModel.sendMealNotification(token)
        } else {
            Log.e("NotificationReceiver", "Firebase token is not available.")
        }
    }
}