package com.mindless.screen.worker

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import androidx.work.testing.TestListenableWorkerBuilder
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import com.mindless.screen.data.local.dao.AggregateDao
import com.mindless.screen.data.local.entity.DailyAggregateEntity
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import androidx.test.core.app.ApplicationProvider

@RunWith(RobolectricTestRunner::class)
class DailyAiAnalysisWorkerTest {

    @Test
    fun doWork_returnsSuccess_whenRunnerCompletes() {
        val context = RuntimeEnvironment.getApplication().applicationContext
        val worker = testWorker(
            context = context,
            runnerFactory = FakeRunnerFactory(
                runner = FakeRunner(
                    block = {}
                )
            )
        )

        val result = runBlocking { worker.doWork() }

        assertEquals(ListenableWorker.Result.success(), result)
    }

    @Test
    fun doWork_returnsRetry_whenRunnerThrows() {
        val context = RuntimeEnvironment.getApplication().applicationContext
        val worker = testWorker(
            context = context,
            runnerFactory = FakeRunnerFactory(
                runner = FakeRunner(
                    block = { throw IllegalStateException("boom") }
                )
            )
        )

        val result = runBlocking { worker.doWork() }

        assertEquals(ListenableWorker.Result.retry(), result)
    }

    @Test
    fun runner_callsUseCaseWithMappedInput_whenAggregateExists() {
        val aggregate = DailyAggregateEntity(
            dayEpochMillis = 1_710_000_000_000L,
            totalScreenTimeMillis = 120L * 60_000L,
            unlockCount = 12,
            addictionScore = 42.0,
            focusTimeMillis = 25L * 60_000L,
            distractionTimeMillis = 10L * 60_000L
        )

        val aggregateDao = FakeAggregateDao(listOf(aggregate))
        val capturedInputs = mutableListOf<DailyAiAnalysisExecutionInput>()
        val runner = DailyAiAnalysisRunner(
            aggregateDao = aggregateDao,
            invokeAnalysis = { input -> capturedInputs += input }
        )

        runBlocking { runner.run() }

        assertEquals(1, capturedInputs.size)
        val input = capturedInputs.first()
        assertEquals(aggregate.dayEpochMillis, input.dayEpochMillis)
        assertEquals(listOf(120), input.last7DaysScreenMinutes)
        assertEquals(12, input.unlockCount)
        assertEquals(0, input.socialMinutes)
        assertEquals(0, input.gamingMinutes)
        assertEquals(0, input.lateNightMinutes)
        assertEquals(10, input.averageSessionMinutes)
        assertTrue(input.completedFocusToday)
    }

    @Test
    fun runner_skipsUseCase_whenNoAggregates() {
        val aggregateDao = FakeAggregateDao(emptyList())
        var invoked = false
        val runner = DailyAiAnalysisRunner(
            aggregateDao = aggregateDao,
            invokeAnalysis = { invoked = true }
        )

        runBlocking { runner.run() }

        assertFalse(invoked)
    }

    private fun testWorker(
        context: Context,
        runnerFactory: DailyAiAnalysisRunnerFactory
    ): DailyAiAnalysisWorker {
        return TestListenableWorkerBuilder<DailyAiAnalysisWorker>(
            context
        ).setWorkerFactory(
            object : WorkerFactory() {
                override fun createWorker(
                    appContext: Context,
                    workerClassName: String,
                    workerParameters: WorkerParameters
                ): ListenableWorker {
                    return DailyAiAnalysisWorker(
                        appContext = appContext,
                        params = workerParameters,
                        runnerFactory = runnerFactory
                    )
                }
            }
        ).build()
    }

    private class FakeRunnerFactory(
        private val runner: DailyAiAnalysisRunner
    ) : DailyAiAnalysisRunnerFactory() {
        override fun create(context: Context): DailyAiAnalysisRunner = runner
    }

    private class FakeRunner(
        private val block: suspend () -> Unit
    ) : DailyAiAnalysisRunner(
        aggregateDao = FakeAggregateDao(emptyList()),
        invokeAnalysis = {}
    ) {
        override suspend fun run() {
            block()
        }
    }

    private class FakeAggregateDao(
        private val items: List<DailyAggregateEntity>
    ) : AggregateDao {
        override fun latestDailyAggregate(): DailyAggregateEntity? = items.firstOrNull()

        override fun latestDailyAggregates(limit: Int): List<DailyAggregateEntity> {
            return items.take(limit)
        }
    }
}
