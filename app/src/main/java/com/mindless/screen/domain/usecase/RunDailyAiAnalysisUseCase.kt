package com.mindless.screen.domain.usecase

import com.mindless.screen.domain.model.AddictionScoreInput
import com.mindless.screen.domain.model.PersonalityClassification
import com.mindless.screen.domain.model.PersonalityType
import com.mindless.screen.domain.repository.AiInsightsRepository

class RunDailyAiAnalysisUseCase(
    private val aiInsightsRepository: AiInsightsRepository,
    private val calculateAddictionScoreUseCase: CalculateAddictionScoreUseCase,
    private val predictTomorrowUsageUseCase: PredictTomorrowUsageUseCase,
    private val classifyPersonalityUseCase: ClassifyPersonalityUseCase,
    private val generateSmartInsightsUseCase: GenerateSmartInsightsUseCase,
    private val computeGamificationUseCase: ComputeGamificationUseCase
) {
    suspend operator fun invoke(
        dayEpochMillis: Long,
        last7DaysScreenMinutes: List<Int>,
        unlockCount: Int,
        socialMinutes: Int,
        gamingMinutes: Int,
        lateNightMinutes: Int,
        averageSessionMinutes: Int,
        completedFocusToday: Boolean
    ) {
        val totalMinutes = last7DaysScreenMinutes.lastOrNull()?.coerceAtLeast(0) ?: 0

        val socialRatio = ratio(socialMinutes, totalMinutes)
        val gamingRatio = ratio(gamingMinutes, totalMinutes)
        val lateNightRatio = ratio(lateNightMinutes, totalMinutes)
        val sessionScore = (averageSessionMinutes * 5).coerceIn(0, 100)

        val addictionResult = calculateAddictionScoreUseCase(
            AddictionScoreInput(
                screenScore = totalMinutes.coerceIn(0, 100),
                socialScore = (socialRatio * 100).toInt().coerceIn(0, 100),
                unlockScore = (unlockCount.coerceAtMost(200) * 100 / 200).coerceIn(0, 100),
                nightScore = (lateNightRatio * 100).toInt().coerceIn(0, 100),
                gamingScore = (gamingRatio * 100).toInt().coerceIn(0, 100),
                sessionScore = sessionScore
            )
        )

        predictTomorrowUsageUseCase(
            last7DaysUsageMinutes = last7DaysScreenMinutes,
            weekendFactorMinutes = socialMinutes + gamingMinutes,
            midnightUsageMinutes = lateNightMinutes,
            midnightThresholdMinutes = 60,
            currentRiskBand = addictionResult.band
        )

        val personality = classifyPersonalityUseCase(
            midnightUsageRatio = lateNightRatio,
            socialUsageRatio = socialRatio,
            productivityUsageRatio = ratio((totalMinutes - socialMinutes - gamingMinutes).coerceAtLeast(0), totalMinutes),
            weekendUsageRatio = 0.0
        )

        val insights = generateSmartInsightsUseCase(
            totalMinutes = totalMinutes,
            unlockCount = unlockCount,
            socialRatio = socialRatio,
            lateNightMinutes = lateNightMinutes
        )

        val gamification = computeGamificationUseCase(
            previousStreak = 0,
            completedFocusToday = completedFocusToday
        )

        val generatedAtEpochMillis = System.currentTimeMillis()
        aiInsightsRepository.replaceInsights(dayEpochMillis, insights, generatedAtEpochMillis)
        aiInsightsRepository.savePersonality(dayEpochMillis, personality.ifUnknownUseBalanced(), generatedAtEpochMillis)
        aiInsightsRepository.saveGamification(dayEpochMillis, gamification, generatedAtEpochMillis)
    }

    private fun ratio(part: Int, total: Int): Double {
        if (total <= 0) return 0.0
        return part.coerceAtLeast(0).toDouble() / total.toDouble()
    }

    private fun PersonalityClassification.ifUnknownUseBalanced(): PersonalityClassification {
        return if (confidence.isNaN()) {
            PersonalityClassification(PersonalityType.BALANCED_USER, 0.60)
        } else {
            this
        }
    }
}
