package com.example.audio

import android.content.Context
import android.util.Log
import com.example.data.model.Ayah
import com.example.data.model.Surah
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.TimeUnit

data class ReciterVoice(
    val id: String,
    val name: String,
    val arabicName: String,
    val cdnFolder: String,
    val styleDescription: String
)

object ReciterVoicePacks {
    val RECITERS = listOf(
        ReciterVoice(
            id = "mishary",
            name = "Mishary Rashid Alafasy",
            arabicName = "مشاري راشد العفاسي",
            cdnFolder = "Alafasy_128kbps",
            styleDescription = "Melodic, crystal clear, emotionally resonant Tajweed"
        ),
        ReciterVoice(
            id = "sudais",
            name = "Abdul Rahman Al-Sudais",
            arabicName = "عبد الرحمن السديس",
            cdnFolder = "Abdurrahmaan_As-Sudais_192kbps",
            styleDescription = "Grand Haram Makkah style, powerful & authoritative"
        ),
        ReciterVoice(
            id = "muaiqly",
            name = "Maher Al-Muaiqly",
            arabicName = "ماهر المعيقلي",
            cdnFolder = "MaherAlMuaiqly128kbps",
            styleDescription = "Calm, steady, and spiritually soothing tempo"
        ),
        ReciterVoice(
            id = "ghamdi",
            name = "Saad Al-Ghamdi",
            arabicName = "سعد الغامدي",
            cdnFolder = "Ghamadi_40kbps",
            styleDescription = "Gentle, reverent, and rhythmic flow"
        ),
        ReciterVoice(
            id = "husary",
            name = "Mahmoud Khalil Al-Husary",
            arabicName = "محمود خليل الحصري",
            cdnFolder = "Husary_128kbps",
            styleDescription = "Master standard classical Murattal & strict Tajweed"
        ),
        ReciterVoice(
            id = "shatri",
            name = "Abu Bakr Al-Shatri",
            arabicName = "أبو بكر الشاطري",
            cdnFolder = "Abu_Bakr_Ash-Shaatree_128kbps",
            styleDescription = "Deep, contemplative, and soulful recitation"
        )
    )

    val DEFAULT_RECITER = RECITERS[0]
}

sealed class QuranAudioResult {
    data class Success(val audioFile: File, val isFromAi: Boolean = false) : QuranAudioResult()
    data class Error(val message: String, val canRetry: Boolean = true) : QuranAudioResult()
}

object QuranAudioService {

    private const val TAG = "QuranAudioService"

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(25, TimeUnit.SECONDS)
        .readTimeout(35, TimeUnit.SECONDS)
        .build()

    /**
     * Audio Pipeline:
     * 1. Check local on-device cache for this Ayah and Reciter.
     * 2. If not cached, seamlessly stream and cache from verified authentic Quran Audio CDN (EveryAyah/QuranCDN).
     */
    suspend fun getAyahAudioFile(
        context: Context,
        surah: Surah,
        ayah: Ayah,
        reciter: ReciterVoice
    ): QuranAudioResult = withContext(Dispatchers.IO) {
        val cacheDir = File(context.cacheDir, "quran_audio")
        if (!cacheDir.exists()) {
            cacheDir.mkdirs()
        }

        val cachedMp3 = File(cacheDir, "${reciter.id}_${surah.number}_${ayah.numberInSurah}.mp3")

        if (cachedMp3.exists() && cachedMp3.length() > 1000) {
            return@withContext QuranAudioResult.Success(cachedMp3, isFromAi = false)
        }

        try {
            return@withContext downloadAyahFromCdn(
                surahNumber = surah.number,
                ayahNumberInSurah = ayah.numberInSurah,
                reciter = reciter,
                outputFile = cachedMp3
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error downloading recitation", e)
            return@withContext QuranAudioResult.Error(
                message = "Unable to load recitation for Ayah ${ayah.numberInSurah}. Please check your connection or retry.",
                canRetry = true
            )
        }
    }

    private suspend fun downloadAyahFromCdn(
        surahNumber: Int,
        ayahNumberInSurah: Int,
        reciter: ReciterVoice,
        outputFile: File
    ): QuranAudioResult = withContext(Dispatchers.IO) {
        val sStr = String.format(java.util.Locale.US, "%03d", surahNumber)
        val aStr = String.format(java.util.Locale.US, "%03d", ayahNumberInSurah)

        // Primary EveryAyah CDN URL pattern: https://everyayah.com/data/Alafasy_128kbps/001001.mp3
        val url = "https://everyayah.com/data/${reciter.cdnFolder}/$sStr$aStr.mp3"

        val request = Request.Builder().url(url).build()
        val response = httpClient.newCall(request).execute()

        if (!response.isSuccessful || response.body == null) {
            // Backup CDN: Quran.com API audio
            val backupUrl = "https://verses.quran.com/${reciter.cdnFolder}/$sStr$aStr.mp3"
            try {
                val backupReq = Request.Builder().url(backupUrl).build()
                val backupResp = httpClient.newCall(backupReq).execute()
                if (backupResp.isSuccessful && backupResp.body != null) {
                    val bytes = backupResp.body!!.bytes()
                    if (bytes.isNotEmpty()) {
                        FileOutputStream(outputFile).use { fos -> fos.write(bytes) }
                        return@withContext QuranAudioResult.Success(outputFile, isFromAi = false)
                    }
                }
            } catch (_: Exception) {}
            return@withContext QuranAudioResult.Error("Failed to fetch recitation (${response.code})")
        }

        val bytes = response.body!!.bytes()
        if (bytes.isEmpty()) {
            return@withContext QuranAudioResult.Error("Empty audio stream received")
        }

        FileOutputStream(outputFile).use { fos ->
            fos.write(bytes)
        }

        return@withContext QuranAudioResult.Success(outputFile, isFromAi = false)
    }
}
