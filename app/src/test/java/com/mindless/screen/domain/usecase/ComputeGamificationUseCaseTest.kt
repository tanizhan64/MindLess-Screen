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
}
