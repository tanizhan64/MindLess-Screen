package com.mindless.screen.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "personality_snapshots")
data class PersonalitySnapshotEntity(
    @PrimaryKey
    val dayEpochMillis: Long,
    val archetype: String,
    val traitsJson: String,
    val confidence: Double,
    val createdAtEpochMillis: Long
)
