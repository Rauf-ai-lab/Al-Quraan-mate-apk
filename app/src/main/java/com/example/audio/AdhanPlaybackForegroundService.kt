package com.example.audio

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.notification.NotificationHelper

class AdhanPlaybackForegroundService : Service() {

    companion object {
        const val ACTION_START_ADHAN = "com.example.action.START_ADHAN"
        const val ACTION_STOP_ADHAN = "com.example.action.STOP_ADHAN"
        const val EXTRA_PRAYER_NAME = "EXTRA_PRAYER_NAME"
        const val EXTRA_SOUND_NAME = "EXTRA_SOUND_NAME"
        const val NOTIFICATION_ID = 9001
        const val CHANNEL_ID = "channel_adhan_live_playback"

        fun startAdhanService(context: Context, prayerName: String, soundName: String) {
            val intent = Intent(context, AdhanPlaybackForegroundService::class.java).apply {
                action = ACTION_START_ADHAN
                putExtra(EXTRA_PRAYER_NAME, prayerName)
                putExtra(EXTRA_SOUND_NAME, soundName)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun stopAdhanService(context: Context) {
            val intent = Intent(context, AdhanPlaybackForegroundService::class.java).apply {
                action = ACTION_STOP_ADHAN
            }
            context.startService(intent)
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action

        if (action == ACTION_STOP_ADHAN) {
            AdhanAudioPlayer.stopAdhan()
            stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelf()
            return START_NOT_STICKY
        }

        val prayerName = intent?.getStringExtra(EXTRA_PRAYER_NAME) ?: "Prayer Time"
        val soundName = intent?.getStringExtra(EXTRA_SOUND_NAME) ?: "Makkah Adhan"

        createNotificationChannel()
        val notification = buildForegroundNotification(prayerName, soundName)
        startForeground(NOTIFICATION_ID, notification)

        AdhanAudioPlayer.playAdhanSound(
            context = applicationContext,
            soundName = soundName,
            durationSeconds = 60,
            onFinished = {
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
            }
        )

        return START_NOT_STICKY
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Adhan Live Recitation",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Plays authentic Adhan recitation at scheduled prayer times"
                setShowBadge(true)
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            }
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    private fun buildForegroundNotification(prayerName: String, soundName: String): Notification {
        val openAppIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val openAppPendingIntent = PendingIntent.getActivity(
            this,
            0,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0)
        )

        val stopIntent = Intent(this, AdhanPlaybackForegroundService::class.java).apply {
            action = ACTION_STOP_ADHAN
        }
        val stopPendingIntent = PendingIntent.getService(
            this,
            1,
            stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0)
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("🕌 Time for $prayerName")
            .setContentText("Hayya 'alas-Salah • Reciting $soundName")
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentIntent(openAppPendingIntent)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .addAction(android.R.drawable.ic_menu_close_clear_cancel, "Stop Adhan", stopPendingIntent)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .build()
    }

    override fun onDestroy() {
        super.onDestroy()
        AdhanAudioPlayer.stopAdhan()
    }
}
