package com.mindless.screen.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mindless.screen.data.local.entity.InsightRecordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface InsightDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInsight(record: InsightRecordEntity)

    @Query("SELECT * FROM insight_records WHERE dateEpochMillis = :dateEpochMillis ORDER BY id DESC")
    fun getInsightsForDate(dateEpochMillis: Long): Flow<List<InsightRecordEntity>>
}
