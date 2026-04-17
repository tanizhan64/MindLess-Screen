package com.mindless.screen.domain.model

data class TomorrowPrediction(
    val predictedMinutes: Int,
    val riskBand: RiskBand,
    val suggestion: String
)

enum class RiskBand {
    LOW,
    MODERATE,
    HIGH
}
