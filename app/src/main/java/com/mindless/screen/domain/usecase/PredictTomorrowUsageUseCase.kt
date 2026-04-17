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
        require(last7DaysUsageMinutes.isNotEmpty()) { "last7DaysUsageMinutes must not be empty" }
        require(last7DaysUsageMinutes.size == USAGE_HISTORY_DAYS) {
            "last7DaysUsageMinutes must contain exactly $USAGE_HISTORY_DAYS values"
        }

        val averageLast7Days = last7DaysUsageMinutes.average()
        val lateNightPenaltyComponent = calculateLateNightPenaltyComponent(
            averageLast7Days = averageLast7Days,
            midnightUsageMinutes = midnightUsageMinutes,
            midnightUsageThresholdMinutes = midnightUsageThresholdMinutes
        )

        val predictedMinutes = (
            (BASE_USAGE_WEIGHT * averageLast7Days) +
                (WEEKEND_ADJUSTMENT_WEIGHT * weekendFactorMinutes) +
                (LATE_NIGHT_COMPONENT_WEIGHT * lateNightPenaltyComponent)
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
            ).coerceIn(MIN_EXCEEDANCE_RATIO, MAX_EXCEEDANCE_RATIO)

        val penaltyRate = BASE_LATE_NIGHT_PENALTY_RATE + (VARIABLE_LATE_NIGHT_PENALTY_RATE * exceedanceRatio)
        return averageLast7Days * penaltyRate
    }

    companion object {
        private const val USAGE_HISTORY_DAYS = 7
        private const val BASE_USAGE_WEIGHT = 0.6
        private const val WEEKEND_ADJUSTMENT_WEIGHT = 0.2
        private const val LATE_NIGHT_COMPONENT_WEIGHT = 0.2
        private const val MIN_EXCEEDANCE_RATIO = 0.0
        private const val MAX_EXCEEDANCE_RATIO = 1.0
        private const val BASE_LATE_NIGHT_PENALTY_RATE = 0.10
        private const val VARIABLE_LATE_NIGHT_PENALTY_RATE = 0.10
    }
}
