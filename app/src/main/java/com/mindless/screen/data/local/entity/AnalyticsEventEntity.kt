package com.mindless.screen.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "analytics_events")
data class AnalyticsEventEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val eventName: String,
    val payloadJson: String,
    val timestampEpochMillis: Long
)
