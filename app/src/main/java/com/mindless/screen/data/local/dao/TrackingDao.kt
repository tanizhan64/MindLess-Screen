package com.mindless.screen.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mindless.screen.data.local.entity.AppUsageRecordEntity
import com.mindless.screen.data.local.entity.SessionRecordEntity
import com.mindless.screen.data.local.entity.UnlockEventEntity

@Dao
interface TrackingDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsageRecord(record: AppUsageRecordEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSessionRecord(record: SessionRecordEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUnlockEvent(event: UnlockEventEntity)

    @Query("SELECT COALESCE(MAX(dayEpochMillis), 0) FROM daily_aggregates")
    fun latestDailyAggregateEpochMillis(): Long
}
