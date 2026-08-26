package com.example.data.repository

import android.content.Context
import android.util.Log
import com.example.data.local.IslamicDataSource
import com.example.data.model.Ayah
import com.example.data.model.Surah
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.File
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

class QuranRepository(private val context: Context) {

    companion object {
        private const val TAG = "QuranRepository"
        private const val BASE_URL = "https://api.alquran.cloud/v1"

        private val memorySurahCache = ConcurrentHashMap<Int, List<Ayah>>()
        private val memoryJuzCache = ConcurrentHashMap<Int, List<Ayah>>()

        private val httpClient = OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .build()
    }

    private val cacheDir = File(context.filesDir, "quran_texts").apply {
        if (!exists()) mkdirs()
    }

    /**
     * Retrieves all authentic Ayahs for a Surah, using Memory Cache -> Disk Cache -> Network -> Bundled Fallback.
     */
    suspend fun getAyahsForSurah(surah: Surah): List<Ayah> = withContext(Dispatchers.IO) {
        memorySurahCache[surah.number]?.let { return@withContext it }

        val diskFile = File(cacheDir, "surah_${surah.number}.json")
        if (diskFile.exists() && diskFile.length() > 50) {
            try {
                val jsonStr = diskFile.readText()
                val ayahs = parseSurahJson(jsonStr, surah)
                if (ayahs.isNotEmpty()) {
                    memorySurahCache[surah.number] = ayahs
                    return@withContext ayahs
                }
            } catch (e: Exception) {
                Log.w(TAG, "Failed reading cached Surah ${surah.number}", e)
            }
        }

        // Try downloading complete authentic Uthmani text + English translation
        try {
            val url = "$BASE_URL/surah/${surah.number}/editions/quran-uthmani,en.sahih"
            val request = Request.Builder().url(url).build()
            val response = httpClient.newCall(request).execute()
            if (response.isSuccessful) {
                val body = response.body?.string()
                if (!body.isNullOrBlank()) {
                    val ayahs = parseSurahJson(body, surah)
                    if (ayahs.isNotEmpty()) {
                        diskFile.writeText(body)
                        memorySurahCache[surah.number] = ayahs
                        return@withContext ayahs
                    }
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, "Network fetch failed for Surah ${surah.number}, falling back to local data: ${e.message}")
        }

        // Bundled fallback
        val fallback = IslamicDataSource.getAyahsForSurah(surah)
        memorySurahCache[surah.number] = fallback
        fallback
    }

    /**
     * Retrieves all authentic Ayahs for a Juz across Surahs sequentially.
     */
    suspend fun getAyahsForJuz(juzNumber: Int): List<Ayah> = withContext(Dispatchers.IO) {
        memoryJuzCache[juzNumber]?.let { return@withContext it }

        val diskFile = File(cacheDir, "juz_$juzNumber.json")
        if (diskFile.exists() && diskFile.length() > 50) {
            try {
                val jsonStr = diskFile.readText()
                val ayahs = parseJuzJson(jsonStr, juzNumber)
                if (ayahs.isNotEmpty()) {
                    memoryJuzCache[juzNumber] = ayahs
                    return@withContext ayahs
                }
            } catch (e: Exception) {
                Log.w(TAG, "Failed reading cached Juz $juzNumber", e)
            }
        }

        // Try downloading complete authentic Uthmani text + English translation for Juz
        try {
            val url = "$BASE_URL/juz/$juzNumber/editions/quran-uthmani,en.sahih"
            val request = Request.Builder().url(url).build()
            val response = httpClient.newCall(request).execute()
            if (response.isSuccessful) {
                val body = response.body?.string()
                if (!body.isNullOrBlank()) {
                    val ayahs = parseJuzJson(body, juzNumber)
                    if (ayahs.isNotEmpty()) {
                        diskFile.writeText(body)
                        memoryJuzCache[juzNumber] = ayahs
                        return@withContext ayahs
                    }
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, "Network fetch failed for Juz $juzNumber, falling back to local data: ${e.message}")
        }

        // Bundled fallback
        val fallback = IslamicDataSource.getAyahsForJuz(juzNumber)
        memoryJuzCache[juzNumber] = fallback
        fallback
    }

    private fun parseSurahJson(jsonStr: String, surah: Surah): List<Ayah> {
        val root = JSONObject(jsonStr)
        val data = root.optJSONArray("data") ?: return emptyList()
        val arabicObj = data.optJSONObject(0) ?: return emptyList()
        val englishObj = data.optJSONObject(1)

        val arabicAyahs = arabicObj.optJSONArray("ayahs") ?: return emptyList()
        val englishAyahs = englishObj?.optJSONArray("ayahs")

        val list = mutableListOf<Ayah>()
        for (i in 0 until arabicAyahs.length()) {
            val arAyah = arabicAyahs.optJSONObject(i) ?: continue
            val enAyah = englishAyahs?.optJSONObject(i)

            val numberInSurah = arAyah.optInt("numberInSurah", i + 1)
            val overallNumber = arAyah.optInt("number", IslamicDataSource.calculateOverallAyahNumber(surah.number, numberInSurah))
            val textArabic = arAyah.optString("text", "")
            val textEnglish = enAyah?.optString("text", "Verse $numberInSurah of Surah ${surah.nameEnglish}.") ?: ""
            val juz = arAyah.optInt("juz", surah.juzNumber)
            val page = arAyah.optInt("page", surah.startPage)

            list.add(
                Ayah(
                    numberInSurah = numberInSurah,
                    overallNumber = overallNumber,
                    surahNumber = surah.number,
                    arabicText = textArabic,
                    englishTranslation = textEnglish,
                    transliteration = "Ayah $numberInSurah min Surah ${surah.nameEnglish}",
                    juz = juz,
                    page = page
                )
            )
        }
        return list
    }

    private fun parseJuzJson(jsonStr: String, juzNumber: Int): List<Ayah> {
        val root = JSONObject(jsonStr)
        val data = root.optJSONArray("data") ?: return emptyList()
        val arabicObj = data.optJSONObject(0) ?: return emptyList()
        val englishObj = data.optJSONObject(1)

        val arabicAyahs = arabicObj.optJSONArray("ayahs") ?: return emptyList()
        val englishAyahs = englishObj?.optJSONArray("ayahs")

        val list = mutableListOf<Ayah>()
        for (i in 0 until arabicAyahs.length()) {
            val arAyah = arabicAyahs.optJSONObject(i) ?: continue
            val enAyah = englishAyahs?.optJSONObject(i)

            val numberInSurah = arAyah.optInt("numberInSurah", 1)
            val overallNumber = arAyah.optInt("number", 1)
            val surahObj = arAyah.optJSONObject("surah")
            val surahNumber = surahObj?.optInt("number") ?: 1
            val surahNameEnglish = surahObj?.optString("englishName", "Surah") ?: "Surah"
            val textArabic = arAyah.optString("text", "")
            val textEnglish = enAyah?.optString("text", "Verse $numberInSurah of $surahNameEnglish.") ?: ""
            val page = arAyah.optInt("page", 1)

            list.add(
                Ayah(
                    numberInSurah = numberInSurah,
                    overallNumber = overallNumber,
                    surahNumber = surahNumber,
                    arabicText = textArabic,
                    englishTranslation = textEnglish,
                    transliteration = "Surah $surahNameEnglish - Ayah $numberInSurah",
                    juz = juzNumber,
                    page = page
                )
            )
        }
        return list
    }
}
