package com.mindless.screen.domain.usecase

import com.mindless.screen.domain.model.PersonalityType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class ClassifyPersonalityUseCaseTest {

    @Test
    fun throwsWhenAnyRatioIsOutOfRange() {
        val useCase = ClassifyPersonalityUseCase()

        assertThrows(IllegalArgumentException::class.java) {
            useCase(
                midnightUsageRatio = 1.01,
                socialUsageRatio = 0.30,
                productivityUsageRatio = 0.10,
                weekendSpikeRatio = 0.15,
            )
        }

        assertThrows(IllegalArgumentException::class.java) {
            useCase(
                midnightUsageRatio = 0.20,
                socialUsageRatio = -0.01,
                productivityUsageRatio = 0.10,
                weekendSpikeRatio = 0.15,
            )
        }
    }

    @Test
    fun throwsWhenAnyRatioIsNotFinite() {
        val useCase = ClassifyPersonalityUseCase()

        assertThrows(IllegalArgumentException::class.java) {
            useCase(
                midnightUsageRatio = Double.NaN,
                socialUsageRatio = 0.30,
                productivityUsageRatio = 0.10,
                weekendSpikeRatio = 0.15,
            )
        }

        assertThrows(IllegalArgumentException::class.java) {
            useCase(
                midnightUsageRatio = 0.20,
                socialUsageRatio = Double.POSITIVE_INFINITY,
                productivityUsageRatio = 0.10,
                weekendSpikeRatio = 0.15,
            )
        }
    }

    @Test
    fun classifiesNightScrollerWhenMidnightUsageDominates() {
        val useCase = ClassifyPersonalityUseCase()

        val result = useCase(
            midnightUsageRatio = 0.45,
            socialUsageRatio = 0.30,
            productivityUsageRatio = 0.10,
            weekendSpikeRatio = 0.15,
        )

        assertEquals(PersonalityType.NIGHT_SCROLLER, result.type)
        assertEquals(0.45, result.confidence, 0.0)
    }

    @Test
    fun classifiesSocialMediaAddictWhenSocialUsageIsHigh() {
        val useCase = ClassifyPersonalityUseCase()

        val result = useCase(
            midnightUsageRatio = 0.20,
            socialUsageRatio = 0.50,
            productivityUsageRatio = 0.20,
            weekendSpikeRatio = 0.20,
        )

        assertEquals(PersonalityType.SOCIAL_MEDIA_ADDICT, result.type)
        assertEquals(0.50, result.confidence, 0.0)
    }

    @Test
    fun classifiesProductivitySeekerWhenProductivityUsageIsHigh() {
        val useCase = ClassifyPersonalityUseCase()

        val result = useCase(
            midnightUsageRatio = 0.15,
            socialUsageRatio = 0.20,
            productivityUsageRatio = 0.46,
            weekendSpikeRatio = 0.10,
        )

        assertEquals(PersonalityType.PRODUCTIVITY_SEEKER, result.type)
        assertEquals(0.46, result.confidence, 0.0)
    }

    @Test
    fun classifiesWeekendBingerWhenWeekendSpikeIsHigh() {
        val useCase = ClassifyPersonalityUseCase()

        val result = useCase(
            midnightUsageRatio = 0.15,
            socialUsageRatio = 0.20,
            productivityUsageRatio = 0.20,
            weekendSpikeRatio = 0.35,
        )

        assertEquals(PersonalityType.WEEKEND_BINGER, result.type)
        assertEquals(0.35, result.confidence, 0.0)
    }

    @Test
    fun usesNightScrollerPrecedenceWhenMultipleThresholdsAreExceeded() {
        val useCase = ClassifyPersonalityUseCase()

        val result = useCase(
            midnightUsageRatio = 0.42,
            socialUsageRatio = 0.60,
            productivityUsageRatio = 0.70,
            weekendSpikeRatio = 0.80,
        )

        assertEquals(PersonalityType.NIGHT_SCROLLER, result.type)
        assertEquals(0.42, result.confidence, 0.0)
    }

    @Test
    fun treatsThresholdValuesAsInclusive() {
        val useCase = ClassifyPersonalityUseCase()

        val midnightResult = useCase(
            midnightUsageRatio = 0.40,
            socialUsageRatio = 0.10,
            productivityUsageRatio = 0.10,
            weekendSpikeRatio = 0.10,
        )
        assertEquals(PersonalityType.NIGHT_SCROLLER, midnightResult.type)

        val socialResult = useCase(
            midnightUsageRatio = 0.39,
            socialUsageRatio = 0.45,
            productivityUsageRatio = 0.10,
            weekendSpikeRatio = 0.10,
        )
        assertEquals(PersonalityType.SOCIAL_MEDIA_ADDICT, socialResult.type)

        val productivityResult = useCase(
            midnightUsageRatio = 0.39,
            socialUsageRatio = 0.44,
            productivityUsageRatio = 0.45,
            weekendSpikeRatio = 0.10,
        )
        assertEquals(PersonalityType.PRODUCTIVITY_SEEKER, productivityResult.type)

        val weekendResult = useCase(
            midnightUsageRatio = 0.39,
            socialUsageRatio = 0.44,
            productivityUsageRatio = 0.44,
            weekendSpikeRatio = 0.30,
        )
        assertEquals(PersonalityType.WEEKEND_BINGER, weekendResult.type)
    }

    @Test
    fun classifiesBalancedUserWhenNoPatternExceedsThreshold() {
        val useCase = ClassifyPersonalityUseCase()

        val result = useCase(
            midnightUsageRatio = 0.20,
            socialUsageRatio = 0.30,
            productivityUsageRatio = 0.25,
            weekendSpikeRatio = 0.20,
        )

        assertEquals(PersonalityType.BALANCED_USER, result.type)
        assertEquals(0.60, result.confidence, 0.0)
    }
}

