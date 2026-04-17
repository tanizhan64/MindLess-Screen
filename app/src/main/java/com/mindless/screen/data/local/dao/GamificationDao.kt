package com.mindless.screen.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mindless.screen.data.local.entity.GamificationStateEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GamificationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertState(state: GamificationStateEntity)

    @Query("SELECT * FROM gamification_states WHERE dateEpochMillis = :dateEpochMillis LIMIT 1")
    fun getStateForDate(dateEpochMillis: Long): Flow<GamificationStateEntity?>
}
