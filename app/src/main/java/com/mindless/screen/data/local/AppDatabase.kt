package com.mindless.screen.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.mindless.screen.data.local.dao.AggregateDao
import com.mindless.screen.data.local.dao.AnalyticsDao
import com.mindless.screen.data.local.dao.CategoryDao
import com.mindless.screen.data.local.dao.GamificationDao
import com.mindless.screen.data.local.dao.InsightDao
import com.mindless.screen.data.local.dao.PersonalityDao
import com.mindless.screen.data.local.dao.TrackingDao
import com.mindless.screen.data.local.entity.AnalyticsEventEntity
import com.mindless.screen.data.local.entity.AppCategoryMapEntity
import com.mindless.screen.data.local.entity.AppUsageRecordEntity
import com.mindless.screen.data.local.entity.DailyAggregateEntity
import com.mindless.screen.data.local.entity.GamificationStateEntity
import com.mindless.screen.data.local.entity.HourlyAggregateEntity
import com.mindless.screen.data.local.entity.InsightRecordEntity
import com.mindless.screen.data.local.entity.PersonalitySnapshotEntity
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
        SubscriptionStateEntity::class,
        InsightRecordEntity::class,
        PersonalitySnapshotEntity::class,
        GamificationStateEntity::class
    ],
    version = 2,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun trackingDao(): TrackingDao
    abstract fun aggregateDao(): AggregateDao
    abstract fun categoryDao(): CategoryDao
    abstract fun analyticsDao(): AnalyticsDao
    abstract fun insightDao(): InsightDao
    abstract fun personalityDao(): PersonalityDao
    abstract fun gamificationDao(): GamificationDao
}
