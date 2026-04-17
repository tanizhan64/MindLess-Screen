package com.mindless.screen.domain.usecase

import com.mindless.screen.domain.model.RiskBand
import org.junit.Assert.assertEquals
import org.junit.Test

class PredictTomorrowUsageUseCaseTest {

    @Test
    fun returnsPredictionWithoutLateNightPenalty_whenMidnightUsageIsWithinThreshold() {
        val useCase = PredictTomorrowUsageUseCase()

        val prediction = useCase(
            last7DaysUsageMinutes = listOf(100, 120, 80, 90, 110, 95, 105),
            weekendFactorMinutes = 130.0,
            midnightUsageMinutes = 30,
            midnightUsageThresholdMinutes = 30,
            riskBand = RiskBand.MODERATE
        )

        assertEquals(86, prediction.predictedMinutes)
        assertEquals(RiskBand.MODERATE, prediction.riskBand)
        assertEquals("Keep your current nighttime routine.", prediction.suggestion)
    }

    @Test
    fun appliesLinearLateNightPenalty_whenMidnightUsageExceedsThreshold() {
        val useCase = PredictTomorrowUsageUseCase()

        val prediction = useCase(
            last7DaysUsageMinutes = listOf(100, 120, 80, 90, 110, 95, 105),
            weekendFactorMinutes = 130.0,
            midnightUsageMinutes = 45,
            midnightUsageThresholdMinutes = 30,
            riskBand = RiskBand.HIGH
        )

        assertEquals(89, prediction.predictedMinutes)
        assertEquals(RiskBand.HIGH, prediction.riskBand)
        assertEquals("Reduce late-night usage to lower tomorrow's screen time.", prediction.suggestion)
    }
}
