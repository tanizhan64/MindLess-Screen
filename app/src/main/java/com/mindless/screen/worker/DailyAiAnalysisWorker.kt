package com.mindless.screen.worker

import android.content.Context
import androidx.room.Room
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.mindless.screen.data.insights.AiInsightsRepositoryImpl
import com.mindless.screen.data.local.AppDatabase
import com.mindless.screen.data.local.dao.AggregateDao
import com.mindless.screen.domain.usecase.CalculateAddictionScoreUseCase
import com.mindless.screen.domain.usecase.ClassifyPersonalityUseCase
import com.mindless.screen.domain.usecase.ComputeGamificationUseCase
import com.mindless.screen.domain.usecase.GenerateSmartInsightsUseCase
import com.mindless.screen.domain.usecase.PredictTomorrowUsageUseCase
import com.mindless.screen.domain.usecase.RunDailyAiAnalysisUseCase
import kotlinx.coroutines.CancellationException

class DailyAiAnalysisWorker(
    appContext: Context,
    params: WorkerParameters,
    private val runnerFactory: DailyAiAnalysisRunnerFactory = DailyAiAnalysisRunnerFactory()
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        return try {
            runnerFactory.create(applicationContext).run()
            Result.success()
        } catch (cancellationException: CancellationException) {
            throw cancellationException
        } catch (_: Exception) {
            Result.retry()
        }
    }
}

open class DailyAiAnalysisRunnerFactory {

    open fun create(context: Context): DailyAiAnalysisRunner {
        val database = database(context)
        val aiInsightsRepository = AiInsightsRepositoryImpl(
            database = database,
            insightDao = database.insightDao(),
            personalityDao = database.personalityDao(),
            gamificationDao = database.gamificationDao()
        )

        val runDailyAiAnalysisUseCase = RunDailyAiAnalysisUseCase(
            aiInsightsRepository = aiInsightsRepository,
            calculateAddictionScoreUseCase = CalculateAddictionScoreUseCase(),
            predictTomorrowUsageUseCase = PredictTomorrowUsageUseCase(),
            classifyPersonalityUseCase = ClassifyPersonalityUseCase(),
            generateSmartInsightsUseCase = GenerateSmartInsightsUseCase(),
            computeGamificationUseCase = ComputeGamificationUseCase()
        )

        return DailyAiAnalysisRunner(
            aggregateDao = database.aggregateDao(),
            invokeAnalysis = { input ->
                runDailyAiAnalysisUseCase(
                    dayEpochMillis = input.dayEpochMillis,
                    last7DaysScreenMinutes = input.last7DaysScreenMinutes,
                    unlockCount = input.unlockCount,
                    socialMinutes = input.socialMinutes,
                    gamingMinutes = input.gamingMinutes,
                    lateNightMinutes = input.lateNightMinutes,
                    averageSessionMinutes = input.averageSessionMinutes,
                    completedFocusToday = input.completedFocusToday
                )
            }
        )
    }

    private fun database(context: Context): AppDatabase {
        return cachedDatabase ?: synchronized(this) {
            cachedDatabase ?: Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                APP_DATABASE_NAME
            ).build().also { cachedDatabase = it }
        }
    }

    companion object {
        private const val APP_DATABASE_NAME = "mindless-screen.db"

        @Volatile
        private var cachedDatabase: AppDatabase? = null
    }
}

data class DailyAiAnalysisExecutionInput(
    val dayEpochMillis: Long,
    val last7DaysScreenMinutes: List<Int>,
    val unlockCount: Int,
    val socialMinutes: Int,
    val gamingMinutes: Int,
    val lateNightMinutes: Int,
    val averageSessionMinutes: Int,
    val completedFocusToday: Boolean
)

open class DailyAiAnalysisRunner(
    private val aggregateDao: AggregateDao,
    private val invokeAnalysis: suspend (DailyAiAnalysisExecutionInput) -> Unit
) {

    open suspend fun run() {
        val latestAggregates = aggregateDao.latestDailyAggregates(HISTORY_DAYS)
        val latest = latestAggregates.firstOrNull() ?: return

        val input = DailyAiAnalysisExecutionInput(
            dayEpochMillis = latest.dayEpochMillis,
            last7DaysScreenMinutes = latestAggregates
                .asReversed()
                .map { millisToMinutes(it.totalScreenTimeMillis) },
            unlockCount = latest.unlockCount,
            socialMinutes = 0,
            gamingMinutes = 0,
            lateNightMinutes = 0,
            averageSessionMinutes = estimateAverageSessionMinutes(
                totalScreenTimeMillis = latest.totalScreenTimeMillis,
                unlockCount = latest.unlockCount
            ),
            completedFocusToday = latest.focusTimeMillis > 0L
        )

        invokeAnalysis(input)
    }

    private fun estimateAverageSessionMinutes(
        totalScreenTimeMillis: Long,
        unlockCount: Int
    ): Int {
        val totalMinutes = millisToMinutes(totalScreenTimeMillis)
        if (unlockCount <= 0) return totalMinutes
        return (totalMinutes / unlockCount).coerceAtLeast(0)
    }

    private fun millisToMinutes(millis: Long): Int {
        return (millis / MILLIS_PER_MINUTE).toInt().coerceAtLeast(0)
    }

    companion object {
        private const val HISTORY_DAYS = 7
        private const val MILLIS_PER_MINUTE = 60_000L
    }
}
