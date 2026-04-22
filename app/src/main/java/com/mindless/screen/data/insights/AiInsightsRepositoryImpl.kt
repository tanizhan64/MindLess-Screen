package com.mindless.screen.data.insights

import androidx.room.RoomDatabase
import androidx.room.withTransaction
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
    private val database: RoomDatabase,
    private val insightDao: InsightDao,
    private val personalityDao: PersonalityDao,
    private val gamificationDao: GamificationDao
) : AiInsightsRepository {

    override suspend fun saveDailyAnalysis(
        dayEpochMillis: Long,
        generatedAtEpochMillis: Long,
        cards: List<InsightCard>,
        classification: PersonalityClassification,
        gamification: GamificationResult
    ) {
        database.withTransaction {
            insightDao.deleteByDay(dayEpochMillis)
            insightDao.insertAll(
                cards.map { card ->
                    InsightRecordEntity(
                        dayEpochMillis = dayEpochMillis,
                        generatedAtEpochMillis = generatedAtEpochMillis,
                        summary = card.message,
                        recommendationsJson = "${card.severity.name}|${card.message}"
                    )
                }
            )

            personalityDao.insertSnapshot(
                PersonalitySnapshotEntity(
                    dayEpochMillis = dayEpochMillis,
                    archetype = classification.type.name,
                    traitsJson = "confidence=${classification.confidence}",
                    confidence = classification.confidence,
                    createdAtEpochMillis = generatedAtEpochMillis
                )
            )

            gamificationDao.upsertState(
                GamificationStateEntity(
                    level = gamification.focusLevel,
                    points = gamification.streak * 10,
                    streakDays = gamification.streak,
                    badgesCsv = gamification.badges.joinToString(separator = ","),
                    updatedAtEpochMillis = generatedAtEpochMillis
                )
            )
        }
    }

    override fun currentStreakDays(): Int {
        return gamificationDao.stateByKey()?.streakDays ?: 0
    }

    override fun latestInsightsMessages(): List<String> {
        val latestDay = insightDao.latestDayEpochMillis() ?: return emptyList()
        return insightDao
            .insightsByDay(latestDay)
            .map { decodeMessage(it) }
    }

    override fun latestPersonalitySummary(): String? {
        val latest = personalityDao.latestSnapshot() ?: return null
        return latest.archetype
    }

    override fun latestGamificationBadges(): List<String> {
        val csv = gamificationDao.stateByKey()?.badgesCsv ?: return emptyList()
        if (csv.isBlank()) return emptyList()
        return csv.split(',').map { it.trim() }.filter { it.isNotEmpty() }
    }

    private fun decodeMessage(record: InsightRecordEntity): String {
        return record.recommendationsJson.substringAfter('|', record.summary)
    }
}
