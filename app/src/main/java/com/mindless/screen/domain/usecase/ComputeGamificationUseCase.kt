package com.mindless.screen.domain.usecase

import com.mindless.screen.domain.model.GamificationResult

class ComputeGamificationUseCase {

    operator fun invoke(previousStreak: Int, completedFocusToday: Boolean): GamificationResult {
        require(previousStreak >= 0) { "previousStreak must be non-negative" }

        val streak = if (completedFocusToday) previousStreak + 1 else 0

        val badges = buildList {
            if (streak >= SILVER_BADGE_STREAK) add(SILVER_BADGE)
            if (streak >= GOLD_BADGE_STREAK) add(GOLD_BADGE)
            if (streak >= MASTER_BADGE_STREAK) add(MASTER_BADGE)
        }

        return GamificationResult(
            streakDays = streak,
            focusLevel = (streak / FOCUS_LEVEL_DIVISOR) + BASE_FOCUS_LEVEL,
            badges = badges,
        )
    }

    companion object {
        private const val SILVER_BADGE_STREAK = 7
        private const val GOLD_BADGE_STREAK = 30
        private const val MASTER_BADGE_STREAK = 90
        private const val FOCUS_LEVEL_DIVISOR = 7
        private const val BASE_FOCUS_LEVEL = 1

        private const val SILVER_BADGE = "7-day Silver"
        private const val GOLD_BADGE = "30-day Gold"
        private const val MASTER_BADGE = "90-day Master Focus"
    }
}
