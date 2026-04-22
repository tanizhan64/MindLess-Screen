package com.mindless.screen.domain.usecase

import com.mindless.screen.domain.model.GamificationResult
import com.mindless.screen.domain.model.InsightCard
import com.mindless.screen.domain.model.PersonalityClassification
import com.mindless.screen.domain.repository.AiInsightsRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class RunDailyAiAnalysisUseCaseTest {

    @Test
    fun persistsInsightsPersonalityAndGamification_forAnalysisDay() {
        val repository = FakeAiInsightsRepository(initialStreak = 0)
        val useCase = RunDailyAiAnalysisUseCase(
            aiInsightsRepository = repository,
            calculateAddictionScoreUseCase = CalculateAddictionScoreUseCase(),
            predictTomorrowUsageUseCase = PredictTomorrowUsageUseCase(),
            classifyPersonalityUseCase = ClassifyPersonalityUseCase(),
            generateSmartInsightsUseCase = GenerateSmartInsightsUseCase(),
            computeGamificationUseCase = ComputeGamificationUseCase()
        )

        val analysisDay = 1_713_744_000_000L

        runBlocking {
            useCase(
                dayEpochMillis = analysisDay,
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
        assertEquals(analysisDay, repository.savedDayEpochMillis)
    }

    @Test
    fun includesTomorrowPredictionCardInInsights() {
        val repository = FakeAiInsightsRepository(initialStreak = 0)
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
                last7DaysScreenMinutes = listOf(120, 130, 140, 150, 160, 170, 180),
                unlockCount = 90,
                socialMinutes = 50,
                gamingMinutes = 40,
                lateNightMinutes = 70,
                averageSessionMinutes = 10,
                completedFocusToday = true
            )
        }

        val firstMessage = repository.latestInsightsMessages().first()
        assertTrue(firstMessage.startsWith("Tomorrow forecast:"))
    }

    @Test
    fun computesGamificationUsingPreviousStreakFromRepository() {
        val repository = FakeAiInsightsRepository(initialStreak = 5)
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
                last7DaysScreenMinutes = listOf(100, 100, 100, 100, 100, 100, 100),
                unlockCount = 20,
                socialMinutes = 20,
                gamingMinutes = 10,
                lateNightMinutes = 0,
                averageSessionMinutes = 6,
                completedFocusToday = true
            )
        }

        assertEquals(6, repository.lastSavedGamification?.streak)
    }


    private class FakeAiInsightsRepository(
        private val initialStreak: Int
    ) : AiInsightsRepository {
        private var insights: List<InsightCard> = emptyList()
        private var personality: PersonalityClassification? = null
        private var gamification: GamificationResult? = null

        var gamificationWasSaved: Boolean = false
            private set
        var savedDayEpochMillis: Long? = null
            private set
        var lastSavedGamification: GamificationResult? = null
            private set
        var lastSavedPersonality: PersonalityClassification? = null
            private set

        override suspend fun saveDailyAnalysis(
            dayEpochMillis: Long,
            generatedAtEpochMillis: Long,
            cards: List<InsightCard>,
            classification: PersonalityClassification,
            gamification: GamificationResult
        ) {
            insights = cards
            personality = classification
            this.gamification = gamification
            gamificationWasSaved = true
            savedDayEpochMillis = dayEpochMillis
            lastSavedGamification = gamification
            lastSavedPersonality = classification
        }

        override fun currentStreakDays(): Int = initialStreak

        override fun latestInsightsMessages(): List<String> = insights.map { it.message }

        override fun latestPersonalitySummary(): String? = personality?.let {
            "${it.type.name} (${it.confidence})"
        }

        override fun latestGamificationBadges(): List<String> = gamification?.badges ?: emptyList()
    }
}
