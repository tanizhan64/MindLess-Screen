package com.mindless.screen.domain.usecase

import com.mindless.screen.domain.model.GamificationResult

class ComputeGamificationUseCase {

    operator fun invoke(
        previousStreak: Int,
        completedFocusToday: Boolean
    ): GamificationResult {
        val streak = if (completedFocusToday) previousStreak + 1 else 0

        val badges = buildList {
            if (streak >= 7) add("7-day Silver")
            if (streak >= 30) add("30-day Gold")
            if (streak >= 90) add("90-day Master Focus")
        }

        val focusLevel = (streak / 7) + 1

        return GamificationResult(
            streak = streak,
            badges = badges,
            focusLevel = focusLevel
        )
    }
}
