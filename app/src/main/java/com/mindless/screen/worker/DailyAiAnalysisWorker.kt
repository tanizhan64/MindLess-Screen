package com.mindless.screen.worker

import android.content.Context
import androidx.room.Room
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.mindless.screen.data.insights.AiInsightsRepositoryImpl
import com.mindless.screen.data.local.AppDatabase
import com.mindless.screen.domain.usecase.CalculateAddictionScoreUseCase
import com.mindless.screen.domain.usecase.ClassifyPersonalityUseCase
import com.mindless.screen.domain.usecase.ComputeGamificationUseCase
import com.mindless.screen.domain.usecase.GenerateSmartInsightsUseCase
import com.mindless.screen.domain.usecase.PredictTomorrowUsageUseCase
import com.mindless.screen.domain.usecase.RunDailyAiAnalysisUseCase

class DailyAiAnalysisWorker(
    appContext: Context,
    params: WorkerParameters,
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        return runCatching {
            val database = Room.databaseBuilder(
                applicationContext,
                AppDatabase::class.java,
                DATABASE_NAME,
            )
                .addMigrations(AppDatabase.MIGRATION_1_2)
                .build()

            try {
                val repository = AiInsightsRepositoryImpl(
                    insightDao = database.insightDao(),
                    personalityDao = database.personalityDao(),
                    gamificationDao = database.gamificationDao(),
                )

                val useCase = RunDailyAiAnalysisUseCase(
                    calculateAddictionScoreUseCase = CalculateAddictionScoreUseCase(),
                    predictTomorrowUsageUseCase = PredictTomorrowUsageUseCase(),
                    classifyPersonalityUseCase = ClassifyPersonalityUseCase(),
                    generateSmartInsightsUseCase = GenerateSmartInsightsUseCase(),
                    computeGamificationUseCase = ComputeGamificationUseCase(),
                    repository = repository,
                )

                useCase(dayEpochMillis = System.currentTimeMillis())
            } finally {
                database.close()
            }

            Result.success()
        }.getOrElse {
            Result.retry()
        }
    }

    companion object {
        private const val DATABASE_NAME = "mindless-screen.db"
    }
}
