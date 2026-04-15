package com.mindless.screen.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_aggregates")
data class DailyAggregateEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val dayStartEpochMillis: Long,
    val totalScreenTimeMillis: Long
)
