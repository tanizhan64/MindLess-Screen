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
    private val gamificationDao: GamificationDao
) : AiInsightsRepository {

    override suspend fun replaceInsights(
        dayEpochMillis: Long,
        cards: List<InsightCard>,
        generatedAtEpochMillis: Long
    ) {
        val dayStart = dayEpochMillis
        val dayEnd = dayEpochMillis + DAY_MILLIS - 1
        insightDao.deleteByDay(dayStart, dayEnd)
        val entities = cards.map { card ->
            InsightRecordEntity(
                generatedAtEpochMillis = generatedAtEpochMillis,
                summary = card.message,
                recommendationsJson = "${card.severity.name}|${card.message}"
            )
        }
        insightDao.insertAll(entities)
    }

    override suspend fun savePersonality(
        dayEpochMillis: Long,
        classification: PersonalityClassification,
        generatedAtEpochMillis: Long
    ) {
        personalityDao.insertSnapshot(
            PersonalitySnapshotEntity(
                dayEpochMillis = dayEpochMillis,
                archetype = classification.type.name,
                traitsJson = "confidence=${classification.confidence}",
                confidence = classification.confidence,
                createdAtEpochMillis = generatedAtEpochMillis
            )
        )
    }

    override suspend fun saveGamification(
        dayEpochMillis: Long,
        result: GamificationResult,
        generatedAtEpochMillis: Long
    ) {
        gamificationDao.upsertState(
            GamificationStateEntity(
                level = result.focusLevel,
                points = result.streak * 10,
                streakDays = result.streak,
                badgesCsv = result.badges.joinToString(separator = ","),
                updatedAtEpochMillis = generatedAtEpochMillis
            )
        )
    }

    override fun latestInsightsMessages(): List<String> {
        val records = insightDao.latestInsights()
        if (records.isEmpty()) return emptyList()

        val latestDayStart = startOfDay(records.first().generatedAtEpochMillis)
        val latestDayEnd = latestDayStart + DAY_MILLIS - 1

        return records
            .asSequence()
            .filter { it.generatedAtEpochMillis in latestDayStart..latestDayEnd }
            .map { decodeMessage(it) }
            .toList()
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

    private fun startOfDay(epochMillis: Long): Long {
        return epochMillis - (epochMillis % DAY_MILLIS)
    }

    private companion object {
        const val DAY_MILLIS: Long = 86_400_000L
    }
}
