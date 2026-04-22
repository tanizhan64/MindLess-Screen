package com.mindless.screen

import android.app.Application
import com.mindless.screen.worker.WorkerScheduler
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MindLessApp : Application() {
    override fun onCreate() {
        super.onCreate()
        WorkerScheduler(this).scheduleAll()
    }
}
