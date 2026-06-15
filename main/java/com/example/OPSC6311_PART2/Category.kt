package com.example.OPSC6311_PART2

data class Category(
    val id: Long = 0,
    val name: String,
    val color: String,
    val budget: Double,
    val minGoal: Double = 0.0,
    val maxGoal: Double = 0.0,
    val userId: Long
)