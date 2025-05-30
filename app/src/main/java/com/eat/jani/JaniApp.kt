package com.eat.jani

import android.app.Application
import android.content.SharedPreferences
import com.eat.jani.api.RetrofitInstance

class JaniApp : Application() {
    companion object {
        lateinit var sharedPreferences: SharedPreferences
        lateinit var authSharedPreferences: SharedPreferences
    }

    override fun onCreate() {
        super.onCreate()
        RetrofitInstance.init(this)
        sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE)
        authSharedPreferences = getSharedPreferences("auth_prefs", MODE_PRIVATE)
    }
}