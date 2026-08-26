package com.example.data.repository

import com.example.data.local.*
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.*

class DeenRepository(
    private val appDatabase: AppDatabase
) {
    private val deenDao = appDatabase.deenDao()
    private val tasbeehDao = appDatabase.tasbeehDao()
    private val bookmarkDao = appDatabase.bookmarkDao()
    private val lastReadDao = appDatabase.lastReadDao()

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    fun getTodayDateString(): String = dateFormat.format(Date())

    fun getTodayDeenRecord(): Flow<DeenDayEntity?> = deenDao.getRecordForDate(getTodayDateString())

    fun getDeenRecordForDate(dateString: String): Flow<DeenDayEntity?> = deenDao.getRecordForDate(dateString)

    fun getRecentDeenRecords(days: Int = 30): Flow<List<DeenDayEntity>> = deenDao.getRecentRecords(days)

    suspend fun togglePrayer(prayerName: String, dateString: String = getTodayDateString()) {
        val current = deenDao.getRecordForDateSync(dateString) ?: DeenDayEntity(dateString = dateString)
        val updated = when (prayerName.lowercase()) {
            "fajr" -> current.copy(fajrCompleted = !current.fajrCompleted)
            "dhuhr" -> current.copy(dhuhrCompleted = !current.dhuhrCompleted)
            "asr" -> current.copy(asrCompleted = !current.asrCompleted)
            "maghrib" -> current.copy(maghribCompleted = !current.maghribCompleted)
            "isha" -> current.copy(ishaCompleted = !current.ishaCompleted)
            "morning_azkar" -> current.copy(morningAzkarCompleted = !current.morningAzkarCompleted)
            "evening_azkar" -> current.copy(eveningAzkarCompleted = !current.eveningAzkarCompleted)
            "taraweeh" -> current.copy(taraweehCompleted = !current.taraweehCompleted)
            "tahajjud" -> current.copy(tahajjudCompleted = !current.tahajjudCompleted)
            else -> current
        }
        deenDao.insertOrUpdate(updated)
    }

    suspend fun updateQuranMinutes(minutes: Int, dateString: String = getTodayDateString()) {
        val current = deenDao.getRecordForDateSync(dateString) ?: DeenDayEntity(dateString = dateString)
        deenDao.insertOrUpdate(current.copy(quranMinutes = minutes))
    }

    suspend fun incrementQuranReading(dateString: String = getTodayDateString()) {
        val current = deenDao.getRecordForDateSync(dateString) ?: DeenDayEntity(dateString = dateString)
        deenDao.insertOrUpdate(current.copy(quranReadingsCount = current.quranReadingsCount + 1))
    }

    suspend fun updateQuranReadingsCount(count: Int, dateString: String = getTodayDateString()) {
        val current = deenDao.getRecordForDateSync(dateString) ?: DeenDayEntity(dateString = dateString)
        deenDao.insertOrUpdate(current.copy(quranReadingsCount = count.coerceAtLeast(0)))
    }

    suspend fun updateFastingStatus(status: String, dateString: String = getTodayDateString()) {
        val current = deenDao.getRecordForDateSync(dateString) ?: DeenDayEntity(dateString = dateString)
        deenDao.insertOrUpdate(current.copy(fastingStatus = status))
    }

    suspend fun addTasbeehCount(dhikrTitle: String, count: Int, target: Int) {
        val today = getTodayDateString()
        tasbeehDao.insertLog(
            TasbeehLogEntity(
                dhikrTitle = dhikrTitle,
                count = count,
                target = target,
                dateString = today
            )
        )
        val current = deenDao.getRecordForDateSync(today) ?: DeenDayEntity(dateString = today)
        deenDao.insertOrUpdate(current.copy(tasbeehCount = current.tasbeehCount + count))
    }

    fun getTodayTasbeehTotal(): Flow<Int?> = tasbeehDao.getTotalCountForDate(getTodayDateString())

    fun getAllBookmarks(): Flow<List<BookmarkEntity>> = bookmarkDao.getAllBookmarks()

    fun isBookmarked(id: String): Flow<Boolean> = bookmarkDao.isBookmarked(id)

    suspend fun toggleBookmark(bookmark: BookmarkEntity) {
        bookmarkDao.insertBookmark(bookmark)
    }

    suspend fun removeBookmark(id: String) {
        bookmarkDao.deleteBookmark(id)
    }

    fun getLastRead(): Flow<LastReadEntity?> = lastReadDao.getLastRead()

    suspend fun setLastRead(surahNumber: Int, ayahNumber: Int, nameAr: String, nameEn: String) {
        lastReadDao.setLastRead(
            LastReadEntity(
                surahNumber = surahNumber,
                ayahNumber = ayahNumber,
                surahNameArabic = nameAr,
                surahNameEnglish = nameEn
            )
        )
    }

    fun getCompletedFastsCount(): Flow<Int> = deenDao.getCompletedFastsCount()
}
