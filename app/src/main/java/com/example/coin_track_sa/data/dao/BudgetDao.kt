package com.example.coin_track_sa.data.dao

import androidx.room.*
import com.example.coin_track_sa.data.entities.Budget
import kotlinx.coroutines.flow.Flow

@Dao
interface BudgetDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(budget: Budget)

    @Query("SELECT * FROM budget WHERE id = 1")
    fun getBudget(): Flow<Budget?>
}