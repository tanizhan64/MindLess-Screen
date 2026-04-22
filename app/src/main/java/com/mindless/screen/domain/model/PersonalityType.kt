package com.mindless.screen.domain.model

enum class PersonalityType {
    NIGHT_SCROLLER,
    SOCIAL_MEDIA_ADDICT,
    PRODUCTIVITY_SEEKER,
    WEEKEND_BINGER,
    BALANCED_USER
}

data class PersonalityClassification(
    val type: PersonalityType,
    val confidence: Double
)
