package com.mindless.screen.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "session_records")
data class SessionRecordEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val startEpochMillis: Long,
    val endEpochMillis: Long,
    val durationMillis: Long,
    val dominantApp: String,
    val lateNightFlag: Boolean,
    val startReason: String,
    val endReason: String
)
