package com.mindless.screen.domain.usecase

import com.mindless.screen.domain.model.PersonalityType
import org.junit.Assert.assertEquals
import org.junit.Test

class ClassifyPersonalityUseCaseTest {

    private val useCase = ClassifyPersonalityUseCase()

    @Test
    fun classifiesNIGHT_SCROLLER_whenMidnightUsageRatioAtOrAboveThreshold() {
        val result = useCase(
            midnightUsageRatio = 0.40,
            socialUsageRatio = 0.10,
            productivityUsageRatio = 0.10,
            weekendUsageRatio = 0.10
        )

        assertEquals(PersonalityType.NIGHT_SCROLLER, result.type)
        assertEquals(0.40, result.confidence, 0.0)
    }

    @Test
    fun classifiesBALANCED_USER_whenNoThresholdsHit() {
        val result = useCase(
            midnightUsageRatio = 0.20,
            socialUsageRatio = 0.30,
            productivityUsageRatio = 0.30,
            weekendUsageRatio = 0.20
        )

        assertEquals(PersonalityType.BALANCED_USER, result.type)
        assertEquals(0.60, result.confidence, 0.0)
    }
}
