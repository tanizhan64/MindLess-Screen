package com.mindless.screen.domain.usecase

import com.mindless.screen.domain.model.RiskBand
import com.mindless.screen.domain.model.TomorrowPrediction
import kotlin.math.roundToInt

class PredictTomorrowUsageUseCase {

    operator fun invoke(
        last7DaysUsageMinutes: List<Int>,
        weekendFactorMinutes: Double,
        midnightUsageMinutes: Int,
        midnightUsageThresholdMinutes: Int,
        riskBand: RiskBand
    ): TomorrowPrediction {
        val averageLast7Days = last7DaysUsageMinutes.average()
        val lateNightPenaltyComponent = calculateLateNightPenaltyComponent(
            averageLast7Days = averageLast7Days,
            midnightUsageMinutes = midnightUsageMinutes,
            midnightUsageThresholdMinutes = midnightUsageThresholdMinutes
        )

        val predictedMinutes = (
            (0.6 * averageLast7Days) +
                (0.2 * weekendFactorMinutes) +
                (0.2 * lateNightPenaltyComponent)
            ).roundToInt()

        val suggestion = if (lateNightPenaltyComponent > 0.0) {
            "Reduce late-night usage to lower tomorrow's screen time."
        } else {
            "Keep your current nighttime routine."
        }

        return TomorrowPrediction(
            predictedMinutes = predictedMinutes,
            riskBand = riskBand,
            suggestion = suggestion
        )
    }

    private fun calculateLateNightPenaltyComponent(
        averageLast7Days: Double,
        midnightUsageMinutes: Int,
        midnightUsageThresholdMinutes: Int
    ): Double {
        if (midnightUsageMinutes <= midnightUsageThresholdMinutes || midnightUsageThresholdMinutes <= 0) {
            return 0.0
        }

        val exceedanceRatio = (
            (midnightUsageMinutes - midnightUsageThresholdMinutes).toDouble() /
                midnightUsageThresholdMinutes.toDouble()
            ).coerceIn(0.0, 1.0)

        val penaltyRate = 0.10 + (0.10 * exceedanceRatio)
        return averageLast7Days * penaltyRate
    }
}
