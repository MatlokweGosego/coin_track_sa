package com.example.coin_track_sa.data.dao

import androidx.room.*
import com.example.coin_track_sa.data.entities.Expense
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {
    @Insert
    suspend fun insert(expense: Expense): Long

    @Update
    suspend fun update(expense: Expense)

    @Delete
    suspend fun delete(expense: Expense)

    @Query("SELECT * FROM expenses WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC, startTime DESC")
    fun getExpensesBetween(startDate: Long, endDate: Long): Flow<List<Expense>>

    @Query("SELECT * FROM expenses ORDER BY date DESC LIMIT :limit")
    fun getRecentExpenses(limit: Int): Flow<List<Expense>>

    @Query("SELECT SUM(amount) FROM expenses WHERE date BETWEEN :startDate AND :endDate")
    suspend fun getTotalExpenseBetween(startDate: Long, endDate: Long): Double?
}