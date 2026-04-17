package com.mindless.screen.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "hourly_aggregates")
data class HourlyAggregateEntity(
    @PrimaryKey
    val hourEpochMillis: Long,
    val totalScreenTimeMillis: Long,
    val unlockCount: Int,
    val dominantCategory: String
)
