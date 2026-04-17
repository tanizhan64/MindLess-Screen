package com.mindless.screen.domain.repository

import com.mindless.screen.domain.model.GamificationResult
import com.mindless.screen.domain.model.InsightCard
import com.mindless.screen.domain.model.PersonalityClassification

interface AiInsightsRepository {
    suspend fun saveInsights(dayEpochMillis: Long, cards: List<InsightCard>)
    suspend fun savePersonality(dayEpochMillis: Long, classification: PersonalityClassification)
    suspend fun saveGamification(dayEpochMillis: Long, gamification: GamificationResult)
}
