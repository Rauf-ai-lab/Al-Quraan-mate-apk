package com.example.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface DeenDao {
    @Query("SELECT * FROM deen_records WHERE dateString = :dateString")
    fun getRecordForDate(dateString: String): Flow<DeenDayEntity?>

    @Query("SELECT * FROM deen_records WHERE dateString = :dateString")
    suspend fun getRecordForDateSync(dateString: String): DeenDayEntity?

    @Query("SELECT * FROM deen_records ORDER BY dateString DESC LIMIT :limit")
    fun getRecentRecords(limit: Int = 30): Flow<List<DeenDayEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(record: DeenDayEntity)

    @Query("SELECT COUNT(*) FROM deen_records WHERE fastingStatus = 'FASTED'")
    fun getCompletedFastsCount(): Flow<Int>
}

@Dao
interface TasbeehDao {
    @Query("SELECT * FROM tasbeeh_logs ORDER BY timestamp DESC LIMIT 50")
    fun getRecentLogs(): Flow<List<TasbeehLogEntity>>

    @Query("SELECT SUM(count) FROM tasbeeh_logs WHERE dateString = :dateString")
    fun getTotalCountForDate(dateString: String): Flow<Int?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: TasbeehLogEntity)
}

@Dao
interface BookmarkDao {
    @Query("SELECT * FROM bookmarks ORDER BY timestamp DESC")
    fun getAllBookmarks(): Flow<List<BookmarkEntity>>

    @Query("SELECT * FROM bookmarks WHERE type = :type ORDER BY timestamp DESC")
    fun getBookmarksByType(type: String): Flow<List<BookmarkEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM bookmarks WHERE id = :id)")
    fun isBookmarked(id: String): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookmark(bookmark: BookmarkEntity)

    @Query("DELETE FROM bookmarks WHERE id = :id")
    suspend fun deleteBookmark(id: String)
}

@Dao
interface LastReadDao {
    @Query("SELECT * FROM last_read WHERE id = 1")
    fun getLastRead(): Flow<LastReadEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setLastRead(lastRead: LastReadEntity)
}
