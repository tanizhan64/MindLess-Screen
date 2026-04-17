package com.mindless.screen.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "personality_snapshots")
data class PersonalitySnapshotEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val dateEpochMillis: Long,
    val personalityType: String,
    val confidence: Float
)
