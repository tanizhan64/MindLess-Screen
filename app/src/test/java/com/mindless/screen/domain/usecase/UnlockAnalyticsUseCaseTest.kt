package com.mindless.screen.domain.usecase

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class UnlockAnalyticsUseCaseTest {

    @Test
    fun emitsAlert_whenUnlockCountExceedsDynamicThreshold() {
        val useCase = UnlockAnalyticsUseCase(staticFallbackThreshold = 120)

        val result = useCase(
            todayUnlockCount = 132,
            recentAverageUnlocks = 100.0
        )

        assertTrue(result.shouldNotify)
        assertEquals("You unlocked your phone 132 times today.", result.message)
    }
}
