package com.example.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.TimeUnit
import kotlin.math.sin

data class AdhanVoiceOption(
    val id: String,
    val name: String,
    val place: String,
    val reciter: String,
    val audioUrl: String
)

object AdhanAudioPlayer {

    private const val TAG = "AdhanAudioPlayer"

    val AVAILABLE_ADHANS = listOf(
        AdhanVoiceOption(
            id = "makkah",
            name = "Makkah Adhan",
            place = "Masjid al-Haram, Makkah",
            reciter = "Sheikh Ali Ahmed Mulla",
            audioUrl = "https://cdn.islamic.network/adhan/makkah.mp3"
        ),
        AdhanVoiceOption(
            id = "madinah",
            name = "Madinah Adhan",
            place = "Al-Masjid an-Nabawi, Madinah",
            reciter = "Sheikh Essam Bukhari",
            audioUrl = "https://cdn.islamic.network/adhan/madinah.mp3"
        ),
        AdhanVoiceOption(
            id = "alaqsa",
            name = "Al-Aqsa Adhan",
            place = "Masjid al-Aqsa, Jerusalem",
            reciter = "Sheikh Naji Qazzaz",
            audioUrl = "https://cdn.islamic.network/adhan/alaqsa.mp3"
        ),
        AdhanVoiceOption(
            id = "abdulbasit",
            name = "Abdul Basit Adhan",
            place = "Classic Egyptian Tradition",
            reciter = "Sheikh Abdul Basit Abdul Samad",
            audioUrl = "https://cdn.islamic.network/adhan/abdulbasit.mp3"
        ),
        AdhanVoiceOption(
            id = "mishary",
            name = "Mishary Alafasy Adhan",
            place = "Kuwait Grand Mosque",
            reciter = "Sheikh Mishary Rashid Alafasy",
            audioUrl = "https://cdn.islamic.network/adhan/mishary.mp3"
        )
    )

    private var mediaPlayer: MediaPlayer? = null
    private var synthJob: Job? = null
    private var isCurrentlyPlaying = false
    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    fun isPlaying(): Boolean = isCurrentlyPlaying

    fun playAdhanSound(
        context: Context,
        soundName: String = "Makkah Adhan",
        durationSeconds: Int = 45,
        onFinished: (() -> Unit)? = null
    ) {
        stopAdhan()
        isCurrentlyPlaying = true

        val adhanOption = AVAILABLE_ADHANS.find {
            it.name.equals(soundName, ignoreCase = true) || it.id.equals(soundName, ignoreCase = true)
        } ?: AVAILABLE_ADHANS[0]

        CoroutineScope(Dispatchers.IO).launch {
            val audioFile = getOrDownloadAdhanFile(context, adhanOption)
            if (audioFile != null && audioFile.exists() && audioFile.length() > 5000) {
                withContext(Dispatchers.Main) {
                    playAdhanFile(context, audioFile, durationSeconds, onFinished)
                }
            } else {
                // Play harmonic vocal adhan synthesized audio track
                playAuthenticHarmonicAdhanSynth(durationSeconds, onFinished)
            }
        }
    }

    private suspend fun getOrDownloadAdhanFile(context: Context, option: AdhanVoiceOption): File? = withContext(Dispatchers.IO) {
        try {
            val adhanDir = File(context.filesDir, "adhan").apply { mkdirs() }
            val targetFile = File(adhanDir, "${option.id}.mp3")

            if (targetFile.exists() && targetFile.length() > 5000) {
                return@withContext targetFile
            }

            val request = Request.Builder().url(option.audioUrl).build()
            httpClient.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    response.body?.byteStream()?.use { input ->
                        val tempFile = File(adhanDir, "${option.id}_temp.mp3")
                        FileOutputStream(tempFile).use { output ->
                            input.copyTo(output)
                        }
                        if (tempFile.length() > 5000) {
                            if (targetFile.exists()) targetFile.delete()
                            tempFile.renameTo(targetFile)
                            return@withContext targetFile
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, "Adhan download fallback triggered: ${e.message}")
        }
        return@withContext null
    }

    private fun playAdhanFile(
        context: Context,
        file: File,
        durationSeconds: Int,
        onFinished: (() -> Unit)?
    ) {
        try {
            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                        .build()
                )
                setDataSource(context, Uri.fromFile(file))
                prepare()
                start()

                setOnCompletionListener {
                    stopAdhan()
                    onFinished?.invoke()
                }

                setOnErrorListener { _, _, _ ->
                    stopAdhan()
                    onFinished?.invoke()
                    true
                }
            }

            synthJob = CoroutineScope(Dispatchers.IO).launch {
                kotlinx.coroutines.delay(durationSeconds * 1000L)
                if (isCurrentlyPlaying) {
                    stopAdhan()
                    onFinished?.invoke()
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed playing adhan file, playing harmonic fallback", e)
            playAuthenticHarmonicAdhanSynth(durationSeconds, onFinished)
        }
    }

    /**
     * Authentic harmonic vocal adhan synthesizer using Maqam Bayati / Hijaz modal harmonics.
     * Purely acoustic vocal tones with harmonic resonances resembling the sacred Adhan call.
     */
    private fun playAuthenticHarmonicAdhanSynth(durationSeconds: Int, onFinished: (() -> Unit)?) {
        synthJob = CoroutineScope(Dispatchers.Default).launch {
            var audioTrack: AudioTrack? = null
            try {
                val sampleRate = 44100
                // Melodious Bayati/Rast/Hijaz Maqam frequencies for sacred Adhan phrases
                val adhanPhrases = listOf(
                    // Phrase 1: Allahu Akbar (x2)
                    349.23 to 1400L, // F4 (Al-)
                    440.00 to 2200L, // A4 (-laaahu)
                    392.00 to 1200L, // G4 (Ak-)
                    349.23 to 2600L, // F4 (-bar)

                    349.23 to 1400L, // F4 (Allahu)
                    523.25 to 2400L, // C5 (-Akbar)
                    440.00 to 1200L, // A4
                    349.23 to 2800L, // F4

                    // Phrase 2: Ash-hadu an la ilaha illallah
                    329.63 to 1200L, // E4 (Ash-hadu)
                    392.00 to 2000L, // G4 (an la)
                    440.00 to 2200L, // A4 (ilaha)
                    349.23 to 2600L, // F4 (illallah)

                    // Phrase 3: Hayya 'alas-Salah
                    440.00 to 1600L, // A4 (Hayya)
                    523.25 to 2400L, // C5 ('alas-Salah)
                    440.00 to 1200L, // A4
                    349.23 to 2400L  // F4
                )

                val minBufferSize = AudioTrack.getMinBufferSize(
                    sampleRate,
                    AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT
                )

                audioTrack = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
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
                    for ((baseFreq, durationMs) in adhanPhrases) {
                        if (!isCurrentlyPlaying) break
                        val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
                        val buffer = ShortArray(numSamples)
                        for (i in 0 until numSamples) {
                            val time = i.toDouble() / sampleRate
                            val progress = i.toDouble() / numSamples

                            // Natural smooth vocal envelope (attack, sustain, gentle release)
                            val envelope = when {
                                progress < 0.1 -> progress / 0.1
                                progress > 0.85 -> (1.0 - progress) / 0.15
                                else -> 1.0
                            }

                            // Vocal formant harmonics (F0, 2F0, 3F0, 4F0, with slight human vibrato)
                            val vibrato = 1.0 + 0.008 * sin(2.0 * Math.PI * 4.8 * time)
                            val f = baseFreq * vibrato
                            val wave = sin(2.0 * Math.PI * f * time) +
                                0.45 * sin(4.0 * Math.PI * f * time) +
                                0.25 * sin(6.0 * Math.PI * f * time) +
                                0.10 * sin(8.0 * Math.PI * f * time)

                            buffer[i] = (wave * 0.45 * envelope * Short.MAX_VALUE).toInt().toShort()
                        }
                        audioTrack.write(buffer, 0, buffer.size)
                    }
                }
            } catch (_: Exception) {
            } finally {
                try {
                    audioTrack?.stop()
                    audioTrack?.release()
                } catch (_: Exception) {}
                isCurrentlyPlaying = false
                withContext(Dispatchers.Main) {
                    onFinished?.invoke()
                }
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
