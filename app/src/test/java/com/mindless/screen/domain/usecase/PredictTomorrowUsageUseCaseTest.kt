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
}
