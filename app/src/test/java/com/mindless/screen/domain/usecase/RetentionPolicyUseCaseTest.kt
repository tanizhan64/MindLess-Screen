package com.mindless.screen.domain.usecase

import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class RetentionPolicyUseCaseTest {

    @Test
    fun computesExpectedCutoffsFromFixedClock() {
        val fixedClock = Clock.fixed(Instant.parse("2026-04-15T12:00:00Z"), ZoneOffset.UTC)
        val useCase = RetentionPolicyUseCase(clock = fixedClock)

        val policy = useCase()

        assertEquals(Instant.parse("2026-03-16T12:00:00Z"), policy.rawCutoffUtc)
        assertEquals(Instant.parse("2026-01-15T12:00:00Z"), policy.hourlyCutoffUtc)
        assertNull(policy.dailyCutoffUtc)
    }
}
