package com.mindless.screen.domain.usecase

import com.mindless.screen.domain.model.GamificationResult
import com.mindless.screen.domain.model.InsightCard
import com.mindless.screen.domain.model.PersonalityClassification
import com.mindless.screen.domain.repository.AiInsightsRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class RunDailyAiAnalysisUseCaseTest {

    @Test
    fun storesPredictionInsightsPersonalityAndGamification() {
        val repository = FakeAiInsightsRepository()
        val useCase = RunDailyAiAnalysisUseCase(
            calculateAddictionScoreUseCase = CalculateAddictionScoreUseCase(),
            predictTomorrowUsageUseCase = PredictTomorrowUsageUseCase(),
            classifyPersonalityUseCase = ClassifyPersonalityUseCase(),
            generateSmartInsightsUseCase = GenerateSmartInsightsUseCase(),
            computeGamificationUseCase = ComputeGamificationUseCase(),
            repository = repository,
        )

        val dayEpochMillis = 1713312000000L
        kotlinx.coroutines.runBlocking {
            useCase(dayEpochMillis = dayEpochMillis)
        }

        assertTrue(repository.savedInsightCount > 0)
        assertNotNull(repository.savedPersonality)
        assertNotNull(repository.savedGamification)
        assertEquals(dayEpochMillis, repository.savedInsightsDay)
        assertEquals(dayEpochMillis, repository.savedPersonalityDay)
        assertEquals(dayEpochMillis, repository.savedGamificationDay)
    }

    private class FakeAiInsightsRepository : AiInsightsRepository {
        var savedInsightCount: Int = 0
        var savedPersonality: PersonalityClassification? = null
        var savedGamification: GamificationResult? = null
        var savedInsightsDay: Long? = null
        var savedPersonalityDay: Long? = null
        var savedGamificationDay: Long? = null

        override suspend fun saveInsights(dayEpochMillis: Long, cards: List<InsightCard>) {
            savedInsightCount = cards.size
            savedInsightsDay = dayEpochMillis
        }

        override suspend fun savePersonality(dayEpochMillis: Long, classification: PersonalityClassification) {
            savedPersonality = classification
            savedPersonalityDay = dayEpochMillis
        }

        override suspend fun saveGamification(dayEpochMillis: Long, gamification: GamificationResult) {
            savedGamification = gamification
            savedGamificationDay = dayEpochMillis
        }
    }
}
