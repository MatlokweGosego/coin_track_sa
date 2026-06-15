package com.example.OPSC6311_PART2

data class Achievement(
    val id: Long = 0,
    val name: String,
    val description: String,
    val type: String,
    val requirement: Double,
    val icon: String,
    val isUnlocked: Boolean = false,
    val dateEarned: String = ""
)