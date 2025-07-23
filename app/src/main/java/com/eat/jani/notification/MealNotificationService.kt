package com.eat.jani.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.eat.jani.JaniApp
import com.eat.jani.MainActivity
import com.eat.jani.R
import com.eat.jani.api.RetrofitInstance
import com.eat.jani.repositories.health.HealthRepository
import com.eat.jani.viewmodels.NotificationViewModelFactory
import com.eat.jani.viewmodels.notification.NotificationViewModel
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import androidx.core.content.edit

class MealNotificationService : FirebaseMessagingService() {
    private lateinit var viewModel: NotificationViewModel

    override fun onCreate() {
        super.onCreate()
        viewModel = NotificationViewModelFactory(
            HealthRepository(RetrofitInstance.healthApi)
        ).create(NotificationViewModel::class.java)
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)

        JaniApp.sharedPreferences.getString("firebase_token", null)?.let {
            Log.d("FirebaseToken", "New token: $token")
            // Update the token in shared preferences
            JaniApp.sharedPreferences.edit { putString("firebase_token", token) }
            // Optionally, send the new token to your server or update it in your app's backend
            viewModel.sendMealNotification(token)
        } ?: run {
            Log.e("FirebaseToken", "Failed to retrieve existing token.")
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        remoteMessage.notification?.let {
            Log.d("Notification", "Title: ${it.title}, Body: ${it.body}")
            sendNotification()
        }
    }

    private fun sendNotification() {
        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        val channelId = "meal_channel_id"
        val channelName = "Meal Notifications"
        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        // ✔️ Check and create notification channel (Android 8+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(channelId, channelName, importance).apply {
                enableLights(true)
                enableVibration(true)
                description = "შეტყობინებები კვების დროებზე"
            }
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        // ✔️ Build the notification
        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.meal_planning) // ⚠️ Make sure this exists!
            .setContentTitle("კვების დროა!")
            .setContentText("არ გამოგრჩეს დღევანდელი კვება.")
            .setAutoCancel(true)
            .setSound(soundUri)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH) // Optional for pre-Oreo

        // ✔️ Send the notification
        with(NotificationManagerCompat.from(this)) {
            if (ActivityCompat.checkSelfPermission(this@MealNotificationService, Manifest.permission.POST_NOTIFICATIONS)
                == PackageManager.PERMISSION_GRANTED || Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU
            ) {
                notify(System.currentTimeMillis().toInt(), builder.build())
            }
        }
    }
}