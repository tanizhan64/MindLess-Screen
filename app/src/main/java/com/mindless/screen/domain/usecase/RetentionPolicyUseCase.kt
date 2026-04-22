package com.mindless.screen.domain.usecase

import java.time.Clock
import java.time.Instant
import java.time.temporal.ChronoUnit

data class RetentionPolicy(
    val rawCutoffUtc: Instant,
    val hourlyCutoffUtc: Instant,
    val dailyCutoffUtc: Instant?
)

class RetentionPolicyUseCase(
    private val clock: Clock = Clock.systemUTC()
) {
    operator fun invoke(): RetentionPolicy {
        val now = Instant.now(clock)

        return RetentionPolicy(
            rawCutoffUtc = now.minus(30, ChronoUnit.DAYS),
            hourlyCutoffUtc = now.minus(90, ChronoUnit.DAYS),
            dailyCutoffUtc = null
        )
    }
}
