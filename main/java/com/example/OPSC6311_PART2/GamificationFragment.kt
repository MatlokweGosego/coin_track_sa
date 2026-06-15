package com.example.OPSC6311_PART2

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.OPSC6311_PART2.databinding.FragmentGamificationBinding
import java.text.SimpleDateFormat
import java.util.*

class GamificationFragment : Fragment() {

    private var _binding: FragmentGamificationBinding? = null
    private val binding get() = _binding!!

    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var sessionManager: SessionManager
    private lateinit var achievementAdapter: AchievementAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGamificationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        databaseHelper = DatabaseHelper(requireContext())
        sessionManager = SessionManager(requireContext())

        setupUI()
        loadAchievements()
        loadUserStats()
    }

    private fun setupUI() {
        binding.rvAchievements.layoutManager = LinearLayoutManager(requireContext())
        achievementAdapter = AchievementAdapter(emptyList())
        binding.rvAchievements.adapter = achievementAdapter
    }

    private fun loadAchievements() {
        val user = sessionManager.getUserDetails() ?: return
        val achievements = databaseHelper.getUserAchievements(user.id)

        val unlockedCount = achievements.count { it.isUnlocked }
        val totalCount = achievements.size

        binding.tvAchievementStats.text = "$unlockedCount / $totalCount Achievements Unlocked"

        val progressPercentage = if (totalCount > 0) (unlockedCount * 100 / totalCount) else 0
        binding.progressAchievements.progress = progressPercentage

        if (achievements.isEmpty()) {
            binding.tvNoAchievements.visibility = View.VISIBLE
            binding.rvAchievements.visibility = View.GONE
        } else {
            binding.tvNoAchievements.visibility = View.GONE
            binding.rvAchievements.visibility = View.VISIBLE
            achievementAdapter.updateAchievements(achievements)
        }
    }

    private fun loadUserStats() {
        val user = sessionManager.getUserDetails() ?: return

        // Get total expenses
        val expenses = databaseHelper.getAllExpenses(user.id)
        val totalExpenses = expenses.size
        val totalAmount = expenses.sumOf { it.amount }

        // Get total income
        val totalIncome = databaseHelper.getTotalIncomeByUser(user.id)

        // Get savings rate
        val savingsRate = if (totalIncome > 0) ((totalIncome - totalAmount) / totalIncome * 100) else 0.0

        binding.tvTotalExpenses.text = "Total Expenses: $totalExpenses"
        binding.tvTotalAmount.text = String.format("Total Spent: R%.2f", totalAmount)
        binding.tvSavingsRate.text = String.format("Savings Rate: %.1f%%", savingsRate)

        // Set progress bar color based on savings rate
        val color = when {
            savingsRate >= 20 -> android.graphics.Color.parseColor("#4CAF50")
            savingsRate >= 10 -> android.graphics.Color.parseColor("#FF9800")
            else -> android.graphics.Color.parseColor("#F44336")
        }
        binding.progressSavingsRate.progressTintList = android.content.res.ColorStateList.valueOf(color)
        binding.progressSavingsRate.progress = savingsRate.toInt().coerceIn(0, 100)

        // Show tip based on performance
        val tip = when {
            savingsRate >= 20 -> "Excellent! You're a saving master! 🎉"
            savingsRate >= 10 -> "Good job! Keep going to reach 20% savings! 💪"
            savingsRate > 0 -> "You're saving! Try to increase your savings rate 📈"
            else -> "Start saving today! Every little bit counts 💰"
        }
        binding.tvTip.text = tip
    }

    override fun onResume() {
        super.onResume()
        loadAchievements()
        loadUserStats()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}