package com.mindless.screen.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mindless.screen.data.local.entity.PersonalitySnapshotEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PersonalityDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSnapshot(snapshot: PersonalitySnapshotEntity)

    @Query("SELECT * FROM personality_snapshots ORDER BY dateEpochMillis DESC, id DESC LIMIT 1")
    fun getLatestSnapshot(): Flow<PersonalitySnapshotEntity?>
}
