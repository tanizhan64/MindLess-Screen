package com.mindless.screen.worker

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.mindless.screen.data.notification.UnlockAlertNotifier
import com.mindless.screen.domain.usecase.UnlockAnalyticsUseCase

class SixHourAggregateWorker(
    appContext: Context,
    params: WorkerParameters
) : Worker(appContext, params) {

    private val unlockAnalyticsUseCase = UnlockAnalyticsUseCase(staticFallbackThreshold = 120)
    private val unlockAlertNotifier = UnlockAlertNotifier()

    override fun doWork(): Result {
        val todayUnlockCount = inputData.getInt(KEY_TODAY_UNLOCK_COUNT, 0)
        val recentAverageUnlocks = inputData.getDouble(KEY_RECENT_AVERAGE_UNLOCKS, 0.0)

        val alertEvaluation = unlockAnalyticsUseCase(
            todayUnlockCount = todayUnlockCount,
            recentAverageUnlocks = recentAverageUnlocks
        )

        if (alertEvaluation.shouldNotify) {
            unlockAlertNotifier.notify(alertEvaluation.message.orEmpty())
        }

        return Result.success()
    }

    companion object {
        const val KEY_TODAY_UNLOCK_COUNT = "today_unlock_count"
        const val KEY_RECENT_AVERAGE_UNLOCKS = "recent_average_unlocks"
    }
}
