package com.mindless.screen.domain.usecase

import com.mindless.screen.domain.model.InsightSeverity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GenerateSmartInsightsUseCaseTest {

    private val useCase = GenerateSmartInsightsUseCase()

    @Test
    fun emitsSocialWarning_whenSocialRatioAtLeastFiftyPercent() {
        val result = useCase(
            totalMinutes = 240,
            unlockCount = 50,
            socialRatio = 0.50,
            lateNightMinutes = 20
        )

        assertTrue(result.any { it.severity == InsightSeverity.WARNING && it.message.contains("social", ignoreCase = true) })
    }

    @Test
    fun emitsUnlockWarningWithCount_whenUnlockCountAtLeastThreshold() {
        val result = useCase(
            totalMinutes = 180,
            unlockCount = 120,
            socialRatio = 0.20,
            lateNightMinutes = 10
        )

        val unlockWarning = result.first { it.message.contains("unlock", ignoreCase = true) }
        assertEquals(InsightSeverity.WARNING, unlockWarning.severity)
        assertTrue(unlockWarning.message.contains("120"))
    }

    @Test
    fun emitsLateNightWarning_whenLateNightMinutesAtLeastSixty() {
        val result = useCase(
            totalMinutes = 200,
            unlockCount = 40,
            socialRatio = 0.25,
            lateNightMinutes = 60
        )

        assertTrue(
            result.any {
                it.severity == InsightSeverity.WARNING &&
                    it.message.contains("late-night", ignoreCase = true)
            }
        )
    }

    @Test
    fun emitsFallbackBalanceInsight_whenNoWarningConditionMatches() {
        val result = useCase(
            totalMinutes = 90,
            unlockCount = 30,
            socialRatio = 0.20,
            lateNightMinutes = 15
        )

        assertEquals(1, result.size)
        assertEquals(InsightSeverity.INFO, result.first().severity)
        assertTrue(result.first().message.contains("balance", ignoreCase = true))
    }
}
