package com.mindless.screen.domain.usecase

import com.mindless.screen.domain.model.AddictionScoreInput
import com.mindless.screen.domain.model.AddictionScoreResult
import kotlin.math.roundToInt

class CalculateAddictionScoreUseCase {

    operator fun invoke(input: AddictionScoreInput): AddictionScoreResult {
        val score = (
            0.35 * input.screen +
                0.20 * input.social +
                0.15 * input.unlock +
                0.10 * input.night +
                0.10 * input.gaming +
                0.10 * input.session
            ).roundToInt().coerceIn(0, 100)

        val riskBand = when (score) {
            in 0..30 -> AddictionScoreResult.RiskBand.HEALTHY
            in 31..60 -> AddictionScoreResult.RiskBand.MODERATE_USAGE
            in 61..80 -> AddictionScoreResult.RiskBand.RISK_ZONE
            else -> AddictionScoreResult.RiskBand.HIGH_ADDICTION
        }

        return AddictionScoreResult(
            score = score,
            riskBand = riskBand
        )
    }
}
