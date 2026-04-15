package com.mindless.screen.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "unlock_events")
data class UnlockEventEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val unlockedAtEpochMillis: Long
)
