package com.mindless.screen.domain.usecase

import com.mindless.screen.domain.model.RiskBand
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class PredictTomorrowUsageUseCaseTest {

    @Test
    fun throwsWhenUsageHistoryIsEmpty() {
        val useCase = PredictTomorrowUsageUseCase()

        assertThrows(IllegalArgumentException::class.java) {
            useCase(
                last7DaysUsageMinutes = emptyList(),
                weekendFactorMinutes = 130.0,
                midnightUsageMinutes = 30,
                midnightUsageThresholdMinutes = 30,
                riskBand = RiskBand.MODERATE
            )
        }
    }

    @Test
    fun throwsWhenUsageHistoryDoesNotContainSevenDays() {
        val useCase = PredictTomorrowUsageUseCase()

        assertThrows(IllegalArgumentException::class.java) {
            useCase(
                last7DaysUsageMinutes = listOf(100, 120, 80, 90, 110, 95),
                weekendFactorMinutes = 130.0,
                midnightUsageMinutes = 30,
                midnightUsageThresholdMinutes = 30,
                riskBand = RiskBand.MODERATE
            )
        }
    }

    @Test
    fun doesNotApplyLateNightPenalty_whenThresholdIsNotPositive() {
        val useCase = PredictTomorrowUsageUseCase()

        val prediction = useCase(
            last7DaysUsageMinutes = listOf(100, 120, 80, 90, 110, 95, 105),
            weekendFactorMinutes = 130.0,
            midnightUsageMinutes = 80,
            midnightUsageThresholdMinutes = 0,
            riskBand = RiskBand.MODERATE
        )

        assertEquals(86, prediction.predictedMinutes)
        assertEquals(RiskBand.MODERATE, prediction.riskBand)
        assertEquals("Keep your current nighttime routine.", prediction.suggestion)
    }

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

    @Test
    fun capsLateNightPenalty_whenMidnightUsageGreatlyExceedsThreshold() {
        val useCase = PredictTomorrowUsageUseCase()

        val prediction = useCase(
            last7DaysUsageMinutes = listOf(100, 120, 80, 90, 110, 95, 105),
            weekendFactorMinutes = 130.0,
            midnightUsageMinutes = 120,
            midnightUsageThresholdMinutes = 30,
            riskBand = RiskBand.HIGH
        )

        assertEquals(90, prediction.predictedMinutes)
        assertEquals(RiskBand.HIGH, prediction.riskBand)
        assertEquals("Reduce late-night usage to lower tomorrow's screen time.", prediction.suggestion)
    }
}
