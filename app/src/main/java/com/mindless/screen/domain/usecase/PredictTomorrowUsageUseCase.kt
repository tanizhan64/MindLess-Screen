package com.mindless.screen.domain.usecase

import com.mindless.screen.domain.model.AddictionRiskBand
import com.mindless.screen.domain.model.TomorrowPrediction

class PredictTomorrowUsageUseCase {

    operator fun invoke(
        last7DaysUsageMinutes: List<Int>,
        weekendFactorMinutes: Int,
        midnightUsageMinutes: Int,
        midnightThresholdMinutes: Int,
        currentRiskBand: AddictionRiskBand
    ): TomorrowPrediction {
        val average = if (last7DaysUsageMinutes.isEmpty()) 0.0 else last7DaysUsageMinutes.average()
        val penaltyPercent = calculatePenaltyPercent(midnightUsageMinutes, midnightThresholdMinutes)

        val predictedUsageMinutes = (
            (0.6 * average) +
                (0.2 * weekendFactorMinutes) +
                (0.2 * average * (penaltyPercent / 100.0))
            ).toInt().coerceAtLeast(0)

        val suggestion =
            if (penaltyPercent > 0.0) {
                "Late-night usage increase detected; consider winding down earlier tonight."
            } else {
                "Your routine looks stable; keep your current healthy routine."
            }

        return TomorrowPrediction(
            predictedUsageMinutes = predictedUsageMinutes,
            riskBand = currentRiskBand,
            suggestion = suggestion
        )
    }

    private fun calculatePenaltyPercent(
        midnightUsageMinutes: Int,
        midnightThresholdMinutes: Int
    ): Double {
        if (midnightUsageMinutes <= midnightThresholdMinutes || midnightThresholdMinutes <= 0) {
            return 0.0
        }

        val exceedance = (midnightUsageMinutes - midnightThresholdMinutes).toDouble()
        val cappedExceedance = exceedance.coerceAtMost(midnightThresholdMinutes.toDouble())
        val exceedanceRatio = cappedExceedance / midnightThresholdMinutes.toDouble()

        return 10.0 + (10.0 * exceedanceRatio)
    }
}
