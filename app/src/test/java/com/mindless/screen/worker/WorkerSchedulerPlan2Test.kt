package com.mindless.screen.worker

import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ListenableWorker
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.concurrent.TimeUnit
import kotlin.reflect.KClass

class WorkerSchedulerPlan2Test {

    @Test
    fun hasExactDailyAiAnalysisWorkNameConstant() {
        assertEquals("daily_ai_analysis", WorkerScheduler.DAILY_AI_ANALYSIS_WORK_NAME)
    }

    @Test
    fun schedulesDailyAiAnalysisAsUniquePeriodicWorkWithExpectedContract() {
        val fakeEnqueuer = FakePeriodicWorkEnqueuer()
        val scheduler = WorkerScheduler(
            context = android.content.ContextWrapper(null),
            periodicWorkEnqueuer = fakeEnqueuer
        )

        val nowMillis = 1_713_139_200_000L
        scheduler.scheduleAll(nowMillis = nowMillis)

        val dailyAiCall = fakeEnqueuer.calls.firstOrNull {
            it.uniqueWorkName == WorkerScheduler.DAILY_AI_ANALYSIS_WORK_NAME
        }
        requireNotNull(dailyAiCall)

        assertEquals(ExistingPeriodicWorkPolicy.UPDATE, dailyAiCall.policy)
        assertEquals(24L, dailyAiCall.repeatIntervalHours)

        val expectedDelay = WorkerScheduler.calculateInitialDelayToNextMidnightMillis(nowMillis)
        assertEquals(expectedDelay, dailyAiCall.initialDelayMillis)
        assertTrue((dailyAiCall.initialDelayMillis ?: 0L) > 0)
    }

    private class FakePeriodicWorkEnqueuer : WorkerScheduler.PeriodicWorkEnqueuer {
        val calls = mutableListOf<EnqueueCall>()

        override fun enqueueUniquePeriodicWork(
            uniqueWorkName: String,
            existingPeriodicWorkPolicy: ExistingPeriodicWorkPolicy,
            workerClass: KClass<out ListenableWorker>,
            repeatInterval: Long,
            repeatIntervalUnit: TimeUnit,
            initialDelayMillis: Long?
        ) {
            calls += EnqueueCall(
                uniqueWorkName = uniqueWorkName,
                policy = existingPeriodicWorkPolicy,
                workerClass = workerClass,
                repeatIntervalHours = TimeUnit.HOURS.convert(repeatInterval, repeatIntervalUnit),
                initialDelayMillis = initialDelayMillis
            )
        }
    }

    private data class EnqueueCall(
        val uniqueWorkName: String,
        val policy: ExistingPeriodicWorkPolicy,
        val workerClass: KClass<out ListenableWorker>,
        val repeatIntervalHours: Long,
        val initialDelayMillis: Long?
    )
}
