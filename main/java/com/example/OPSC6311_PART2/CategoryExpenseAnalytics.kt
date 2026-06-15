package com.example.OPSC6311_PART2

// Single data class definition for analytics
data class CategoryExpenseAnalytics(

val categoryId: Long,
val categoryName: String,
val categoryColor: String,
val budget: Double,
val totalSpent: Double,
val minGoal: Double,
val maxGoal: Double
)

