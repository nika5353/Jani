package com.eat.jani.notification

import android.content.Context
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import java.util.Calendar

object NotificationScheduler {
    @RequiresApi(Build.VERSION_CODES.S)
    fun scheduleMealNotifications(context: Context, times: List<String>) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        for ((index, timeString) in times.withIndex()) {
            val timeParts = timeString.split(":")
            if (timeParts.size != 2) continue

            val hour = timeParts[0].toIntOrNull() ?: continue
            val minute = timeParts[1].toIntOrNull() ?: continue

            val calendar = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, minute)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
                if (before(Calendar.getInstance())) {
                    add(Calendar.DAY_OF_YEAR, 1) // თუ დრო უკვე გასულია, დაგეგმოს ხვალისთვის
                }
            }

            val intent = Intent(context, NotificationReceiver::class.java).apply {
                putExtra("title", "კვება")
                putExtra("message", "არ გამოგრჩეს კვება ამ დროისთვის: $timeString")
            }

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                index,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            try {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        calendar.timeInMillis,
                        pendingIntent
                    )
                    Log.d("NotificationScheduler", "Scheduled alarm for $hour:$minute")
                } else {
                    Log.w("NotificationScheduler", "Cannot schedule exact alarms. Permission required.")
                }
            } catch (e: SecurityException) {
                Log.e("NotificationScheduler", "SecurityException: ${e.message}")
            }
        }
    }
}