package com.example.coin_track_sa.data.entities
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "expenses",
    foreignKeys = [ForeignKey(
        entity = Category::class,
        parentColumns = ["id"],
        childColumns = ["categoryId"],
        onDelete = ForeignKey.SET_NULL
    )]
)
data class Expense(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val amount: Double,
    val date: Long,
    val startTime: Long? = null,
    val endTime: Long? = null,
    val description: String? = null,
    @ColumnInfo(index = true) val categoryId: Long? = null,
    val photoUri: String? = null
)