package com.mindless.screen.domain.usecase

import com.mindless.screen.domain.model.PersonalityClassification
import com.mindless.screen.domain.model.PersonalityType

class ClassifyPersonalityUseCase {

    operator fun invoke(
        midnightUsageRatio: Double,
        socialUsageRatio: Double,
        productivityUsageRatio: Double,
        weekendSpikeRatio: Double,
    ): PersonalityClassification {
        require(midnightUsageRatio.isFinite() && midnightUsageRatio in RATIO_MIN..RATIO_MAX) {
            "midnightUsageRatio must be within [$RATIO_MIN, $RATIO_MAX] and finite"
        }
        require(socialUsageRatio.isFinite() && socialUsageRatio in RATIO_MIN..RATIO_MAX) {
            "socialUsageRatio must be within [$RATIO_MIN, $RATIO_MAX] and finite"
        }
        require(productivityUsageRatio.isFinite() && productivityUsageRatio in RATIO_MIN..RATIO_MAX) {
            "productivityUsageRatio must be within [$RATIO_MIN, $RATIO_MAX] and finite"
        }
        require(weekendSpikeRatio.isFinite() && weekendSpikeRatio in RATIO_MIN..RATIO_MAX) {
            "weekendSpikeRatio must be within [$RATIO_MIN, $RATIO_MAX] and finite"
        }

        return when {
            midnightUsageRatio >= NIGHT_SCROLLER_THRESHOLD -> PersonalityClassification(
                type = PersonalityType.NIGHT_SCROLLER,
                confidence = midnightUsageRatio,
            )

            socialUsageRatio >= SOCIAL_MEDIA_THRESHOLD -> PersonalityClassification(
                type = PersonalityType.SOCIAL_MEDIA_ADDICT,
                confidence = socialUsageRatio,
            )

            productivityUsageRatio >= PRODUCTIVITY_THRESHOLD -> PersonalityClassification(
                type = PersonalityType.PRODUCTIVITY_SEEKER,
                confidence = productivityUsageRatio,
            )

            weekendSpikeRatio >= WEEKEND_BINGER_THRESHOLD -> PersonalityClassification(
                type = PersonalityType.WEEKEND_BINGER,
                confidence = weekendSpikeRatio,
            )

            else -> PersonalityClassification(
                type = PersonalityType.BALANCED_USER,
                confidence = BALANCED_DEFAULT_CONFIDENCE,
            )
        }
    }

    companion object {
        private const val RATIO_MIN = 0.0
        private const val RATIO_MAX = 1.0
        private const val NIGHT_SCROLLER_THRESHOLD = 0.40
        private const val SOCIAL_MEDIA_THRESHOLD = 0.45
        private const val PRODUCTIVITY_THRESHOLD = 0.45
        private const val WEEKEND_BINGER_THRESHOLD = 0.30
        private const val BALANCED_DEFAULT_CONFIDENCE = 0.60
    }
}
