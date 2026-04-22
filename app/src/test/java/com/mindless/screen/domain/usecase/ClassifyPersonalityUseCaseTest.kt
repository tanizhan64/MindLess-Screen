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
    fun classifiesSOCIAL_MEDIA_ADDICT_whenSocialUsageRatioAtThreshold_andEarlierRulesNotTriggered() {
        val result = useCase(
            midnightUsageRatio = 0.39,
            socialUsageRatio = 0.45,
            productivityUsageRatio = 0.10,
            weekendUsageRatio = 0.10
        )

        assertEquals(PersonalityType.SOCIAL_MEDIA_ADDICT, result.type)
        assertEquals(0.45, result.confidence, 0.0)
    }

    @Test
    fun doesNotClassifySOCIAL_MEDIA_ADDICT_whenSocialUsageRatioJustBelowThreshold_andEarlierRulesNotTriggered() {
        val result = useCase(
            midnightUsageRatio = 0.39,
            socialUsageRatio = 0.44,
            productivityUsageRatio = 0.10,
            weekendUsageRatio = 0.10
        )

        assertEquals(PersonalityType.BALANCED_USER, result.type)
        assertEquals(0.60, result.confidence, 0.0)
    }

    @Test
    fun classifiesPRODUCTIVITY_SEEKER_whenProductivityUsageRatioAtThreshold_andEarlierRulesNotTriggered() {
        val result = useCase(
            midnightUsageRatio = 0.39,
            socialUsageRatio = 0.44,
            productivityUsageRatio = 0.45,
            weekendUsageRatio = 0.10
        )

        assertEquals(PersonalityType.PRODUCTIVITY_SEEKER, result.type)
        assertEquals(0.45, result.confidence, 0.0)
    }

    @Test
    fun doesNotClassifyPRODUCTIVITY_SEEKER_whenProductivityUsageRatioJustBelowThreshold_andEarlierRulesNotTriggered() {
        val result = useCase(
            midnightUsageRatio = 0.39,
            socialUsageRatio = 0.44,
            productivityUsageRatio = 0.44,
            weekendUsageRatio = 0.10
        )

        assertEquals(PersonalityType.BALANCED_USER, result.type)
        assertEquals(0.60, result.confidence, 0.0)
    }

    @Test
    fun classifiesWEEKEND_BINGER_whenWeekendUsageRatioAtThreshold_andEarlierRulesNotTriggered() {
        val result = useCase(
            midnightUsageRatio = 0.39,
            socialUsageRatio = 0.44,
            productivityUsageRatio = 0.44,
            weekendUsageRatio = 0.30
        )

        assertEquals(PersonalityType.WEEKEND_BINGER, result.type)
        assertEquals(0.30, result.confidence, 0.0)
    }

    @Test
    fun doesNotClassifyWEEKEND_BINGER_whenWeekendUsageRatioJustBelowThreshold_andEarlierRulesNotTriggered() {
        val result = useCase(
            midnightUsageRatio = 0.39,
            socialUsageRatio = 0.44,
            productivityUsageRatio = 0.44,
            weekendUsageRatio = 0.29
        )

        assertEquals(PersonalityType.BALANCED_USER, result.type)
        assertEquals(0.60, result.confidence, 0.0)
    }

    @Test
    fun classifiesNIGHT_SCROLLER_whenMultipleThresholdsMet_becauseMidnightHasHighestPrecedence() {
        val result = useCase(
            midnightUsageRatio = 0.40,
            socialUsageRatio = 0.45,
            productivityUsageRatio = 0.45,
            weekendUsageRatio = 0.30
        )

        assertEquals(PersonalityType.NIGHT_SCROLLER, result.type)
        assertEquals(0.40, result.confidence, 0.0)
    }

    @Test
    fun classifiesSOCIAL_MEDIA_ADDICT_whenSocialProductivityAndWeekendThresholdsMet_andMidnightNotMet() {
        val result = useCase(
            midnightUsageRatio = 0.39,
            socialUsageRatio = 0.45,
            productivityUsageRatio = 0.45,
            weekendUsageRatio = 0.30
        )

        assertEquals(PersonalityType.SOCIAL_MEDIA_ADDICT, result.type)
        assertEquals(0.45, result.confidence, 0.0)
    }

    @Test
    fun classifiesPRODUCTIVITY_SEEKER_whenProductivityAndWeekendThresholdsMet_andMidnightSocialNotMet() {
        val result = useCase(
            midnightUsageRatio = 0.39,
            socialUsageRatio = 0.44,
            productivityUsageRatio = 0.45,
            weekendUsageRatio = 0.30
        )

        assertEquals(PersonalityType.PRODUCTIVITY_SEEKER, result.type)
        assertEquals(0.45, result.confidence, 0.0)
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
