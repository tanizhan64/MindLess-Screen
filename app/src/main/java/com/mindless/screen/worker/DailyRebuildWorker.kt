package com.mindless.screen.worker

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

class DailyRebuildWorker(
    appContext: Context,
    params: WorkerParameters
) : Worker(appContext, params) {
    override fun doWork(): Result {
        return Result.success()
    }
}
