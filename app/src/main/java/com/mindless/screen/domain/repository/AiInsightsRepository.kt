package com.mindless.screen.domain.repository

import com.mindless.screen.domain.model.GamificationResult
import com.mindless.screen.domain.model.InsightCard
import com.mindless.screen.domain.model.PersonalityClassification

interface AiInsightsRepository {
    suspend fun replaceInsights(
        dayEpochMillis: Long,
        cards: List<InsightCard>,
        generatedAtEpochMillis: Long
    )

    suspend fun savePersonality(
        dayEpochMillis: Long,
        classification: PersonalityClassification,
        generatedAtEpochMillis: Long
    )

    suspend fun saveGamification(
        dayEpochMillis: Long,
        result: GamificationResult,
        generatedAtEpochMillis: Long
    )

    fun latestInsightsMessages(): List<String>

    fun latestPersonalitySummary(): String?

    fun latestGamificationBadges(): List<String>
}
