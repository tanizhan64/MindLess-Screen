package com.mindless.screen.domain.model

data class GamificationResult(
    val streak: Int,
    val badges: List<String>,
    val focusLevel: Int
)
