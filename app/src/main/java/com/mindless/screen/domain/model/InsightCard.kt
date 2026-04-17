package com.mindless.screen.domain.model

data class InsightCard(
    val type: String,
    val severity: String,
    val message: String,
    val recommendation: String,
)
