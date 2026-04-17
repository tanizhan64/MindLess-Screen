package com.mindless.screen.domain.usecase

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

class GenerateSmartInsightsUseCaseTest {

    @Test
    fun throwsWhenSocialRatioIsOutOfRange() {
        val useCase = GenerateSmartInsightsUseCase()

        assertThrows(IllegalArgumentException::class.java) {
            useCase(
                totalMinutes = 200,
                unlockCount = 40,
                socialRatio = 1.1,
                lateNightMinutes = 20,
            )
        }

        assertThrows(IllegalArgumentException::class.java) {
            useCase(
                totalMinutes = 200,
                unlockCount = 40,
                socialRatio = -0.1,
                lateNightMinutes = 20,
            )
        }

        assertThrows(IllegalArgumentException::class.java) {
            useCase(
                totalMinutes = 200,
                unlockCount = 40,
                socialRatio = Double.NaN,
                lateNightMinutes = 20,
            )
        }

        assertThrows(IllegalArgumentException::class.java) {
            useCase(
                totalMinutes = 200,
                unlockCount = 40,
                socialRatio = Double.POSITIVE_INFINITY,
                lateNightMinutes = 20,
            )
        }
    }

    @Test
    fun throwsWhenCountsAreNegative() {
        val useCase = GenerateSmartInsightsUseCase()

        assertThrows(IllegalArgumentException::class.java) {
            useCase(
                totalMinutes = -1,
                unlockCount = 40,
                socialRatio = 0.2,
                lateNightMinutes = 20,
            )
        }

        assertThrows(IllegalArgumentException::class.java) {
            useCase(
                totalMinutes = 200,
                unlockCount = -1,
                socialRatio = 0.2,
                lateNightMinutes = 20,
            )
        }

        assertThrows(IllegalArgumentException::class.java) {
            useCase(
                totalMinutes = 200,
                unlockCount = 40,
                socialRatio = 0.2,
                lateNightMinutes = -1,
            )
        }
    }

    @Test
    fun emitsSocialWarningWhenSocialRatioHigh() {
        val useCase = GenerateSmartInsightsUseCase()

        val cards = useCase(
            totalMinutes = 360,
            unlockCount = 40,
            socialRatio = 0.55,
            lateNightMinutes = 20,
        )

        assertTrue(cards.any { it.type == "social" })
        assertEquals(1, cards.size)
    }

    @Test
    fun emitsUnlockWarningAtThreshold() {
        val useCase = GenerateSmartInsightsUseCase()

        val cards = useCase(
            totalMinutes = 360,
            unlockCount = 120,
            socialRatio = 0.20,
            lateNightMinutes = 20,
        )

        assertTrue(cards.any { it.type == "unlock" })
    }

    @Test
    fun emitsNightWarningAtThreshold() {
        val useCase = GenerateSmartInsightsUseCase()

        val cards = useCase(
            totalMinutes = 360,
            unlockCount = 40,
            socialRatio = 0.20,
            lateNightMinutes = 60,
        )

        assertTrue(cards.any { it.type == "night" })
    }

    @Test
    fun emitsStableInsightWhenNoThresholdsAreExceeded() {
        val useCase = GenerateSmartInsightsUseCase()

        val cards = useCase(
            totalMinutes = 120,
            unlockCount = 40,
            socialRatio = 0.20,
            lateNightMinutes = 20,
        )

        assertEquals(1, cards.size)
        assertEquals("balance", cards.first().type)
    }
}
