package com.mindless.screen.data.insights

import com.mindless.screen.data.local.dao.GamificationDao
import com.mindless.screen.data.local.dao.InsightDao
import com.mindless.screen.data.local.dao.PersonalityDao
import com.mindless.screen.data.local.entity.GamificationStateEntity
import com.mindless.screen.data.local.entity.InsightRecordEntity
import com.mindless.screen.data.local.entity.PersonalitySnapshotEntity
import com.mindless.screen.domain.model.GamificationResult
import com.mindless.screen.domain.model.InsightCard
import com.mindless.screen.domain.model.PersonalityClassification
import com.mindless.screen.domain.repository.AiInsightsRepository

class AiInsightsRepositoryImpl(
    private val insightDao: InsightDao,
    private val personalityDao: PersonalityDao,
    private val gamificationDao: GamificationDao,
) : AiInsightsRepository {

    override suspend fun saveInsights(dayEpochMillis: Long, cards: List<InsightCard>) {
        cards.forEach { card ->
            insightDao.insertInsight(
                InsightRecordEntity(
                    dateEpochMillis = dayEpochMillis,
                    type = card.type,
                    severity = if (card.severity == "warning") 2 else 1,
                    message = card.message,
                    recommendation = card.recommendation,
                ),
            )
        }
    }

    override suspend fun savePersonality(dayEpochMillis: Long, classification: PersonalityClassification) {
        personalityDao.insertSnapshot(
            PersonalitySnapshotEntity(
                dateEpochMillis = dayEpochMillis,
                personalityType = classification.type.name,
                confidence = classification.confidence.toFloat(),
            ),
        )
    }

    override suspend fun saveGamification(dayEpochMillis: Long, gamification: GamificationResult) {
        gamificationDao.upsertState(
            GamificationStateEntity(
                dateEpochMillis = dayEpochMillis,
                streakDays = gamification.streakDays,
                focusLevel = gamification.focusLevel,
                badgesJson = gamification.badges.joinToString(separator = "|"),
            ),
        )
    }
}
