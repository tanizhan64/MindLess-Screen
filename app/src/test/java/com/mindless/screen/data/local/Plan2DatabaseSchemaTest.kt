package com.mindless.screen.data.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class Plan2DatabaseSchemaTest {

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
    fun plan2TablesExistInSqliteMaster() {
        val requiredTables = setOf(
            "insight_records",
            "personality_snapshots",
            "gamification_states"
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

    @Test
    fun plan2TablesContainExpectedColumns() {
        assertEquals(
            setOf("id", "dateEpochMillis", "type", "severity", "message", "recommendation"),
            tableColumns("insight_records")
        )
        assertEquals(
            setOf("id", "dateEpochMillis", "personalityType", "confidence"),
            tableColumns("personality_snapshots")
        )
        assertEquals(
            setOf("dateEpochMillis", "streakDays", "focusLevel", "badgesJson"),
            tableColumns("gamification_states")
        )
    }

    private fun tableColumns(tableName: String): Set<String> {
        val columns = mutableSetOf<String>()
        val cursor = database.openHelper.writableDatabase.query("PRAGMA table_info($tableName)")

        cursor.use {
            while (it.moveToNext()) {
                columns.add(it.getString(it.getColumnIndexOrThrow("name")))
            }
        }

        return columns
    }
}
