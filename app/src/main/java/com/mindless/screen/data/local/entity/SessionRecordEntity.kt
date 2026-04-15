package com.mindless.screen.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "session_records")
data class SessionRecordEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val sessionStartEpochMillis: Long,
    val sessionEndEpochMillis: Long?
)
