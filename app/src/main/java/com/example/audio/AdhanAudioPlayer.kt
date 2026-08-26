package com.example.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.sin

object AdhanAudioPlayer {

    private var mediaPlayer: MediaPlayer? = null
    private var synthJob: Job? = null
    private var isCurrentlyPlaying = false

    fun isPlaying(): Boolean = isCurrentlyPlaying

    fun playAdhanSound(context: Context, soundName: String = "Makkah Adhan", durationSeconds: Int = 15) {
        stopAdhan()
        isCurrentlyPlaying = true

        try {
            // Attempt to play standard alarm ringtone first
            val alarmUri: Uri? = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
                ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

            if (alarmUri != null) {
                mediaPlayer = MediaPlayer().apply {
                    setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_ALARM)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .build()
                    )
                    setDataSource(context, alarmUri)
                    isLooping = true
                    prepare()
                    start()
                }

                synthJob = CoroutineScope(Dispatchers.IO).launch {
                    delay(durationSeconds * 1000L)
                    stopAdhan()
                }
            } else {
                playHarmonicAdhanSynth(durationSeconds)
            }
        } catch (_: Exception) {
            playHarmonicAdhanSynth(durationSeconds)
        }
    }

    private fun playHarmonicAdhanSynth(durationSeconds: Int) {
        synthJob = CoroutineScope(Dispatchers.Default).launch {
            try {
                val sampleRate = 44100
                // Melodious Bayati / Hijaz Islamic Maqam frequencies for Takbeer (Allahu Akbar)
                val notes = listOf(
                    392.0 to 1200L, // G4 (Al-)
                    523.25 to 1600L, // C5 (-laaahu)
                    466.16 to 1000L, // Bb4 (Ak-)
                    392.0 to 1800L,  // G4 (-bar)
                    349.23 to 800L,  // F4
                    392.0 to 1400L,  // G4
                    523.25 to 1800L, // C5 (Allahu Akbar)
                    466.16 to 1000L,
                    392.0 to 2000L
                )

                val minBufferSize = AudioTrack.getMinBufferSize(
                    sampleRate,
                    AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT
                )

                val audioTrack = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_ALARM)
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(sampleRate)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes(minBufferSize)
                    .setTransferMode(AudioTrack.MODE_STREAM)
                    .build()

                audioTrack.play()

                val startTime = System.currentTimeMillis()
                while (isCurrentlyPlaying && (System.currentTimeMillis() - startTime) < durationSeconds * 1000L) {
                    for ((freq, durationMs) in notes) {
                        if (!isCurrentlyPlaying) break
                        val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
                        val buffer = ShortArray(numSamples)
                        for (i in 0 until numSamples) {
                            val time = i.toDouble() / sampleRate
                            val envelope = (1.0 - (i.toDouble() / numSamples) * 0.3)
                            val wave = sin(2.0 * Math.PI * freq * time) + 0.3 * sin(4.0 * Math.PI * freq * time)
                            buffer[i] = (wave * 0.5 * envelope * Short.MAX_VALUE).toInt().toShort()
                        }
                        audioTrack.write(buffer, 0, buffer.size)
                    }
                }

                audioTrack.stop()
                audioTrack.release()
            } catch (_: Exception) {
            } finally {
                isCurrentlyPlaying = false
            }
        }
    }

    fun stopAdhan() {
        isCurrentlyPlaying = false
        synthJob?.cancel()
        synthJob = null
        try {
            mediaPlayer?.stop()
            mediaPlayer?.release()
        } catch (_: Exception) {}
        mediaPlayer = null
    }
}
