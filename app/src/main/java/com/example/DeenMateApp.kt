package com.example

import android.app.Application
import com.example.data.local.AppDatabase
import com.example.notification.NotificationHelper
import com.example.notification.PrayerAlarmScheduler

class DeenMateApp : Application() {
    lateinit var database: AppDatabase
        private set

    override fun onCreate() {
        super.onCreate()
        database = AppDatabase.getInstance(this)
        NotificationHelper.createNotificationChannels(this)
        PrayerAlarmScheduler.scheduleAllAlarms(this)
    }
}
