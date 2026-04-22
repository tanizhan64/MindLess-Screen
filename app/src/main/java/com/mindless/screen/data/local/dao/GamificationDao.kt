package com.mindless.screen.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mindless.screen.data.local.entity.GamificationStateEntity

@Dao
interface GamificationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertState(state: GamificationStateEntity)

    @Query("SELECT * FROM gamification_states WHERE key = :key LIMIT 1")
    fun stateByKey(key: String = "active_state"): GamificationStateEntity?
}
