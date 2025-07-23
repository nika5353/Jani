package com.eat.jani

import android.annotation.SuppressLint
import android.app.Application
import android.content.SharedPreferences
import android.util.Log
import androidx.core.content.edit
import com.eat.jani.api.RetrofitInstance
import com.eat.jani.notification.fetchFirebaseToken
import com.eat.jani.repositories.health.HealthRepository
import com.eat.jani.viewmodels.NotificationViewModelFactory
import com.eat.jani.viewmodels.notification.NotificationViewModel

class JaniApp : Application() {
    private lateinit var viewModel: NotificationViewModel

    companion object {
        lateinit var sharedPreferences: SharedPreferences
        lateinit var authSharedPreferences: SharedPreferences
    }

    @SuppressLint("CommitPrefEdits")
    override fun onCreate() {
        super.onCreate()
        RetrofitInstance.init(this)
        sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE)
        authSharedPreferences = getSharedPreferences("auth_prefs", MODE_PRIVATE)

        viewModel = NotificationViewModelFactory(
            HealthRepository(RetrofitInstance.healthApi)
        ).create(NotificationViewModel::class.java)

        fetchFirebaseToken { token ->
            Log.d("FirebaseToken", "Fetched token: $token")
            token?.let {
                sharedPreferences.edit {
                    putString("firebase_token", it)
                }
            }
        }
    }
}
