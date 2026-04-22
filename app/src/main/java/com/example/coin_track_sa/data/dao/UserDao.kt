package com.example.coin_track_sa.data.dao

import androidx.room.*
import com.example.coin_track_sa.data.entities.User

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertUser(user: User): Long

    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    suspend fun getUserByUsername(username: String): User?

    @Query("UPDATE users SET fitness_level = :level, points = :points WHERE id = :userId")
    suspend fun updateFitness(userId: Long, level: String, points: Int)

    @Query("SELECT * FROM users WHERE id = :userId")
    suspend fun getUserById(userId: Long): User?
}