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

    @Query("DELETE FROM insight_records WHERE generatedAtEpochMillis BETWEEN :dayStartEpochMillis AND :dayEndEpochMillis")
    suspend fun deleteByDay(dayStartEpochMillis: Long, dayEndEpochMillis: Long)

    @Query("SELECT * FROM insight_records ORDER BY generatedAtEpochMillis DESC, id DESC")
    fun latestInsights(): List<InsightRecordEntity>
}
