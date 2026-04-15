package com.mindless.screen.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.mindless.screen.data.local.dao.AggregateDao
import com.mindless.screen.data.local.dao.AnalyticsDao
import com.mindless.screen.data.local.dao.CategoryDao
import com.mindless.screen.data.local.dao.TrackingDao
import com.mindless.screen.data.local.entity.AnalyticsEventEntity
import com.mindless.screen.data.local.entity.AppCategoryMapEntity
import com.mindless.screen.data.local.entity.AppUsageRecordEntity
import com.mindless.screen.data.local.entity.DailyAggregateEntity
import com.mindless.screen.data.local.entity.HourlyAggregateEntity
import com.mindless.screen.data.local.entity.SessionRecordEntity
import com.mindless.screen.data.local.entity.SubscriptionStateEntity
import com.mindless.screen.data.local.entity.UnlockEventEntity

@Database(
    entities = [
        AppUsageRecordEntity::class,
        UnlockEventEntity::class,
        SessionRecordEntity::class,
        HourlyAggregateEntity::class,
        DailyAggregateEntity::class,
        AppCategoryMapEntity::class,
        AnalyticsEventEntity::class,
        SubscriptionStateEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun trackingDao(): TrackingDao
    abstract fun aggregateDao(): AggregateDao
    abstract fun categoryDao(): CategoryDao
    abstract fun analyticsDao(): AnalyticsDao
}
