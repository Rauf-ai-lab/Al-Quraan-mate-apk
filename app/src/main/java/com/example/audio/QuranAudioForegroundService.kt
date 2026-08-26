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

class QuranAudioForegroundService : Service() {

    companion object {
        const val ACTION_PLAY = "com.example.action.QURAN_PLAY"
        const val ACTION_PAUSE = "com.example.action.QURAN_PAUSE"
        const val ACTION_NEXT = "com.example.action.QURAN_NEXT"
        const val ACTION_PREV = "com.example.action.QURAN_PREV"
        const val ACTION_STOP = "com.example.action.QURAN_STOP"
        const val ACTION_UPDATE_NOTIFICATION = "com.example.action.QURAN_UPDATE_NOTIFICATION"

        const val EXTRA_SURAH_NAME = "EXTRA_SURAH_NAME"
        const val EXTRA_AYAH_NUMBER = "EXTRA_AYAH_NUMBER"
        const val EXTRA_RECITER_NAME = "EXTRA_RECITER_NAME"
        const val EXTRA_IS_PLAYING = "EXTRA_IS_PLAYING"

        const val NOTIFICATION_ID = 9002
        const val CHANNEL_ID = "channel_quran_audio_playback"

        var isServiceRunning = false
            private set

        fun startOrUpdate(
            context: Context,
            surahName: String,
            ayahNumber: Int,
            reciterName: String,
            isPlaying: Boolean
        ) {
            val intent = Intent(context, QuranAudioForegroundService::class.java).apply {
                action = ACTION_UPDATE_NOTIFICATION
                putExtra(EXTRA_SURAH_NAME, surahName)
                putExtra(EXTRA_AYAH_NUMBER, ayahNumber)
                putExtra(EXTRA_RECITER_NAME, reciterName)
                putExtra(EXTRA_IS_PLAYING, isPlaying)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun stopService(context: Context) {
            val intent = Intent(context, QuranAudioForegroundService::class.java).apply {
                action = ACTION_STOP
            }
            context.startService(intent)
        }
    }

    private var currentSurahName = "Al-Fatihah"
    private var currentAyahNumber = 1
    private var currentReciterName = "Mishary Rashid Alafasy"
    private var isCurrentlyPlaying = true

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        isServiceRunning = true
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action

        when (action) {
            ACTION_STOP -> {
                QuranAudioPlayerManager.sharedInstance?.stop()
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
                return START_NOT_STICKY
            }
            ACTION_PLAY -> {
                QuranAudioPlayerManager.sharedInstance?.togglePlayPause()
                isCurrentlyPlaying = true
                updateNotification()
                return START_NOT_STICKY
            }
            ACTION_PAUSE -> {
                QuranAudioPlayerManager.sharedInstance?.togglePlayPause()
                isCurrentlyPlaying = false
                updateNotification()
                return START_NOT_STICKY
            }
            ACTION_NEXT -> {
                QuranAudioPlayerManager.sharedInstance?.nextAyah()
                return START_NOT_STICKY
            }
            ACTION_PREV -> {
                QuranAudioPlayerManager.sharedInstance?.previousAyah()
                return START_NOT_STICKY
            }
            ACTION_UPDATE_NOTIFICATION -> {
                currentSurahName = intent.getStringExtra(EXTRA_SURAH_NAME) ?: currentSurahName
                currentAyahNumber = intent.getIntExtra(EXTRA_AYAH_NUMBER, currentAyahNumber)
                currentReciterName = intent.getStringExtra(EXTRA_RECITER_NAME) ?: currentReciterName
                isCurrentlyPlaying = intent.getBooleanExtra(EXTRA_IS_PLAYING, isCurrentlyPlaying)
                updateNotification()
            }
        }

        return START_STICKY
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Quran Audio Recitation",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Background Quran recitation audio player controls"
                setShowBadge(false)
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            }
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    private fun updateNotification() {
        val notification = buildMediaNotification()
        startForeground(NOTIFICATION_ID, notification)
    }

    private fun buildMediaNotification(): Notification {
        val openAppIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val openAppPendingIntent = PendingIntent.getActivity(
            this,
            0,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0)
        )

        val prevIntent = Intent(this, QuranAudioForegroundService::class.java).apply { action = ACTION_PREV }
        val prevPendingIntent = PendingIntent.getService(this, 1, prevIntent, PendingIntent.FLAG_UPDATE_CURRENT or (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0))

        val playPauseIntent = Intent(this, QuranAudioForegroundService::class.java).apply {
            action = if (isCurrentlyPlaying) ACTION_PAUSE else ACTION_PLAY
        }
        val playPausePendingIntent = PendingIntent.getService(this, 2, playPauseIntent, PendingIntent.FLAG_UPDATE_CURRENT or (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0))

        val nextIntent = Intent(this, QuranAudioForegroundService::class.java).apply { action = ACTION_NEXT }
        val nextPendingIntent = PendingIntent.getService(this, 3, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT or (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0))

        val stopIntent = Intent(this, QuranAudioForegroundService::class.java).apply { action = ACTION_STOP }
        val stopPendingIntent = PendingIntent.getService(this, 4, stopIntent, PendingIntent.FLAG_UPDATE_CURRENT or (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0))

        val playPauseIcon = if (isCurrentlyPlaying) android.R.drawable.ic_media_pause else android.R.drawable.ic_media_play
        val playPauseTitle = if (isCurrentlyPlaying) "Pause" else "Play"

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Surah $currentSurahName • Ayah $currentAyahNumber")
            .setContentText("Reciter: $currentReciterName")
            .setSmallIcon(android.R.drawable.ic_media_play)
            .setContentIntent(openAppPendingIntent)
            .setOngoing(isCurrentlyPlaying)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .addAction(android.R.drawable.ic_media_previous, "Previous", prevPendingIntent)
            .addAction(playPauseIcon, playPauseTitle, playPausePendingIntent)
            .addAction(android.R.drawable.ic_media_next, "Next", nextPendingIntent)
            .addAction(android.R.drawable.ic_menu_close_clear_cancel, "Close", stopPendingIntent)
            .build()
    }

    override fun onDestroy() {
        super.onDestroy()
        isServiceRunning = false
    }
}
