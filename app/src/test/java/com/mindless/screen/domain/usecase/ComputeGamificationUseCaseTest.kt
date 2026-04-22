package com.mindless.screen.domain.usecase

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ComputeGamificationUseCaseTest {

    private val useCase = ComputeGamificationUseCase()

    @Test
    fun incrementsStreak_whenFocusCompletedToday() {
        val result = useCase(
            previousStreak = 3,
            completedFocusToday = true
        )

        assertEquals(4, result.streak)
        assertEquals(1, result.focusLevel)
    }

    @Test
    fun addsSilverBadge_whenStreakReachesSevenDays() {
        val result = useCase(
            previousStreak = 6,
            completedFocusToday = true
        )

        assertEquals(7, result.streak)
        assertTrue(result.badges.contains("7-day Silver"))
        assertEquals(2, result.focusLevel)
    }

    @Test
    fun resetsStreakToZero_whenFocusNotCompletedToday() {
        val result = useCase(
            previousStreak = 12,
            completedFocusToday = false
        )

        assertEquals(0, result.streak)
    }

    @Test
    fun addsGoldBadge_whenStreakReachesThirtyDays() {
        val result = useCase(
            previousStreak = 29,
            completedFocusToday = true
        )

        assertEquals(30, result.streak)
        assertTrue(result.badges.contains("30-day Gold"))
    }

    @Test
    fun addsMasterFocusBadge_whenStreakReachesNinetyDays() {
        val result = useCase(
            previousStreak = 89,
            completedFocusToday = true
        )

        assertEquals(90, result.streak)
        assertTrue(result.badges.contains("90-day Master Focus"))
    }
}
