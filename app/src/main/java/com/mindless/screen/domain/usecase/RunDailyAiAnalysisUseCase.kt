package com.mindless.screen.domain.usecase

import com.mindless.screen.domain.model.AddictionScoreInput
import com.mindless.screen.domain.model.AddictionScoreResult
import com.mindless.screen.domain.repository.AiInsightsRepository

class RunDailyAiAnalysisUseCase(
    private val calculateAddictionScoreUseCase: CalculateAddictionScoreUseCase,
    private val predictTomorrowUsageUseCase: PredictTomorrowUsageUseCase,
    private val classifyPersonalityUseCase: ClassifyPersonalityUseCase,
    private val generateSmartInsightsUseCase: GenerateSmartInsightsUseCase,
    private val computeGamificationUseCase: ComputeGamificationUseCase,
    private val repository: AiInsightsRepository,
) {
    suspend operator fun invoke(dayEpochMillis: Long) {
        val score = calculateAddictionScoreUseCase(
            AddictionScoreInput(
                screen = 70,
                social = 55,
                unlock = 65,
                night = 45,
                gaming = 35,
                session = 50,
            ),
        )

        val prediction = predictTomorrowUsageUseCase(
            last7DaysUsageMinutes = listOf(280, 300, 290, 310, 330, 360, 340),
            weekendFactorMinutes = 35.0,
            midnightUsageMinutes = 70,
            midnightUsageThresholdMinutes = 60,
            riskBand = mapRiskBand(score.riskBand),
        )

        val personality = classifyPersonalityUseCase(
            midnightUsageRatio = 0.32,
            socialUsageRatio = 0.48,
            productivityUsageRatio = 0.12,
            weekendSpikeRatio = 0.20,
        )

        val insights = generateSmartInsightsUseCase(
            totalMinutes = prediction.predictedMinutes,
            unlockCount = 128,
            socialRatio = 0.52,
            lateNightMinutes = 70,
        )

        val gamification = computeGamificationUseCase(
            previousStreak = 6,
            completedFocusToday = true,
        )

        repository.saveInsights(dayEpochMillis, insights)
        repository.savePersonality(dayEpochMillis, personality)
        repository.saveGamification(dayEpochMillis, gamification)
    }

    private fun mapRiskBand(band: AddictionScoreResult.RiskBand): com.mindless.screen.domain.model.RiskBand {
        return when (band) {
            AddictionScoreResult.RiskBand.HEALTHY -> com.mindless.screen.domain.model.RiskBand.LOW
            AddictionScoreResult.RiskBand.MODERATE_USAGE -> com.mindless.screen.domain.model.RiskBand.MODERATE
            AddictionScoreResult.RiskBand.RISK_ZONE,
            AddictionScoreResult.RiskBand.HIGH_ADDICTION,
            -> com.mindless.screen.domain.model.RiskBand.HIGH
        }
    }
}
