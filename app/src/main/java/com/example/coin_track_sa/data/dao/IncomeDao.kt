package com.example.coin_track_sa.data.dao

import androidx.room.*
import com.example.coin_track_sa.data.entities.Income
import kotlinx.coroutines.flow.Flow

@Dao
interface IncomeDao {
    @Insert
    suspend fun insert(income: Income): Long

    @Update
    suspend fun update(income: Income)

    @Delete
    suspend fun delete(income: Income)

    @Query("SELECT * FROM incomes WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    fun getIncomesBetween(startDate: Long, endDate: Long): Flow<List<Income>>

    @Query("SELECT SUM(amount) FROM incomes WHERE date BETWEEN :startDate AND :endDate")
    suspend fun getTotalIncomeBetween(startDate: Long, endDate: Long): Double?
}