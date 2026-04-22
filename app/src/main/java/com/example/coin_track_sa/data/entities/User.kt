package com.example.coin_track_sa.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "username") val username: String,
    @ColumnInfo(name = "password_hash") val passwordHash: String,
    @ColumnInfo(name = "email") val email: String? = null,
    @ColumnInfo(name = "fitness_level") val fitnessLevel: String = "Rookie",
    @ColumnInfo(name = "points") val points: Int = 0
)