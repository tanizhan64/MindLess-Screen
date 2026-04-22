package com.mindless.screen.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mindless.screen.data.local.entity.InsightRecordEntity

@Dao
interface InsightDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(records: List<InsightRecordEntity>)

    @Query("DELETE FROM insight_records WHERE dayEpochMillis = :dayEpochMillis")
    suspend fun deleteByDay(dayEpochMillis: Long)

    @Query("SELECT * FROM insight_records WHERE dayEpochMillis = :dayEpochMillis ORDER BY id ASC")
    fun insightsByDay(dayEpochMillis: Long): List<InsightRecordEntity>

    @Query("SELECT dayEpochMillis FROM insight_records ORDER BY dayEpochMillis DESC LIMIT 1")
    fun latestDayEpochMillis(): Long?
}
