package com.mindless.screen.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mindless.screen.data.local.entity.InsightRecordEntity

@Dao
interface InsightDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInsight(record: InsightRecordEntity)

    @Query("SELECT * FROM insight_records ORDER BY generatedAtEpochMillis DESC LIMIT 1")
    fun latestInsight(): InsightRecordEntity?
}
