package com.mindless.screen.worker

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

class WorkerScheduler(
    private val context: Context
) {
    fun scheduleAll() {
        val workManager = WorkManager.getInstance(context)

        val lightScanRequest = PeriodicWorkRequestBuilder<LightUsageScanWorker>(15, TimeUnit.MINUTES)
            .build()
        workManager.enqueueUniquePeriodicWork(
            "light_usage_scan_15m",
            ExistingPeriodicWorkPolicy.KEEP,
            lightScanRequest
        )

        val aggregateRequest = PeriodicWorkRequestBuilder<SixHourAggregateWorker>(6, TimeUnit.HOURS)
            .build()
        workManager.enqueueUniquePeriodicWork(
            "aggregate_6h",
            ExistingPeriodicWorkPolicy.KEEP,
            aggregateRequest
        )

        val dailyRebuildRequest = PeriodicWorkRequestBuilder<DailyRebuildWorker>(24, TimeUnit.HOURS)
            .build()
        workManager.enqueueUniquePeriodicWork(
            "daily_rebuild_midnight",
            ExistingPeriodicWorkPolicy.KEEP,
            dailyRebuildRequest
        )

        val cleanupRequest = PeriodicWorkRequestBuilder<CleanupWorker>(24, TimeUnit.HOURS)
            .build()
        workManager.enqueueUniquePeriodicWork(
            "cleanup_daily",
            ExistingPeriodicWorkPolicy.KEEP,
            cleanupRequest
        )
    }
}
