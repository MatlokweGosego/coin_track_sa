package com.example.OPSC6311_PART2


data class CategorySummary(
    val id: Long,
    val name: String,
    val totalSpent: Double,
    val budget: Double = 0.0
)
