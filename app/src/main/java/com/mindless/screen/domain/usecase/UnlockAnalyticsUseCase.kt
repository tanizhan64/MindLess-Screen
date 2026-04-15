package com.mindless.screen.domain.usecase

import kotlin.math.max
import kotlin.math.roundToInt

class UnlockAnalyticsUseCase(
    private val staticFallbackThreshold: Int = 120
) {
    operator fun invoke(
        todayUnlockCount: Int,
        recentAverageUnlocks: Double
    ): UnlockAlertEvaluation {
        val dynamicThreshold = max(staticFallbackThreshold, (recentAverageUnlocks * 1.2).roundToInt())
        val shouldNotify = todayUnlockCount >= dynamicThreshold
        val message = if (shouldNotify) {
            "You unlocked your phone $todayUnlockCount times today."
        } else {
            null
        }

        return UnlockAlertEvaluation(
            shouldNotify = shouldNotify,
            message = message
        )
    }
}

data class UnlockAlertEvaluation(
    val shouldNotify: Boolean,
    val message: String?
)
