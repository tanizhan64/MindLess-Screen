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

    @Test
    fun mapsBoundaryTransitionsToExpectedBands() {
        val score30 = useCase(
            AddictionScoreInput(
                screenScore = 87,
                socialScore = 0,
                unlockScore = 0,
                nightScore = 0,
                gamingScore = 0,
                sessionScore = 0
            )
        )
        val score31 = useCase(
            AddictionScoreInput(
                screenScore = 89,
                socialScore = 0,
                unlockScore = 0,
                nightScore = 0,
                gamingScore = 0,
                sessionScore = 0
            )
        )
        val score60 = useCase(
            AddictionScoreInput(
                screenScore = 172,
                socialScore = 0,
                unlockScore = 0,
                nightScore = 0,
                gamingScore = 0,
                sessionScore = 0
            )
        )
        val score61 = useCase(
            AddictionScoreInput(
                screenScore = 175,
                socialScore = 0,
                unlockScore = 0,
                nightScore = 0,
                gamingScore = 0,
                sessionScore = 0
            )
        )
        val score80 = useCase(
            AddictionScoreInput(
                screenScore = 229,
                socialScore = 0,
                unlockScore = 0,
                nightScore = 0,
                gamingScore = 0,
                sessionScore = 0
            )
        )
        val score81 = useCase(
            AddictionScoreInput(
                screenScore = 232,
                socialScore = 0,
                unlockScore = 0,
                nightScore = 0,
                gamingScore = 0,
                sessionScore = 0
            )
        )

        assertEquals(30, score30.score)
        assertEquals(AddictionRiskBand.HEALTHY, score30.band)

        assertEquals(31, score31.score)
        assertEquals(AddictionRiskBand.MODERATE_USAGE, score31.band)

        assertEquals(60, score60.score)
        assertEquals(AddictionRiskBand.MODERATE_USAGE, score60.band)

        assertEquals(61, score61.score)
        assertEquals(AddictionRiskBand.RISK_ZONE, score61.band)

        assertEquals(80, score80.score)
        assertEquals(AddictionRiskBand.RISK_ZONE, score80.band)

        assertEquals(81, score81.score)
        assertEquals(AddictionRiskBand.HIGH_ADDICTION, score81.band)
    }

    @Test
    fun clampsScoreToZeroAndHealthyBand_forStronglyNegativeInputs() {
        val result = useCase(
            AddictionScoreInput(
                screenScore = -500,
                socialScore = -500,
                unlockScore = -500,
                nightScore = -500,
                gamingScore = -500,
                sessionScore = -500
            )
        )

        assertEquals(0, result.score)
        assertEquals(AddictionRiskBand.HEALTHY, result.band)
    }

    @Test
    fun floorsDecimalRawScore_usingToIntContract() {
        val result = useCase(
            AddictionScoreInput(
                screenScore = 80,
                socialScore = 10,
                unlockScore = 2,
                nightScore = 4,
                gamingScore = 0,
                sessionScore = 0
            )
        )

        assertEquals(30, result.score)
        assertEquals(AddictionRiskBand.HEALTHY, result.band)
    }
}
