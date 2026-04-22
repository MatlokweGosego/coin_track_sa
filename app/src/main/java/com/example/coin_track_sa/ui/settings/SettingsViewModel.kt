package com.example.coin_track_sa.ui.settings



import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.coin_track_sa.data.AppDatabase
import com.example.coin_track_sa.data.entities.Budget
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileWriter

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)

    private val _exportStatus = MutableLiveData<String?>()
    val exportStatus: LiveData<String?> = _exportStatus

    fun updateBudgetResetDay(day: Int) {
        viewModelScope.launch {
            val currentBudget = db.budgetDao().getBudget().receive(null)
            if (currentBudget != null) {
                db.budgetDao().insertOrUpdate(currentBudget.copy(resetDay = day))
            }
        }
    }

    fun exportData() {
        viewModelScope.launch {
            try {
                val expenses = db.expenseDao().getExpensesBetween(0, Long.MAX_VALUE).receive(emptyList())
                val file = File(getApplication<Application>().getExternalFilesDir(null), "expenses_export.csv")
                FileWriter(file).use { writer ->
                    writer.write("ID,Amount,Date,StartTime,EndTime,Description,CategoryId,PhotoUri\n")
                    expenses.forEach { expense ->
                        writer.write("${expense.id},${expense.amount},${expense.date},${expense.startTime},${expense.endTime},${expense.description},${expense.categoryId},${expense.photoUri}\n")
                    }
                }
                _exportStatus.postValue("Exported to ${file.absolutePath}")
            } catch (e: Exception) {
                _exportStatus.postValue("Export failed: ${e.message}")
            }
        }
    }

    fun clearExportStatus() {
        _exportStatus.value = null
    }

    private suspend fun <T> LiveData<T>.receive(default: T): T = (this as? MutableLiveData)?.value ?: default
}






