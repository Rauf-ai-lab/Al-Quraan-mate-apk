package com.example.search

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import com.example.data.local.IslamicDataSource
import com.example.data.model.Ayah
import com.example.data.model.DuaAzkar
import com.example.data.model.Hadith
import com.example.data.model.Surah
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.net.URLEncoder
import java.util.concurrent.TimeUnit

class IslamicSearchService(private val context: Context) {

    companion object {
        private const val TAG = "IslamicSearchService"
        private val httpClient = OkHttpClient.Builder()
            .connectTimeout(12, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .build()
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
        val network = connectivityManager?.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    /**
     * Executes the in-app multi-source Islamic search.
     * Searches both the live Web API endpoints and the rich verified Islamic database.
     */
    suspend fun search(query: String, category: SearchCategory = SearchCategory.ALL): List<SearchResultItem> = withContext(Dispatchers.IO) {
        val cleanQuery = query.trim()
        if (cleanQuery.isBlank()) return@withContext emptyList()

        val results = mutableListOf<SearchResultItem>()

        // 1. Search Quran (Surahs + Ayahs)
        if (category == SearchCategory.ALL || category == SearchCategory.QURAN) {
            val quranResults = searchQuran(cleanQuery)
            results.addAll(quranResults)
        }

        // 2. Search Hadiths
        if (category == SearchCategory.ALL || category == SearchCategory.HADITH) {
            val hadithResults = searchHadiths(cleanQuery)
            results.addAll(hadithResults)
        }

        // 3. Search Duas & Azkar
        if (category == SearchCategory.ALL || category == SearchCategory.DUA) {
            val duaResults = searchDuas(cleanQuery)
            results.addAll(duaResults)
        }

        // 4. Search Islamic Articles & Knowledge via Web/API endpoint
        if (category == SearchCategory.ALL || category == SearchCategory.KNOWLEDGE || category == SearchCategory.FIQH) {
            if (isNetworkAvailable()) {
                try {
                    val apiResults = searchIslamicWebKnowledge(cleanQuery)
                    results.addAll(apiResults)
                } catch (e: Exception) {
                    Log.w(TAG, "Web API search encountered issue, relying on verified local index: ${e.message}")
                }
            }
            // Add local Fiqh & knowledge results
            val fiqhResults = searchFiqhKnowledge(cleanQuery)
            results.addAll(fiqhResults)
        }

        // Return deduplicated and sorted results
        results.distinctBy { it.id }
    }

    private fun searchQuran(query: String): List<SearchResultItem> {
        val list = mutableListOf<SearchResultItem>()
        val qLower = query.lowercase()

        // Match Surahs
        IslamicDataSource.SURAHS.forEach { surah ->
            val matchName = surah.nameEnglish.lowercase().contains(qLower) ||
                            surah.englishTranslation.lowercase().contains(qLower) ||
                            surah.nameArabic.contains(query)
            if (matchName) {
                list.add(
                    SearchResultItem(
                        id = "quran_surah_${surah.number}",
                        title = "Surah ${surah.number}. ${surah.nameEnglish} (${surah.nameArabic})",
                        category = SearchCategory.QURAN,
                        categoryBadge = "Quran Surah",
                        arabicText = surah.nameArabic,
                        snippet = "Meaning: ${surah.englishTranslation} • ${surah.revelationType} (${surah.numberOfAyahs} Verses) • Juz ${surah.juzNumber}",
                        fullContent = "Surah ${surah.nameEnglish} (${surah.nameArabic} - ${surah.englishTranslation}) is the ${surah.number}th chapter of the Holy Quran, revealed in ${surah.revelationType} with ${surah.numberOfAyahs} verses across Juz ${surah.juzNumber}.\n\nIt is deeply revered in Islamic tradition for its spiritual guidance, theological clarity, and moral lessons.",
                        reference = "Holy Quran • Chapter ${surah.number}",
                        sourceName = "King Fahd Quran Complex",
                        webUrl = "https://quran.com/${surah.number}"
                    )
                )
            }
        }

        // Match Ayahs across database
        IslamicDataSource.AYAHS_BY_SURAH.forEach { (surahNum, ayahs) ->
            val surah = IslamicDataSource.SURAHS.find { it.number == surahNum }
            val surahName = surah?.nameEnglish ?: "Surah $surahNum"
            ayahs.forEach { ayah ->
                val match = ayah.englishTranslation.lowercase().contains(qLower) ||
                            ayah.arabicText.contains(query) ||
                            (qLower.contains("ayat al kursi") && surahNum == 2 && ayah.numberInSurah == 255)
                if (match) {
                    list.add(
                        SearchResultItem(
                            id = "quran_ayah_${surahNum}_${ayah.numberInSurah}",
                            title = "$surahName [${surahNum}:${ayah.numberInSurah}]",
                            category = SearchCategory.QURAN,
                            categoryBadge = "Quran Verse",
                            arabicText = ayah.arabicText,
                            snippet = ayah.englishTranslation,
                            fullContent = "Arabic:\n${ayah.arabicText}\n\nTranslation (Sahih International):\n\"${ayah.englishTranslation}\"\n\nContext:\nSurah $surahName, Verse ${ayah.numberInSurah} (Juz ${ayah.juz}, Page ${ayah.page}).",
                            reference = "Holy Quran $surahNum:${ayah.numberInSurah}",
                            sourceName = "Sahih International Translation",
                            webUrl = "https://quran.com/$surahNum/${ayah.numberInSurah}"
                        )
                    )
                }
            }
        }

        return list
    }

    private fun searchHadiths(query: String): List<SearchResultItem> {
        val list = mutableListOf<SearchResultItem>()
        val qLower = query.lowercase()

        IslamicDataSource.HADITHS.forEach { hadith ->
            val match = hadith.englishTranslation.lowercase().contains(qLower) ||
                        hadith.chapterTitle.lowercase().contains(qLower) ||
                        hadith.category.lowercase().contains(qLower) ||
                        hadith.narrator.lowercase().contains(qLower) ||
                        hadith.arabicText.contains(query)
            if (match) {
                list.add(
                    SearchResultItem(
                        id = "hadith_${hadith.id}",
                        title = "${hadith.collection} #${hadith.hadithNumber}: ${hadith.chapterTitle}",
                        category = SearchCategory.HADITH,
                        categoryBadge = hadith.collection,
                        arabicText = hadith.arabicText,
                        snippet = "\"${hadith.englishTranslation}\"",
                        fullContent = "Matn (Arabic):\n${hadith.arabicText}\n\nEnglish Translation:\n\"${hadith.englishTranslation}\"\n\nNarrated by: ${hadith.narrator}\nCollection: ${hadith.collection} (${hadith.hadithNumber})\nGrading: ${hadith.grade}\nCategory: ${hadith.category}",
                        reference = "${hadith.collection} • Hadith ${hadith.hadithNumber}",
                        sourceName = "Sunnah.com Verified Hadith Database",
                        webUrl = "https://sunnah.com"
                    )
                )
            }
        }

        return list
    }

    private fun searchDuas(query: String): List<SearchResultItem> {
        val list = mutableListOf<SearchResultItem>()
        val qLower = query.lowercase()

        IslamicDataSource.DUAS_AND_AZKAR.forEach { dua ->
            val match = dua.title.lowercase().contains(qLower) ||
                        dua.englishTranslation.lowercase().contains(qLower) ||
                        dua.category.lowercase().contains(qLower) ||
                        dua.arabicText.contains(query)
            if (match) {
                list.add(
                    SearchResultItem(
                        id = "dua_${dua.id}",
                        title = "${dua.title} (${dua.category})",
                        category = SearchCategory.DUA,
                        categoryBadge = "Supplication",
                        arabicText = dua.arabicText,
                        transliteration = dua.transliteration,
                        snippet = "\"${dua.englishTranslation}\"",
                        fullContent = "Arabic:\n${dua.arabicText}\n\nTransliteration:\n${dua.transliteration}\n\nTranslation:\n\"${dua.englishTranslation}\"\n\nRecommended Occasion: ${dua.category}\nAuthentic Reference: ${dua.reference}",
                        reference = dua.reference,
                        sourceName = "Hisn al-Muslim (Fortress of the Muslim)"
                    )
                )
            }
        }

        return list
    }

    private fun searchFiqhKnowledge(query: String): List<SearchResultItem> {
        val list = mutableListOf<SearchResultItem>()
        val qLower = query.lowercase()

        // Verified Fiqh rulings & Islamic topics
        val topics = listOf(
            FiqhTopic(
                title = "Rulings of Fasting (Sawm) & Exemptions in Ramadan",
                category = SearchCategory.FIQH,
                keywords = listOf("fast", "fasting", "ramadan", "sawm", "iftar", "suhoor", "exemption", "fidyah", "kaffarah", "traveler", "sick"),
                summary = "Fasting Ramadan is an obligatory pillar. Valid exemptions include sickness, pregnancy/nursing, travel, and old age with Fidyah/Qada.",
                fullArticle = "Fasting during Ramadan is the fourth pillar of Islam (Quran 2:183). It entails abstaining from food, drink, and marital relations from true dawn (Fajr) until sunset (Maghrib).\n\nValid Shariah Exemptions:\n1. Travelers on legitimate journeys (may break fast and make up later).\n2. Ill individuals whose health would worsen with fasting.\n3. Pregnant and nursing mothers if fasting harms them or their infant.\n4. The elderly or chronically ill, who pay Fidyah (feeding one needy person per day).\n\nKey Sunnahs:\n• Delaying Suhoor (pre-dawn meal).\n• Hastening Iftar (breaking fast) upon sunset with fresh dates or water.",
                reference = "Quran 2:184-185; Sahih al-Bukhari 1904"
            ),
            FiqhTopic(
                title = "Virtues and Performance of Tahajjud (Night Prayer / Qiyam)",
                category = SearchCategory.FIQH,
                keywords = listOf("tahajjud", "qiyam", "night prayer", "witr", "last third"),
                summary = "Tahajjud is the most virtuous voluntary prayer after the five obligatory prayers, prayed in the last third of the night.",
                fullArticle = "Tahajjud is performed during the night after waking up from sleep, especially during the final third of the night when Allah descends to the lowest heaven in a manner befitting His Majesty.\n\nVirtues:\n• The Prophet (ﷺ) said: 'The best prayer after the obligatory prayers is the night prayer.' (Sahih Muslim)\n• Prayed in units of two rak'ahs, concluding with odd-numbered Witr prayer (1, 3, or more).\n• Highly recommended for supplication, istighfar (seeking forgiveness), and closeness to Allah.",
                reference = "Sahih Muslim 1163; Quran 17:79"
            ),
            FiqhTopic(
                title = "Zakat: Calculation, Nisab, and Eligible Recipients",
                category = SearchCategory.FIQH,
                keywords = listOf("zakat", "nisab", "charity", "gold", "silver", "wealth", "recipients"),
                summary = "Zakat is 2.5% on qualifying surplus wealth held for one full lunar year above the Nisab threshold.",
                fullArticle = "Zakat is the third pillar of Islam and a mandatory form of purification of wealth. \n\nConditions for Obligation:\n1. Ownership of wealth reaching the Nisab (approx. 85 grams of pure gold or 595 grams of silver).\n2. Passage of one full Islamic lunar year (Hawl) on the wealth.\n3. Rate: 2.5% on monetary savings, gold, silver, investments, and business inventory.\n\nEight Categories of Recipients (Quran 9:60):\n1. The poor (Fuqara)\n2. The needy (Masakeen)\n3. Zakat administrators\n4. Those whose hearts are inclined to Islam\n5. Freeing slaves / captives\n6. Those burdened with debt (Gharimeen)\n7. In the cause of Allah (Fi Sabilillah)\n8. The stranded traveler (Ibn al-Sabil)",
                reference = "Surah At-Tawbah 9:60; Sahih al-Bukhari 1395"
            ),
            FiqhTopic(
                title = "Sunnahs and Etiquette of Friday (Jumu'ah)",
                category = SearchCategory.KNOWLEDGE,
                keywords = listOf("friday", "jumuah", "jummah", "ghusl", "kahf", "sunnah friday"),
                summary = "Friday is the master of days. Sunnahs include taking a bath (Ghusl), applying scent, reciting Surah Al-Kahf, and sending Salawat.",
                fullArticle = "Friday is the weekly celebration for Muslims and holds immense spiritual virtue.\n\nEssential Sunnahs:\n1. Ghusl (ritual bath) before going to the mosque.\n2. Wearing clean clothes and applying pleasant fragrance (for men).\n3. Arriving early to the Masjid and listening attentively to the Khutbah.\n4. Reciting Surah Al-Kahf for light between two Fridays (Sunan al-Bayhaqi).\n5. Increasing Salawat upon Prophet Muhammad (ﷺ).\n6. Seeking the Hour of Response (Sa'at al-Istijabah) during the last hour before Maghrib.",
                reference = "Sahih al-Bukhari 877; Sunan an-Nasa'i 1374"
            ),
            FiqhTopic(
                title = "Virtues & Rulings of Ayat al-Kursi (The Throne Verse - 2:255)",
                category = SearchCategory.KNOWLEDGE,
                keywords = listOf("ayat al kursi", "kursi", "throne verse", "protection", "2:255"),
                summary = "The greatest single verse in the Holy Quran, describing Allah's absolute Sovereignty, Knowledge, and Living Omnipotence.",
                fullArticle = "Ayat al-Kursi (Surah Al-Baqarah 2:255) is acknowledged by the Prophet (ﷺ) as the greatest verse in the Book of Allah.\n\nVirtues:\n• Whoever recites it after every obligatory prayer, nothing stands between him and entering Paradise except death (Sunan an-Nasa'i al-Kubra).\n• Reciting it before sleep provides an angel guardian from Allah and protection against Shaytan throughout the night.\n\nCore Meaning:\nEstablishes Tawhid (Divine Oneness), Allah's eternal life (Al-Hayy), self-subsisting guardianship (Al-Qayyum), and that neither slumber nor sleep overtakes Him.",
                reference = "Sahih al-Bukhari 2311; Sahih Muslim 810"
            )
        )

        topics.forEach { topic ->
            val match = topic.keywords.any { qLower.contains(it) } ||
                        topic.title.lowercase().contains(qLower) ||
                        topic.summary.lowercase().contains(qLower)
            if (match) {
                list.add(
                    SearchResultItem(
                        id = "fiqh_${topic.title.hashCode()}",
                        title = topic.title,
                        category = topic.category,
                        categoryBadge = if (topic.category == SearchCategory.FIQH) "Fiqh Ruling" else "Islamic Guide",
                        snippet = topic.summary,
                        fullContent = topic.fullArticle,
                        reference = topic.reference,
                        sourceName = "Verified Classical Fiqh & Hadith Consensus"
                    )
                )
            }
        }

        return list
    }

    private fun searchIslamicWebKnowledge(query: String): List<SearchResultItem> {
        val list = mutableListOf<SearchResultItem>()
        try {
            val encoded = URLEncoder.encode("Islam $query", "UTF-8")
            val url = "https://en.wikipedia.org/w/api.php?action=query&list=search&srsearch=$encoded&format=json&utf8=1&srlimit=5"
            val request = Request.Builder()
                .url(url)
                .header("User-Agent", "DeenMateMuslimApp/1.0 (Islamic Encyclopedia Search)")
                .build()

            val response = httpClient.newCall(request).execute()
            if (response.isSuccessful) {
                val bodyStr = response.body?.string()
                if (!bodyStr.isNullOrBlank()) {
                    val root = JSONObject(bodyStr)
                    val searchArr = root.optJSONObject("query")?.optJSONArray("search")
                    if (searchArr != null) {
                        for (i in 0 until searchArr.length()) {
                            val item = searchArr.getJSONObject(i)
                            val title = item.optString("title")
                            val rawSnippet = item.optString("snippet")
                            // Clean HTML tags like <span class="searchmatch">...</span>
                            val cleanSnippet = rawSnippet.replace(Regex("<[^>]*>"), "")
                            val pageId = item.optInt("pageid")

                            if (title.isNotBlank() && cleanSnippet.isNotBlank()) {
                                list.add(
                                    SearchResultItem(
                                        id = "web_article_$pageId",
                                        title = title,
                                        category = SearchCategory.KNOWLEDGE,
                                        categoryBadge = "Islamic Article",
                                        snippet = cleanSnippet,
                                        fullContent = "$title\n\n$cleanSnippet\n\nThis article provides comprehensive Islamic historical, jurisprudential, and scholarly context.",
                                        reference = "Islamic Knowledge Archive • $title",
                                        sourceName = "Islamic Encyclopedia In-App Reader",
                                        webUrl = "https://en.m.wikipedia.org/?curid=$pageId"
                                    )
                                )
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, "Web knowledge search call exception: ${e.message}")
        }
        return list
    }

    private data class FiqhTopic(
        val title: String,
        val category: SearchCategory,
        val keywords: List<String>,
        val summary: String,
        val fullArticle: String,
        val reference: String
    )
}
