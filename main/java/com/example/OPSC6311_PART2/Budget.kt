package com.example.OPSC6311_PART2

// Budget.kt

data class Budget(
    val id: Long = 0,
    val amount: Double,
    val period: String, // "daily", "weekly", "monthly", "yearly"
    val startDate: String,
    val endDate: String,
    val userId: Long
)