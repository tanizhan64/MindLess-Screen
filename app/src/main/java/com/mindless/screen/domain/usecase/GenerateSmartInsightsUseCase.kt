package com.mindless.screen.domain.usecase

import com.mindless.screen.domain.model.InsightCard

class GenerateSmartInsightsUseCase {

    operator fun invoke(
        totalMinutes: Int,
        unlockCount: Int,
        socialRatio: Double,
        lateNightMinutes: Int,
    ): List<InsightCard> {
        require(totalMinutes >= 0) { "totalMinutes must be non-negative" }
        require(unlockCount >= 0) { "unlockCount must be non-negative" }
        require(lateNightMinutes >= 0) { "lateNightMinutes must be non-negative" }
        require(socialRatio.isFinite() && socialRatio in SOCIAL_RATIO_MIN..SOCIAL_RATIO_MAX) {
            "socialRatio must be within [$SOCIAL_RATIO_MIN, $SOCIAL_RATIO_MAX] and finite"
        }

        val cards = mutableListOf<InsightCard>()

        if (socialRatio >= SOCIAL_WARNING_THRESHOLD) {
            cards += InsightCard(
                type = "social",
                severity = "warning",
                message = "High social media share detected",
                recommendation = "Set a social app cap for tomorrow",
            )
        }

        if (unlockCount >= UNLOCK_WARNING_THRESHOLD) {
            cards += InsightCard(
                type = "unlock",
                severity = "warning",
                message = "You unlocked your phone $unlockCount times today.",
                recommendation = "Try batching checks into 30-minute windows",
            )
        }

        if (lateNightMinutes >= LATE_NIGHT_WARNING_THRESHOLD) {
            cards += InsightCard(
                type = "night",
                severity = "warning",
                message = "Late-night usage is increasing next-day risk",
                recommendation = "Start a wind-down focus session before midnight",
            )
        }

        if (cards.isEmpty()) {
            cards += InsightCard(
                type = "balance",
                severity = "info",
                message = "Your usage pattern is stable",
                recommendation = "Keep your current routine",
            )
        }

        return cards
    }

    companion object {
        private const val SOCIAL_RATIO_MIN = 0.0
        private const val SOCIAL_RATIO_MAX = 1.0
        private const val SOCIAL_WARNING_THRESHOLD = 0.50
        private const val UNLOCK_WARNING_THRESHOLD = 120
        private const val LATE_NIGHT_WARNING_THRESHOLD = 60
    }
}
