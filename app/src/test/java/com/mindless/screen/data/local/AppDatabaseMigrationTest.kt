package com.mindless.screen.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.test.core.app.ApplicationProvider
import com.mindless.screen.data.local.entity.AnalyticsEventEntity
import com.mindless.screen.data.local.entity.AppCategoryMapEntity
import com.mindless.screen.data.local.entity.AppUsageRecordEntity
import com.mindless.screen.data.local.entity.DailyAggregateEntity
import com.mindless.screen.data.local.entity.HourlyAggregateEntity
import com.mindless.screen.data.local.entity.SessionRecordEntity
import com.mindless.screen.data.local.entity.SubscriptionStateEntity
import com.mindless.screen.data.local.entity.UnlockEventEntity
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class AppDatabaseMigrationTest {

    private val context = ApplicationProvider.getApplicationContext<Context>()
    private val databaseName = "app-database-migration-test"

    @After
    fun tearDown() {
        context.deleteDatabase(databaseName)
    }

    @Test
    fun migrationFrom1To2AddsPlan2TablesAndPreservesExistingData() {
        context.deleteDatabase(databaseName)
        createVersion1DatabaseWithSampleData()

        val migratedRoomDatabase = Room.databaseBuilder(context, AppDatabase::class.java, databaseName)
            .addMigrations(AppDatabase.MIGRATION_1_2)
            .build()

        try {
            val migratedDatabase = migratedRoomDatabase.openHelper.writableDatabase

            assertTrue(tableExists(migratedDatabase, "insight_records"))
            assertTrue(tableExists(migratedDatabase, "personality_snapshots"))
            assertTrue(tableExists(migratedDatabase, "gamification_states"))

            migratedDatabase.query("SELECT packageName, totalForegroundMillis FROM app_usage_records")
                .use { cursor ->
                    assertTrue(cursor.moveToFirst())
                    assertEquals("com.example.app", cursor.getString(0))
                    assertEquals(180000L, cursor.getLong(1))
                }
        } finally {
            migratedRoomDatabase.close()
        }
    }

    private fun createVersion1DatabaseWithSampleData() {
        val legacyDatabase = Room.databaseBuilder(context, LegacyV1Database::class.java, databaseName).build()
        try {
            legacyDatabase.openHelper.writableDatabase.execSQL(
                """
                INSERT INTO app_usage_records (
                    packageName,
                    dateEpochMillis,
                    totalForegroundMillis,
                    launchCount,
                    hourlyBucket,
                    category,
                    source,
                    confidence,
                    wasBackgrounded,
                    recordedAtEpochMillis
                ) VALUES (
                    'com.example.app',
                    1710000000000,
                    180000,
                    3,
                    10,
                    'social',
                    'USAGE_STATS',
                    0.9,
                    0,
                    1710000000000
                )
                """.trimIndent()
            )
        } finally {
            legacyDatabase.close()
        }
    }

    private fun tableExists(database: SupportSQLiteDatabase, tableName: String): Boolean {
        database.query(
            "SELECT name FROM sqlite_master WHERE type='table' AND name=?",
            arrayOf(tableName)
        ).use { cursor ->
            return cursor.moveToFirst()
        }
    }
}

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
    ],
    version = 1,
    exportSchema = false,
)
abstract class LegacyV1Database : RoomDatabase()