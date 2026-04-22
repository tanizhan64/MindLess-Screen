package com.mindless.screen.domain.usecase

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class UnlockAnalyticsUseCaseTest {

    @Test
    fun doesNotEmitAlert_whenUnlockCountIsBelowThreshold() {
        val useCase = UnlockAnalyticsUseCase(staticFallbackThreshold = 120)

        val result = useCase(
            todayUnlockCount = 119,
            recentAverageUnlocks = 100.0
        )

        assertFalse(result.shouldNotify)
        assertNull(result.message)
    }

    @Test
    fun emitsAlert_whenUnlockCountEqualsThreshold() {
        val useCase = UnlockAnalyticsUseCase(staticFallbackThreshold = 120)

        val result = useCase(
            todayUnlockCount = 120,
            recentAverageUnlocks = 100.0
        )

        assertTrue(result.shouldNotify)
        assertEquals("You unlocked your phone 120 times today.", result.message)
    }

    @Test
    fun emitsAlert_whenDynamicThresholdDominatesStaticFallback() {
        val useCase = UnlockAnalyticsUseCase(staticFallbackThreshold = 120)

        val result = useCase(
            todayUnlockCount = 180,
            recentAverageUnlocks = 150.0
        )

        assertTrue(result.shouldNotify)
        assertEquals("You unlocked your phone 180 times today.", result.message)
    }

    @Test
    fun emitsAlert_whenStaticFallbackThresholdDominatesDynamicThreshold() {
        val useCase = UnlockAnalyticsUseCase(staticFallbackThreshold = 120)

        val result = useCase(
            todayUnlockCount = 120,
            recentAverageUnlocks = 80.0
        )

        assertTrue(result.shouldNotify)
        assertEquals("You unlocked your phone 120 times today.", result.message)
    }
}
