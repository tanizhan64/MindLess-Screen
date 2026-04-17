package com.mindless.screen.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "gamification_states")
data class GamificationStateEntity(
    @PrimaryKey
    val dateEpochMillis: Long,
    val streakDays: Int,
    val focusLevel: Int,
    val badgesJson: String
)
