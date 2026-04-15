package com.mindless.screen.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "hourly_aggregates")
data class HourlyAggregateEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val hourStartEpochMillis: Long,
    val totalScreenTimeMillis: Long
)
