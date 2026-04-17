package com.mindless.screen.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import com.mindless.screen.data.local.entity.DailyAggregateEntity

@Dao
interface AggregateDao {
    @Query("SELECT * FROM daily_aggregates ORDER BY dayEpochMillis DESC LIMIT 1")
    fun latestDailyAggregate(): DailyAggregateEntity?
}
