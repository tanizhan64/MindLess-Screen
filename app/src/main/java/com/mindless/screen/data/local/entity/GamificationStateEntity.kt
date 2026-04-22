package com.mindless.screen.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "gamification_states")
data class GamificationStateEntity(
    @PrimaryKey
    val key: String = "active_state",
    val level: Int,
    val points: Int,
    val streakDays: Int,
    val updatedAtEpochMillis: Long
)
