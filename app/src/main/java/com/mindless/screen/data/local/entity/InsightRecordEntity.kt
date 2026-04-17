package com.mindless.screen.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "insight_records")
data class InsightRecordEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val dateEpochMillis: Long,
    val type: String,
    val severity: Int,
    val message: String,
    val recommendation: String
)
