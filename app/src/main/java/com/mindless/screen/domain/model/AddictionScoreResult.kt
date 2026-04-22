package com.mindless.screen.domain.model

data class AddictionScoreResult(
    val score: Int,
    val band: AddictionRiskBand
)

enum class AddictionRiskBand {
    HEALTHY,
    MODERATE_USAGE,
    RISK_ZONE,
    HIGH_ADDICTION
}
