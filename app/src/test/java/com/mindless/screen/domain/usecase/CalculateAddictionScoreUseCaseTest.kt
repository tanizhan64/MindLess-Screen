package com.mindless.screen.domain.usecase

import com.mindless.screen.domain.model.AddictionScoreInput
import com.mindless.screen.domain.model.AddictionScoreResult
import org.junit.Assert.assertEquals
import org.junit.Test

class CalculateAddictionScoreUseCaseTest {

    private val useCase = CalculateAddictionScoreUseCase()

    @Test
    fun computesWeightedScore_andMapsToModerateUsageBand() {
        val result = useCase(
            AddictionScoreInput(
                screen = 50,
                social = 50,
                unlock = 50,
                night = 50,
                gaming = 50,
                session = 50
            )
        )

        assertEquals(50, result.score)
        assertEquals(AddictionScoreResult.RiskBand.MODERATE_USAGE, result.riskBand)
    }

    @Test
    fun clampsScoreToUpperBound_whenWeightedResultExceeds100() {
        val result = useCase(
            AddictionScoreInput(
                screen = 200,
                social = 200,
                unlock = 200,
                night = 200,
                gaming = 200,
                session = 200
            )
        )

        assertEquals(100, result.score)
        assertEquals(AddictionScoreResult.RiskBand.HIGH_ADDICTION, result.riskBand)
    }
}
