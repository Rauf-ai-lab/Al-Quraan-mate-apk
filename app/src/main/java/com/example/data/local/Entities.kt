package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "deen_records")
data class DeenDayEntity(
    @PrimaryKey val dateString: String, // "YYYY-MM-DD"
    val fajrCompleted: Boolean = false,
    val dhuhrCompleted: Boolean = false,
    val asrCompleted: Boolean = false,
    val maghribCompleted: Boolean = false,
    val ishaCompleted: Boolean = false,
    val quranMinutes: Int = 0,
    val quranReadingsCount: Int = 0,
    val morningAzkarCompleted: Boolean = false,
    val eveningAzkarCompleted: Boolean = false,
    val tasbeehCount: Int = 0,
    val fastingStatus: String = "NONE", // "NONE", "FASTED", "MISSED", "EXCUSED"
    val extraDeedsCount: Int = 0,
    val taraweehCompleted: Boolean = false,
    val tahajjudCompleted: Boolean = false
) {
    fun calculateScore(): Int {
        var score = 0
        if (fajrCompleted) score += 15
        if (dhuhrCompleted) score += 15
        if (asrCompleted) score += 15
        if (maghribCompleted) score += 15
        if (ishaCompleted) score += 15
        if (quranMinutes >= 10) score += 10
        if (morningAzkarCompleted) score += 5
        if (eveningAzkarCompleted) score += 5
        if (tasbeehCount >= 33) score += 5
        if (fastingStatus == "FASTED") score += 5
        return score.coerceAtMost(100)
    }
}

@Entity(tableName = "tasbeeh_logs")
data class TasbeehLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val dhikrTitle: String,
    val count: Int,
    val target: Int,
    val dateString: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "bookmarks")
data class BookmarkEntity(
    @PrimaryKey val id: String, // e.g. "quran_1_1", "hadith_bukhari_1", "dua_morning_1"
    val type: String,           // "QURAN", "HADITH", "DUA"
    val title: String,
    val subtitle: String,
    val arabicSnippet: String,
    val englishSnippet: String,
    val destinationData: String, // Surah number or item ID
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "last_read")
data class LastReadEntity(
    @PrimaryKey val id: Int = 1,
    val surahNumber: Int,
    val ayahNumber: Int,
    val surahNameArabic: String,
    val surahNameEnglish: String,
    val timestamp: Long = System.currentTimeMillis()
)
