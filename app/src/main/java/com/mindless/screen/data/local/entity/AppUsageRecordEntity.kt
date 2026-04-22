package com.mindless.screen.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "app_usage_records")
data class AppUsageRecordEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val packageName: String,
    val dateEpochMillis: Long,
    val totalForegroundMillis: Long,
    val launchCount: Int,
    val hourlyBucket: Int,
    val category: String,
    val source: String,
    val confidence: Double,
    val wasBackgrounded: Boolean,
    val recordedAtEpochMillis: Long
)
