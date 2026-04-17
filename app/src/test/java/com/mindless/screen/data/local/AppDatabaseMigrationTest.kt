package com.mindless.screen.data.local

import android.database.sqlite.SQLiteDatabase
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class AppDatabaseMigrationTest {

    private val context = ApplicationProvider.getApplicationContext<android.content.Context>()
    private val databaseName = "app-database-migration-test"

    @After
    fun tearDown() {
        context.deleteDatabase(databaseName)
    }

    @Test
    fun migrationFrom1To2AddsPlan2TablesAndPreservesExistingData() {
        context.deleteDatabase(databaseName)
        val databaseFile = context.getDatabasePath(databaseName)
        databaseFile.parentFile?.mkdirs()

        SQLiteDatabase.openOrCreateDatabase(databaseFile, null).use { database ->
            createVersion1Schema(database)
            database.execSQL(
                """
                INSERT INTO app_usage_records (packageName, foregroundMillis, recordedAtEpochMillis)
                VALUES ('com.example.app', 180000, 1710000000000)
                """.trimIndent()
            )
            database.setVersion(1)
        }

        val migratedRoomDatabase = Room.databaseBuilder(context, AppDatabase::class.java, databaseName)
            .addMigrations(AppDatabase.MIGRATION_1_2)
            .build()

        try {
            val migratedDatabase = migratedRoomDatabase.openHelper.writableDatabase

            assertTrue(tableExists(migratedDatabase, "insight_records"))
            assertTrue(tableExists(migratedDatabase, "personality_snapshots"))
            assertTrue(tableExists(migratedDatabase, "gamification_states"))

            migratedDatabase.query("SELECT packageName, foregroundMillis FROM app_usage_records")
                .use { cursor ->
                    assertTrue(cursor.moveToFirst())
                    assertEquals("com.example.app", cursor.getString(0))
                    assertEquals(180000L, cursor.getLong(1))
                }
        } finally {
            migratedRoomDatabase.close()
        }
    }

    private fun tableExists(database: androidx.sqlite.db.SupportSQLiteDatabase, tableName: String): Boolean {
        database.query(
            "SELECT name FROM sqlite_master WHERE type='table' AND name=?",
            arrayOf(tableName)
        ).use { cursor ->
            return cursor.moveToFirst()
        }
    }

    private fun createVersion1Schema(database: SQLiteDatabase) {
        database.execSQL(
            """
            CREATE TABLE IF NOT EXISTS app_usage_records (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                packageName TEXT NOT NULL,
                foregroundMillis INTEGER NOT NULL,
                recordedAtEpochMillis INTEGER NOT NULL
            )
            """.trimIndent()
        )

        database.execSQL(
            """
            CREATE TABLE IF NOT EXISTS unlock_events (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                unlockedAtEpochMillis INTEGER NOT NULL
            )
            """.trimIndent()
        )

        database.execSQL(
            """
            CREATE TABLE IF NOT EXISTS session_records (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                sessionStartEpochMillis INTEGER NOT NULL,
                sessionEndEpochMillis INTEGER
            )
            """.trimIndent()
        )

        database.execSQL(
            """
            CREATE TABLE IF NOT EXISTS hourly_aggregates (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                hourStartEpochMillis INTEGER NOT NULL,
                totalScreenTimeMillis INTEGER NOT NULL
            )
            """.trimIndent()
        )

        database.execSQL(
            """
            CREATE TABLE IF NOT EXISTS daily_aggregates (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                dayStartEpochMillis INTEGER NOT NULL,
                totalScreenTimeMillis INTEGER NOT NULL
            )
            """.trimIndent()
        )

        database.execSQL(
            """
            CREATE TABLE IF NOT EXISTS app_category_map (
                packageName TEXT NOT NULL,
                category TEXT NOT NULL,
                PRIMARY KEY(packageName)
            )
            """.trimIndent()
        )

        database.execSQL(
            """
            CREATE TABLE IF NOT EXISTS analytics_events (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                eventName TEXT NOT NULL,
                timestampEpochMillis INTEGER NOT NULL
            )
            """.trimIndent()
        )

        database.execSQL(
            """
            CREATE TABLE IF NOT EXISTS subscription_state (
                `key` TEXT NOT NULL,
                isPremium INTEGER NOT NULL,
                updatedAtEpochMillis INTEGER NOT NULL,
                PRIMARY KEY(`key`)
            )
            """.trimIndent()
        )
    }
}
