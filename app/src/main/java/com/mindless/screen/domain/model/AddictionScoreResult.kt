package com.mindless.screen.domain.model

data class AddictionScoreResult(
    val score: Int,
    val riskBand: RiskBand
) {
    enum class RiskBand {
        HEALTHY,
        MODERATE_USAGE,
        RISK_ZONE,
        HIGH_ADDICTION
    }
}
