package com.mindless.screen.domain.usecase

import com.mindless.screen.domain.model.PersonalityClassification
import com.mindless.screen.domain.model.PersonalityType

class ClassifyPersonalityUseCase {

    operator fun invoke(
        midnightUsageRatio: Double,
        socialUsageRatio: Double,
        productivityUsageRatio: Double,
        weekendUsageRatio: Double
    ): PersonalityClassification {
        return when {
            midnightUsageRatio >= 0.40 -> PersonalityClassification(
                type = PersonalityType.NIGHT_SCROLLER,
                confidence = midnightUsageRatio
            )
            socialUsageRatio >= 0.45 -> PersonalityClassification(
                type = PersonalityType.SOCIAL_MEDIA_ADDICT,
                confidence = socialUsageRatio
            )
            productivityUsageRatio >= 0.45 -> PersonalityClassification(
                type = PersonalityType.PRODUCTIVITY_SEEKER,
                confidence = productivityUsageRatio
            )
            weekendUsageRatio >= 0.30 -> PersonalityClassification(
                type = PersonalityType.WEEKEND_BINGER,
                confidence = weekendUsageRatio
            )
            else -> PersonalityClassification(
                type = PersonalityType.BALANCED_USER,
                confidence = 0.60
            )
        }
    }
}
