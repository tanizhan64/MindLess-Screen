package com.mindless.screen.domain.usecase

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

class ComputeGamificationUseCaseTest {

    @Test
    fun throwsWhenPreviousStreakIsNegative() {
        val useCase = ComputeGamificationUseCase()

        assertThrows(IllegalArgumentException::class.java) {
            useCase(previousStreak = -1, completedFocusToday = true)
        }
    }

    @Test
    fun awardsSilverBadgeAt7DayStreak() {
        val useCase = ComputeGamificationUseCase()

        val result = useCase(previousStreak = 6, completedFocusToday = true)

        assertEquals(7, result.streakDays)
        assertTrue("7-day Silver" in result.badges)
    }

    @Test
    fun resetsStreakWhenFocusNotCompleted() {
        val useCase = ComputeGamificationUseCase()

        val result = useCase(previousStreak = 9, completedFocusToday = false)

        assertEquals(0, result.streakDays)
        assertTrue(result.badges.isEmpty())
    }
}
