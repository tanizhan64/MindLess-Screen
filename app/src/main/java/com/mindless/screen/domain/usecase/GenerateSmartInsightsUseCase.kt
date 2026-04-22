package com.mindless.screen.domain.usecase

import com.mindless.screen.domain.model.InsightCard
import com.mindless.screen.domain.model.InsightSeverity

class GenerateSmartInsightsUseCase {

    operator fun invoke(
        totalMinutes: Int,
        unlockCount: Int,
        socialRatio: Double,
        lateNightMinutes: Int
    ): List<InsightCard> {
        val cards = mutableListOf<InsightCard>()

        if (socialRatio >= 0.50) {
            cards += InsightCard(
                severity = InsightSeverity.WARNING,
                message = "Social app usage is high today. Try balancing with offline time."
            )
        }

        if (unlockCount >= 120) {
            cards += InsightCard(
                severity = InsightSeverity.WARNING,
                message = "You unlocked your phone $unlockCount times today. Consider reducing quick checks."
            )
        }

        if (lateNightMinutes >= 60) {
            cards += InsightCard(
                severity = InsightSeverity.WARNING,
                message = "Late-night screen time is elevated. Protect sleep by winding down earlier."
            )
        }

        if (cards.isEmpty()) {
            cards += InsightCard(
                severity = InsightSeverity.INFO,
                message = "Your usage looks balanced today at $totalMinutes minutes. Keep this healthy rhythm going."
            )
        }

        return cards
    }
}
