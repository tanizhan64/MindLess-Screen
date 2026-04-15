package com.mindless.screen.data.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class AppDatabaseSchemaTest {

    private lateinit var database: AppDatabase

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).build()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun requiredTablesExistInSqliteMaster() {
        val requiredTables = setOf(
            "app_usage_records",
            "session_records",
            "hourly_aggregates",
            "daily_aggregates",
            "app_category_map",
            "analytics_events"
        )

        val existingTables = mutableSetOf<String>()
        val cursor = database.openHelper.writableDatabase.query(
            "SELECT name FROM sqlite_master WHERE type='table'"
        )

        cursor.use {
            while (it.moveToNext()) {
                existingTables.add(it.getString(0))
            }
        }

        assertTrue(existingTables.containsAll(requiredTables))
    }
}
