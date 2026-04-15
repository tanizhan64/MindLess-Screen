package com.mindless.screen.domain.model

data class StartupState(
    val canTrackUsage: Boolean,
    val isPremium: Boolean,
    val shouldTriggerBackgroundAggregation: Boolean
)
