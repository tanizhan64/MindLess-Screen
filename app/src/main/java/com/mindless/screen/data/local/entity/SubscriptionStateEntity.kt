package com.mindless.screen.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "subscription_state")
data class SubscriptionStateEntity(
    @PrimaryKey
    val key: String = "active_state",
    val isPremium: Boolean,
    val updatedAtEpochMillis: Long
)
