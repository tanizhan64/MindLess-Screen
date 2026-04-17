package com.mindless.screen.domain.model

data class GamificationResult(
    val streakDays: Int,
    val focusLevel: Int,
    val badges: List<String>,
)
