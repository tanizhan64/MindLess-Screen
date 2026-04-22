package com.mindless.screen.domain.usecase

import com.mindless.screen.domain.model.GamificationResult
import com.mindless.screen.domain.model.InsightCard
import com.mindless.screen.domain.model.PersonalityClassification
import com.mindless.screen.domain.repository.AiInsightsRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class RunDailyAiAnalysisUseCaseTest {

    @Test
    fun persistsInsightsPersonalityAndGamification_forAnalysisDay() {
        val repository = FakeAiInsightsRepository()
        val useCase = RunDailyAiAnalysisUseCase(
            aiInsightsRepository = repository,
            calculateAddictionScoreUseCase = CalculateAddictionScoreUseCase(),
            predictTomorrowUsageUseCase = PredictTomorrowUsageUseCase(),
            classifyPersonalityUseCase = ClassifyPersonalityUseCase(),
            generateSmartInsightsUseCase = GenerateSmartInsightsUseCase(),
            computeGamificationUseCase = ComputeGamificationUseCase()
        )

        runBlocking {
            useCase(
                dayEpochMillis = 1_713_744_000_000L,
                last7DaysScreenMinutes = listOf(120, 135, 140, 150, 160, 145, 170),
                unlockCount = 130,
                socialMinutes = 100,
                gamingMinutes = 60,
                lateNightMinutes = 80,
                averageSessionMinutes = 12,
                completedFocusToday = true
            )
        }

        assertTrue(repository.latestInsightsMessages().isNotEmpty())
        assertNotNull(repository.latestPersonalitySummary())
        assertTrue(repository.gamificationWasSaved)
    }

    private class FakeAiInsightsRepository : AiInsightsRepository {
        private var insights: List<InsightCard> = emptyList()
        private var personality: PersonalityClassification? = null
        private var gamification: GamificationResult? = null

        var gamificationWasSaved: Boolean = false
            private set

        override suspend fun replaceInsights(
            dayEpochMillis: Long,
            cards: List<InsightCard>,
            generatedAtEpochMillis: Long
        ) {
            insights = cards
        }

        override suspend fun savePersonality(
            dayEpochMillis: Long,
            classification: PersonalityClassification,
            generatedAtEpochMillis: Long
        ) {
            personality = classification
        }

        override suspend fun saveGamification(
            dayEpochMillis: Long,
            result: GamificationResult,
            generatedAtEpochMillis: Long
        ) {
            gamification = result
            gamificationWasSaved = true
        }

        override fun latestInsightsMessages(): List<String> = insights.map { it.message }

        override fun latestPersonalitySummary(): String? = personality?.let {
            "${it.type.name} (${it.confidence})"
        }

        override fun latestGamificationBadges(): List<String> = gamification?.badges ?: emptyList()
    }
}
