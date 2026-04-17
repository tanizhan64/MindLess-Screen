package com.mindless.screen.worker

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.Calendar
import java.util.concurrent.TimeUnit

class WorkerScheduler(
    private val context: Context
) {
    private fun calculateInitialDelayToNextMidnightMillis(nowMillis: Long = System.currentTimeMillis()): Long {
        val now = Calendar.getInstance().apply {
            timeInMillis = nowMillis
        }
        val nextMidnight = (now.clone() as Calendar).apply {
            add(Calendar.DAY_OF_YEAR, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return nextMidnight.timeInMillis - now.timeInMillis
    }

    fun scheduleAll() {
        val workManager = WorkManager.getInstance(context)

        val lightScanRequest = PeriodicWorkRequestBuilder<LightUsageScanWorker>(15, TimeUnit.MINUTES)
            .build()
        workManager.enqueueUniquePeriodicWork(
            "light_usage_scan_15m",
            ExistingPeriodicWorkPolicy.UPDATE,
            lightScanRequest
        )

        val aggregateRequest = PeriodicWorkRequestBuilder<SixHourAggregateWorker>(6, TimeUnit.HOURS)
            .build()
        workManager.enqueueUniquePeriodicWork(
            "aggregate_6h",
            ExistingPeriodicWorkPolicy.UPDATE,
            aggregateRequest
        )

        val initialDelayToMidnightMillis = calculateInitialDelayToNextMidnightMillis()
        val dailyRebuildRequest = PeriodicWorkRequestBuilder<DailyRebuildWorker>(24, TimeUnit.HOURS)
            .setInitialDelay(initialDelayToMidnightMillis, TimeUnit.MILLISECONDS)
            .build()
        workManager.enqueueUniquePeriodicWork(
            "daily_rebuild_midnight",
            ExistingPeriodicWorkPolicy.UPDATE,
            dailyRebuildRequest
        )

        val cleanupRequest = PeriodicWorkRequestBuilder<CleanupWorker>(24, TimeUnit.HOURS)
            .build()
        workManager.enqueueUniquePeriodicWork(
            "cleanup_daily",
            ExistingPeriodicWorkPolicy.UPDATE,
            cleanupRequest
        )

        val dailyAiAnalysisRequest = PeriodicWorkRequestBuilder<DailyAiAnalysisWorker>(24, TimeUnit.HOURS)
            .setInitialDelay(initialDelayToMidnightMillis, TimeUnit.MILLISECONDS)
            .build()
        workManager.enqueueUniquePeriodicWork(
            "daily_ai_analysis",
            ExistingPeriodicWorkPolicy.UPDATE,
            dailyAiAnalysisRequest
        )
    }
}
