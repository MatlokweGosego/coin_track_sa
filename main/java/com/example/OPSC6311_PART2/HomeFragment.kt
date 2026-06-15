package com.example.OPSC6311_PART2

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.OPSC6311_PART2.databinding.FragmentHomeBinding
import java.text.NumberFormat
import java.util.*
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import java.text.SimpleDateFormat

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var sessionManager: SessionManager

    private lateinit var expenseAdapter: ExpenseAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        databaseHelper = DatabaseHelper(requireContext())
        sessionManager = SessionManager(requireContext())


        setupUI()
        loadData()
        setupClickListeners()
    }

    private fun setupUI() {
        val user = sessionManager.getUserDetails()
        binding.tvWelcome.text = "Welcome, ${user?.name ?: "User"}"

        binding.rvRecentExpenses.layoutManager = LinearLayoutManager(requireContext())
        expenseAdapter = ExpenseAdapter(emptyList())
        binding.rvRecentExpenses.adapter = expenseAdapter
    }

    private fun setupClickListeners() {
        // Income button
        binding.btnIncome.setOnClickListener {
            showAddIncomeDialog()
        }

        // Expense button


        // Rewards card - Navigate to Gamification


        // Insights card - Navigate to Insights


        // Gamification card - Navigate to Gamification

    }

    private fun loadData() {
        val user = sessionManager.getUserDetails() ?: return

        // Get recent expenses
        val expenses = databaseHelper.getAllExpenses(user.id).take(5)
        if (expenses.isEmpty()) {
            binding.tvNoExpenses.visibility = View.VISIBLE
            binding.rvRecentExpenses.visibility = View.GONE
        } else {
            binding.tvNoExpenses.visibility = View.GONE
            binding.rvRecentExpenses.visibility = View.VISIBLE
            expenseAdapter.updateExpenses(expenses)
        }

        // Calculate total balance
        val totalExpenses = expenses.sumOf { it.amount }
        val totalIncome = databaseHelper.getTotalIncomeByUser(user.id)
        val balance = totalIncome - totalExpenses

        val formatter = NumberFormat.getCurrencyInstance().apply {
            currency = Currency.getInstance("ZAR")
        }
        binding.tvTotalBalance.text = formatter.format(balance)


    }


    private fun showAddIncomeDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_income, null)
        val etAmount = dialogView.findViewById<EditText>(R.id.etIncomeAmount)
        val etSource = dialogView.findViewById<EditText>(R.id.etIncomeSource)
        val etNote = dialogView.findViewById<EditText>(R.id.etIncomeNote)
        val btnCancel = dialogView.findViewById<Button>(R.id.btnCancelIncome)
        val btnSave = dialogView.findViewById<Button>(R.id.btnSaveIncome)

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        btnSave.setOnClickListener {
            val amountStr = etAmount.text.toString().trim()
            val source = etSource.text.toString().trim()
            val note = etNote.text.toString().trim()

            if (amountStr.isEmpty()) {
                Toast.makeText(requireContext(), "Please enter an amount", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (source.isEmpty()) {
                Toast.makeText(requireContext(), "Please enter a source", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            try {
                val amount = amountStr.toDouble()
                val user = sessionManager.getUserDetails()

                if (user != null) {

                    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    val currentDate = dateFormat.format(Date())

                    val income = Income(
                        amount = amount,
                        source = source,
                        note = note,
                        date = currentDate,
                        userId = user.id
                    )

                    val id = databaseHelper.addIncome(income)

                    if (id > 0) {
                        Toast.makeText(requireContext(), "Income added successfully", Toast.LENGTH_SHORT).show()
                        loadData() // Refresh data
                        dialog.dismiss()
                    } else {
                        Toast.makeText(requireContext(), "Failed to add income. Please try again.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "User session not found. Please log in again.", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                Log.e("IncomeDialog", "Error adding income", e)
            }
        }

        dialog.show()
    }

    override fun onResume() {
        super.onResume()
        loadData() // Refresh data when returning to fragment
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}