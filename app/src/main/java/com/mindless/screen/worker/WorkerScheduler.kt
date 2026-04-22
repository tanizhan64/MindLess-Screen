package com.mindless.screen.worker

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ListenableWorker
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import java.util.Calendar
import java.util.concurrent.TimeUnit
import kotlin.reflect.KClass

class WorkerScheduler(
    context: Context,
    private val periodicWorkEnqueuer: PeriodicWorkEnqueuer = WorkManagerPeriodicWorkEnqueuer(
        WorkManager.getInstance(context)
    )
) {
    fun scheduleAll(nowMillis: Long = System.currentTimeMillis()) {
        periodicWorkEnqueuer.enqueueUniquePeriodicWork(
            uniqueWorkName = "light_usage_scan_15m",
            existingPeriodicWorkPolicy = ExistingPeriodicWorkPolicy.UPDATE,
            workerClass = LightUsageScanWorker::class,
            repeatInterval = 15,
            repeatIntervalUnit = TimeUnit.MINUTES
        )

        periodicWorkEnqueuer.enqueueUniquePeriodicWork(
            uniqueWorkName = "aggregate_6h",
            existingPeriodicWorkPolicy = ExistingPeriodicWorkPolicy.UPDATE,
            workerClass = SixHourAggregateWorker::class,
            repeatInterval = 6,
            repeatIntervalUnit = TimeUnit.HOURS
        )

        val initialDelayToMidnightMillis = calculateInitialDelayToNextMidnightMillis(nowMillis)
        periodicWorkEnqueuer.enqueueUniquePeriodicWork(
            uniqueWorkName = "daily_rebuild_midnight",
            existingPeriodicWorkPolicy = ExistingPeriodicWorkPolicy.UPDATE,
            workerClass = DailyRebuildWorker::class,
            repeatInterval = 24,
            repeatIntervalUnit = TimeUnit.HOURS,
            initialDelayMillis = initialDelayToMidnightMillis
        )

        periodicWorkEnqueuer.enqueueUniquePeriodicWork(
            uniqueWorkName = "cleanup_daily",
            existingPeriodicWorkPolicy = ExistingPeriodicWorkPolicy.UPDATE,
            workerClass = CleanupWorker::class,
            repeatInterval = 24,
            repeatIntervalUnit = TimeUnit.HOURS
        )

        periodicWorkEnqueuer.enqueueUniquePeriodicWork(
            uniqueWorkName = DAILY_AI_ANALYSIS_WORK_NAME,
            existingPeriodicWorkPolicy = ExistingPeriodicWorkPolicy.UPDATE,
            workerClass = DailyAiAnalysisWorker::class,
            repeatInterval = DAILY_AI_ANALYSIS_REPEAT_INTERVAL_HOURS,
            repeatIntervalUnit = TimeUnit.HOURS,
            initialDelayMillis = initialDelayToMidnightMillis
        )
    }

    interface PeriodicWorkEnqueuer {
        fun enqueueUniquePeriodicWork(
            uniqueWorkName: String,
            existingPeriodicWorkPolicy: ExistingPeriodicWorkPolicy,
            workerClass: KClass<out ListenableWorker>,
            repeatInterval: Long,
            repeatIntervalUnit: TimeUnit,
            initialDelayMillis: Long? = null
        )
    }

    private class WorkManagerPeriodicWorkEnqueuer(
        private val workManager: WorkManager
    ) : PeriodicWorkEnqueuer {
        override fun enqueueUniquePeriodicWork(
            uniqueWorkName: String,
            existingPeriodicWorkPolicy: ExistingPeriodicWorkPolicy,
            workerClass: KClass<out ListenableWorker>,
            repeatInterval: Long,
            repeatIntervalUnit: TimeUnit,
            initialDelayMillis: Long?
        ) {
            val requestBuilder = PeriodicWorkRequest.Builder(
                workerClass.java,
                repeatInterval,
                repeatIntervalUnit
            )

            if (initialDelayMillis != null) {
                requestBuilder.setInitialDelay(initialDelayMillis, TimeUnit.MILLISECONDS)
            }

            workManager.enqueueUniquePeriodicWork(
                uniqueWorkName,
                existingPeriodicWorkPolicy,
                requestBuilder.build()
            )
        }
    }

    companion object {
        const val DAILY_AI_ANALYSIS_WORK_NAME = "daily_ai_analysis"
        const val DAILY_AI_ANALYSIS_REPEAT_INTERVAL_HOURS = 24L

        internal fun calculateInitialDelayToNextMidnightMillis(
            nowMillis: Long = System.currentTimeMillis()
        ): Long {
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
    }
}
