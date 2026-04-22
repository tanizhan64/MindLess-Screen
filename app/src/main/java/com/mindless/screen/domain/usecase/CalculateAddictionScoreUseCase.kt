package com.mindless.screen.domain.usecase

import com.mindless.screen.domain.model.AddictionRiskBand
import com.mindless.screen.domain.model.AddictionScoreInput
import com.mindless.screen.domain.model.AddictionScoreResult

class CalculateAddictionScoreUseCase {
    operator fun invoke(input: AddictionScoreInput): AddictionScoreResult {
        val rawScore =
            (0.35 * input.screenScore) +
                (0.20 * input.socialScore) +
                (0.15 * input.unlockScore) +
                (0.10 * input.nightScore) +
                (0.10 * input.gamingScore) +
                (0.10 * input.sessionScore)

        val score = rawScore.coerceIn(0.0, 100.0).toInt()
        val band = when (score) {
            in 0..30 -> AddictionRiskBand.HEALTHY
            in 31..60 -> AddictionRiskBand.MODERATE_USAGE
            in 61..80 -> AddictionRiskBand.RISK_ZONE
            else -> AddictionRiskBand.HIGH_ADDICTION
        }

        return AddictionScoreResult(
            score = score,
            band = band
        )
    }
}
