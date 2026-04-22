package com.example.coin_track_sa.data.entities
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "budget")
data class Budget(
    @PrimaryKey val id: Int = 1,
    val totalBudget: Double,
    val resetDay: Int = 1
)