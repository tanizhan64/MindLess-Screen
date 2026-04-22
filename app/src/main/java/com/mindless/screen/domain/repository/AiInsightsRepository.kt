package com.mindless.screen.domain.repository

import com.mindless.screen.domain.model.GamificationResult
import com.mindless.screen.domain.model.InsightCard
import com.mindless.screen.domain.model.PersonalityClassification

interface AiInsightsRepository {
    suspend fun saveDailyAnalysis(
        dayEpochMillis: Long,
        generatedAtEpochMillis: Long,
        cards: List<InsightCard>,
        classification: PersonalityClassification,
        gamification: GamificationResult
    )

    fun currentStreakDays(): Int

    fun latestInsightsMessages(): List<String>

    fun latestPersonalitySummary(): String?

    fun latestGamificationBadges(): List<String>
}
