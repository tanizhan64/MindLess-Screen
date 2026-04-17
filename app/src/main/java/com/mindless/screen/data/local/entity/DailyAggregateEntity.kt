package com.mindless.screen.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_aggregates")
data class DailyAggregateEntity(
    @PrimaryKey
    val dayEpochMillis: Long,
    val totalScreenTimeMillis: Long,
    val unlockCount: Int,
    val addictionScore: Double,
    val focusTimeMillis: Long,
    val distractionTimeMillis: Long
)
