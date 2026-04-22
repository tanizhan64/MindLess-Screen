package com.mindless.screen.domain.usecase

import com.mindless.screen.domain.model.AddictionRiskBand
import com.mindless.screen.domain.model.AddictionScoreInput
import org.junit.Assert.assertEquals
import org.junit.Test

class CalculateAddictionScoreUseCaseTest {

    private val useCase = CalculateAddictionScoreUseCase()

    @Test
    fun returnsWeightedScoreAndRiskZoneBand_forGivenInput() {
        val result = useCase(
            AddictionScoreInput(
                screenScore = 80,
                socialScore = 70,
                unlockScore = 60,
                nightScore = 50,
                gamingScore = 40,
                sessionScore = 30
            )
        )

        assertEquals(63, result.score)
        assertEquals(AddictionRiskBand.RISK_ZONE, result.band)
    }

    @Test
    fun clampsScoreTo100_forLargeInputs() {
        val result = useCase(
            AddictionScoreInput(
                screenScore = 200,
                socialScore = 200,
                unlockScore = 200,
                nightScore = 200,
                gamingScore = 200,
                sessionScore = 200
            )
        )

        assertEquals(100, result.score)
        assertEquals(AddictionRiskBand.HIGH_ADDICTION, result.band)
    }
}
