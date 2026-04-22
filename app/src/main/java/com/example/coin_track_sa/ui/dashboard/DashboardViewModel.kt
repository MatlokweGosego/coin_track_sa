package com.example.coin_track_sa.ui.dashboard

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.asLiveData
import com.example.coin_track_sa.data.AppDatabase
import com.example.coin_track_sa.utils.SessionManager
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import java.util.Calendar

class DashboardViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    private val userId = SessionManager.getUserId(application)

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    val fitnessLevel = MutableLiveData("Rookie")
    val points = MutableLiveData(0)
    val totalBalance = MutableLiveData(0.0)
    val safeToSpend = MutableLiveData(0.0)
    val budgetProgress = MutableLiveData(BudgetProgress(0.0, 0.0, 0.0, 0))

    val recentExpenses = db.expenseDao().getRecentExpenses(5).asLiveData()

    init {
        loadUserData()
    }

    private fun loadUserData() {
        viewModelScope.launch {
            val user = db.userDao().getUserById(userId)
            user?.let {
                fitnessLevel.value = it.fitnessLevel
                points.value = it.points
            }
        }
    }

    fun refreshData() {
        viewModelScope.launch {
            _isLoading.value = true
            val monthStart = getMonthStartTimestamp()
            val monthEnd = getMonthEndTimestamp()

            // Fetch data concurrently
            val incomes = db.incomeDao().getTotalIncomeBetween(monthStart, monthEnd)
            val expenses = db.expenseDao().getTotalExpenseBetween(monthStart, monthEnd)
            val budgetFlow = db.budgetDao().getBudget()

            val totalIncome = incomes ?: 0.0
            val totalExpense = expenses ?: 0.0
            val balance = totalIncome - totalExpense

            totalBalance.postValue(balance)

            val budget = budgetFlow.first()
            val committedExpenses = 0.0 // Placeholder
            val safe = if (budget != null) {
                minOf(balance, budget.totalBudget - committedExpenses)
            } else {
                balance
            }
            safeToSpend.postValue(safe.coerceAtLeast(0.0))

            // Budget progress
            val total = budget?.totalBudget ?: 0.0
            val spent = totalExpense
            val remaining = (total - spent).coerceAtLeast(0.0)
            val percentage = if (total > 0) ((spent / total) * 100).toInt() else 0
            budgetProgress.postValue(BudgetProgress(total, spent, remaining, percentage))

            _isLoading.postValue(false)
        }
    }

    private fun getMonthStartTimestamp(): Long {
        val cal = Calendar.getInstance()
        cal.set(Calendar.DAY_OF_MONTH, 1)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    private fun getMonthEndTimestamp(): Long {
        val cal = Calendar.getInstance()
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH))
        cal.set(Calendar.HOUR_OF_DAY, 23)
        cal.set(Calendar.MINUTE, 59)
        cal.set(Calendar.SECOND, 59)
        cal.set(Calendar.MILLISECOND, 999)
        return cal.timeInMillis
    }

    data class BudgetProgress(val total: Double, val spent: Double, val remaining: Double, val percentage: Int)
}