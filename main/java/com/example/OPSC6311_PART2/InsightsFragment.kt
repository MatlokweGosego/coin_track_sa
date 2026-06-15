package com.example.OPSC6311_PART2

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.example.OPSC6311_PART2.databinding.FragmentInsightsBinding
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class InsightsFragment : Fragment() {

    private var _binding: FragmentInsightsBinding? = null
    private val binding get() = _binding!!

    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var sessionManager: SessionManager

    private val currencyFormat = NumberFormat.getCurrencyInstance().apply {
        currency = Currency.getInstance("ZAR")
    }

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInsightsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        databaseHelper = DatabaseHelper(requireContext())
        sessionManager = SessionManager(requireContext())

        setupPeriodSpinner()
        loadInsights()
    }

    private fun setupPeriodSpinner() {
        val periods = arrayOf("This Month", "Last Month", "Last 3 Months", "This Year")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, periods)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerPeriod.adapter = adapter

        binding.spinnerPeriod.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                loadInsights()
                loadCategoryChart()
                loadGoalPerformance()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun getDateRangeForPeriod(period: String): Pair<String, String> {
        val calendar = Calendar.getInstance()
        val endDate = dateFormat.format(calendar.time)

        val startDate = when (binding.spinnerPeriod.selectedItemPosition) {
            0 -> { // This Month
                calendar.set(Calendar.DAY_OF_MONTH, 1)
                dateFormat.format(calendar.time)
            }
            1 -> { // Last Month
                calendar.add(Calendar.MONTH, -1)
                calendar.set(Calendar.DAY_OF_MONTH, 1)
                val start = dateFormat.format(calendar.time)
                calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
                dateFormat.format(calendar.time)
                start
            }
            2 -> { // Last 3 Months
                calendar.add(Calendar.MONTH, -3)
                dateFormat.format(calendar.time)
            }
            3 -> { // This Year
                calendar.set(Calendar.DAY_OF_YEAR, 1)
                dateFormat.format(calendar.time)
            }
            else -> {
                calendar.set(Calendar.DAY_OF_MONTH, 1)
                dateFormat.format(calendar.time)
            }
        }

        return Pair(startDate, endDate)
    }

    private fun loadInsights() {
        val user = sessionManager.getUserDetails() ?: return
        val (startDate, endDate) = getDateRangeForPeriod(binding.spinnerPeriod.selectedItem.toString())

        // Get expenses and income for period
        val periodExpenses = databaseHelper.getTotalExpensesByPeriod(user.id, startDate, endDate)
        val periodIncome = databaseHelper.getIncomeByDateRange(user.id, startDate, endDate)

        // Get monthly budget
        val monthlyBudget = databaseHelper.getBudgetByPeriod(user.id, "monthly")?.amount ?: 0.0

        // Calculate insights
        val savings = periodIncome - periodExpenses
        val savingsRate = if (periodIncome > 0) (savings / periodIncome * 100) else 0.0
        val budgetUsage = if (monthlyBudget > 0) (periodExpenses / monthlyBudget * 100) else 0.0

        // Update UI
        binding.tvPeriodRange.text = "$startDate to $endDate"
        binding.tvTotalIncome.text = "Income: ${currencyFormat.format(periodIncome)}"
        binding.tvTotalExpenses.text = "Expenses: ${currencyFormat.format(periodExpenses)}"
        binding.tvNetSavings.text = "Net Savings: ${currencyFormat.format(savings)}"

        binding.tvSavingsRate.text = "Savings Rate: ${String.format("%.1f", savingsRate)}%"
        binding.progressSavings.progress = savingsRate.toInt().coerceIn(0, 100)

        binding.tvBudgetUsage.text = "Budget Usage: ${String.format("%.1f", budgetUsage)}%"
        binding.progressBudgetUsage.progress = budgetUsage.toInt().coerceIn(0, 100)

        // Set colors based on performance
        when {
            savingsRate >= 20 -> binding.progressSavings.progressTintList =
                android.content.res.ColorStateList.valueOf(Color.parseColor("#4CAF50"))
            savingsRate >= 10 -> binding.progressSavings.progressTintList =
                android.content.res.ColorStateList.valueOf(Color.parseColor("#FF9800"))
            else -> binding.progressSavings.progressTintList =
                android.content.res.ColorStateList.valueOf(Color.parseColor("#F44336"))
        }

        when {
            budgetUsage <= 80 -> binding.progressBudgetUsage.progressTintList =
                android.content.res.ColorStateList.valueOf(Color.parseColor("#4CAF50"))
            budgetUsage <= 100 -> binding.progressBudgetUsage.progressTintList =
                android.content.res.ColorStateList.valueOf(Color.parseColor("#FF9800"))
            else -> binding.progressBudgetUsage.progressTintList =
                android.content.res.ColorStateList.valueOf(Color.parseColor("#F44336"))
        }
    }

    private fun loadCategoryChart() {
        val user = sessionManager.getUserDetails() ?: return
        val (startDate, endDate) = getDateRangeForPeriod(binding.spinnerPeriod.selectedItem.toString())

        val categoryData = databaseHelper.getExpensesByCategoryForPeriod(user.id, startDate, endDate)
        val pieEntries = mutableListOf<PieEntry>()
        val colors = mutableListOf<Int>()

        for (category in categoryData) {
            if (category.totalSpent > 0) {
                pieEntries.add(PieEntry(category.totalSpent.toFloat(), category.categoryName))
                try {
                    colors.add(Color.parseColor(category.categoryColor))
                } catch (e: Exception) {
                    colors.add(ColorTemplate.COLORFUL_COLORS[pieEntries.size % ColorTemplate.COLORFUL_COLORS.size])
                }
            }
        }

        if (pieEntries.isNotEmpty()) {
            binding.chartExpenses.visibility = View.VISIBLE
            binding.tvChartEmpty.visibility = View.GONE

            val pieDataSet = PieDataSet(pieEntries, "Expenses by Category")
            pieDataSet.setColors(colors)
            pieDataSet.valueTextSize = 12f
            pieDataSet.valueTextColor = Color.WHITE
            pieDataSet.valueFormatter = PercentFormatter()
            pieDataSet.setDrawValues(true)

            val pieData = PieData(pieDataSet)

            binding.chartExpenses.apply {
                data = pieData
                description.isEnabled = false
                setUsePercentValues(true)
                setExtraOffsets(5f, 10f, 5f, 5f)
                dragDecelerationFrictionCoef = 0.95f
                isDrawHoleEnabled = true
                setHoleColor(Color.TRANSPARENT)
                setTransparentCircleColor(Color.WHITE)
                setTransparentCircleAlpha(110)
                holeRadius = 40f
                transparentCircleRadius = 45f
                animateY(1400, Easing.EaseInOutQuad)
                legend.isEnabled = true
                setEntryLabelColor(Color.WHITE)
                setEntryLabelTextSize(12f)
                centerText = "Spending by\nCategory"
                setCenterTextColor(Color.WHITE)
                setCenterTextSize(14f)
                invalidate()
            }
        } else {
            binding.chartExpenses.visibility = View.GONE
            binding.tvChartEmpty.visibility = View.VISIBLE
            binding.tvChartEmpty.text = "No expense data for this period\nAdd some expenses to see the chart!"
        }
    }

    private fun loadGoalPerformance() {
        val user = sessionManager.getUserDetails() ?: return
        val (startDate, endDate) = getDateRangeForPeriod(binding.spinnerPeriod.selectedItem.toString())

        val categoryData = databaseHelper.getExpensesByCategoryForPeriod(user.id, startDate, endDate)

        var categoriesWithinMinGoal = 0
        var categoriesWithinMaxGoal = 0
        var totalCategories = 0

        for (category in categoryData) {
            if (category.minGoal > 0 || category.maxGoal > 0) {
                totalCategories++
                if (category.minGoal > 0 && category.totalSpent >= category.minGoal) {
                    categoriesWithinMinGoal++
                }
                if (category.maxGoal > 0 && category.totalSpent <= category.maxGoal) {
                    categoriesWithinMaxGoal++
                }
            }
        }

        val minGoalPercentage = if (totalCategories > 0) (categoriesWithinMinGoal * 100 / totalCategories) else 0
        val maxGoalPercentage = if (totalCategories > 0) (categoriesWithinMaxGoal * 100 / totalCategories) else 0

        if (totalCategories > 0) {
            binding.tvMinGoalPerformance.text = "Minimum Goals Met: $categoriesWithinMinGoal/$totalCategories"
            binding.progressMinGoals.progress = minGoalPercentage

            binding.tvMaxGoalPerformance.text = "Maximum Goals Met: $categoriesWithinMaxGoal/$totalCategories"
            binding.progressMaxGoals.progress = maxGoalPercentage
        } else {
            binding.tvMinGoalPerformance.text = "No goals set for any category"
            binding.progressMinGoals.progress = 0
            binding.tvMaxGoalPerformance.text = "Set min/max goals in Categories tab"
            binding.progressMaxGoals.progress = 0
        }

        // Visual feedback for goal performance
        val performanceText = when {
            maxGoalPercentage >= 80 && totalCategories > 0 -> "Excellent! You're staying within your budget goals! 🎉"
            maxGoalPercentage >= 60 && totalCategories > 0 -> "Good job! Keep monitoring your spending to stay within goals 💪"
            maxGoalPercentage >= 40 && totalCategories > 0 -> "You're doing okay. Try to reduce spending in some categories 📊"
            totalCategories > 0 -> "Watch your spending! You're exceeding your goals in many categories ⚠️"
            else -> "Go to Categories tab and set min/max goals for your categories to track performance! 📋"
        }
        binding.tvGoalPerformanceMessage.text = performanceText

        // Set progress bar colors
        val minColor = if (minGoalPercentage >= 70) Color.parseColor("#4CAF50") else Color.parseColor("#FF9800")
        val maxColor = if (maxGoalPercentage >= 70) Color.parseColor("#4CAF50") else Color.parseColor("#FF9800")

        binding.progressMinGoals.progressTintList = android.content.res.ColorStateList.valueOf(minColor)
        binding.progressMaxGoals.progressTintList = android.content.res.ColorStateList.valueOf(maxColor)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}