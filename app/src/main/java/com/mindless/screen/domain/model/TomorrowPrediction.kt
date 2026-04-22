package com.mindless.screen.domain.model

data class TomorrowPrediction(
    val predictedUsageMinutes: Int,
    val riskBand: AddictionRiskBand,
    val suggestion: String
)
