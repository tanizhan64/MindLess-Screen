package com.mindless.screen.data.insights

import com.mindless.screen.domain.model.GamificationResult
import com.mindless.screen.domain.model.InsightCard
import com.mindless.screen.domain.model.PersonalityClassification
import com.mindless.screen.domain.model.PersonalityType
import com.mindless.screen.domain.repository.AiInsightsRepository

class InMemoryAiInsightsRepository : AiInsightsRepository {
    override suspend fun saveDailyAnalysis(
        dayEpochMillis: Long,
        generatedAtEpochMillis: Long,
        cards: List<InsightCard>,
        classification: PersonalityClassification,
        gamification: GamificationResult
    ) = Unit

    override fun currentStreakDays(): Int = 0

    override fun latestInsightsMessages(): List<String> {
        return listOf(
            "Tomorrow forecast: 180 min. Keep your routine stable.",
            "Social app usage is high today. Try balancing with offline time."
        )
    }

    override fun latestPersonalitySummary(): String {
        return "${PersonalityType.BALANCED_USER.name} (0.60)"
    }

    override fun latestGamificationBadges(): List<String> {
        return listOf("7-day Silver")
    }
}
