package com.mindless.screen.domain.usecase

import com.mindless.screen.domain.model.AddictionRiskBand
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PredictTomorrowUsageUseCaseTest {

    private val useCase = PredictTomorrowUsageUseCase()

    @Test
    fun appliesLateNightPenalty_whenMidnightUsageExceedsThreshold() {
        val result = useCase(
            last7DaysUsageMinutes = listOf(120, 130, 140, 125, 135, 145, 150),
            weekendFactorMinutes = 40,
            midnightUsageMinutes = 75,
            midnightThresholdMinutes = 60,
            currentRiskBand = AddictionRiskBand.RISK_ZONE
        )

        assertTrue(result.predictedUsageMinutes > 0)
        assertEquals(AddictionRiskBand.RISK_ZONE, result.riskBand)
        assertTrue(result.suggestion.contains("late-night", ignoreCase = true))
    }

    @Test
    fun returnsStableSuggestion_whenNoLateNightPenaltyApplies() {
        val result = useCase(
            last7DaysUsageMinutes = listOf(100, 110, 95, 105, 98, 102, 99),
            weekendFactorMinutes = 20,
            midnightUsageMinutes = 45,
            midnightThresholdMinutes = 60,
            currentRiskBand = AddictionRiskBand.MODERATE_USAGE
        )

        assertTrue(result.predictedUsageMinutes > 0)
        assertEquals(AddictionRiskBand.MODERATE_USAGE, result.riskBand)
        assertTrue(result.suggestion.contains("stable", ignoreCase = true))
        assertTrue(result.suggestion.contains("routine", ignoreCase = true))
    }

    @Test
    fun computesExactPrediction_withPenaltyPath() {
        val result = useCase(
            last7DaysUsageMinutes = listOf(100, 100, 100, 100, 100, 100, 100),
            weekendFactorMinutes = 50,
            midnightUsageMinutes = 90,
            midnightThresholdMinutes = 60,
            currentRiskBand = AddictionRiskBand.RISK_ZONE
        )

        assertEquals(73, result.predictedUsageMinutes)
        assertTrue(result.suggestion.contains("late-night", ignoreCase = true))
    }

    @Test
    fun computesExactPrediction_withoutPenaltyPath() {
        val result = useCase(
            last7DaysUsageMinutes = listOf(80, 80, 80, 80, 80, 80, 80),
            weekendFactorMinutes = 40,
            midnightUsageMinutes = 30,
            midnightThresholdMinutes = 60,
            currentRiskBand = AddictionRiskBand.HEALTHY
        )

        assertEquals(56, result.predictedUsageMinutes)
        assertTrue(result.suggestion.contains("stable", ignoreCase = true))
    }

    @Test
    fun doesNotApplyPenalty_whenMidnightUsageEqualsThreshold() {
        val result = useCase(
            last7DaysUsageMinutes = listOf(70, 70, 70, 70, 70, 70, 70),
            weekendFactorMinutes = 20,
            midnightUsageMinutes = 60,
            midnightThresholdMinutes = 60,
            currentRiskBand = AddictionRiskBand.MODERATE_USAGE
        )

        assertEquals(46, result.predictedUsageMinutes)
        assertTrue(result.suggestion.contains("stable", ignoreCase = true))
    }

    @Test
    fun capsPenaltyAtTwentyPercent_forLargeExceedance() {
        val hugeExceedanceResult = useCase(
            last7DaysUsageMinutes = listOf(100, 100, 100, 100, 100, 100, 100),
            weekendFactorMinutes = 50,
            midnightUsageMinutes = 1000,
            midnightThresholdMinutes = 60,
            currentRiskBand = AddictionRiskBand.RISK_ZONE
        )

        val maxBoundaryResult = useCase(
            last7DaysUsageMinutes = listOf(100, 100, 100, 100, 100, 100, 100),
            weekendFactorMinutes = 50,
            midnightUsageMinutes = 120,
            midnightThresholdMinutes = 60,
            currentRiskBand = AddictionRiskBand.RISK_ZONE
        )

        assertEquals(74, hugeExceedanceResult.predictedUsageMinutes)
        assertEquals(74, maxBoundaryResult.predictedUsageMinutes)
        assertEquals(maxBoundaryResult.predictedUsageMinutes, hugeExceedanceResult.predictedUsageMinutes)
    }

    @Test
    fun handlesEmptyLast7DaysList_deterministicallyFromZeroAverage() {
        val result = useCase(
            last7DaysUsageMinutes = emptyList(),
            weekendFactorMinutes = 35,
            midnightUsageMinutes = 120,
            midnightThresholdMinutes = 60,
            currentRiskBand = AddictionRiskBand.RISK_ZONE
        )

        assertEquals(7, result.predictedUsageMinutes)
        assertTrue(result.suggestion.contains("late-night", ignoreCase = true))
    }
}
