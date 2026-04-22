package com.example.coin_track_sa.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.coin_track_sa.databinding.FragmentDashboardBinding
import com.example.coin_track_sa.ui.expense.ExpenseAdapter
import com.example.coin_track_sa.utils.CurrencyUtils
import kotlinx.coroutines.launch

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    private val viewModel: DashboardViewModel by viewModels()
    private lateinit var expenseAdapter: ExpenseAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        setupRecyclerView()
        observeViewModel()

        binding.fabAddExpense.setOnClickListener {
            // Navigate to add expense screen (not implemented here)
        }

        binding.btnViewAllExpenses.setOnClickListener {
            // Navigate to expenses list
        }

        binding.cardBudget.setOnClickListener {
            // Navigate to budget screen
        }

        binding.cardGoals.setOnClickListener {
            // Navigate to goals screen
        }

        viewModel.refreshData()
    }

    private fun setupToolbar() {
        binding.toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_settings -> {
                    findNavController().navigate(R.id.action_dashboard_to_settings)
                    true
                }
                else -> false
            }
        }
    }

    private fun setupRecyclerView() {
        expenseAdapter = ExpenseAdapter { expense ->
            // Handle expense click
        }
        binding.rvRecentExpenses.layoutManager = LinearLayoutManager(requireContext())
        binding.rvRecentExpenses.adapter = expenseAdapter
    }

    private fun observeViewModel() {
        viewModel.fitnessLevel.observe(viewLifecycleOwner) { level ->
            binding.tvFitnessLevel.text = level
        }

        viewModel.points.observe(viewLifecycleOwner) { points ->
            binding.tvPoints.text = getString(R.string.points_format, points)
        }

        viewModel.totalBalance.observe(viewLifecycleOwner) { balance ->
            binding.tvTotalBalance.text = CurrencyUtils.formatZAR(balance)
        }

        viewModel.safeToSpend.observe(viewLifecycleOwner) { amount ->
            binding.tvSafeToSpend.text = CurrencyUtils.formatZAR(amount)
        }

        viewModel.budgetProgress.observe(viewLifecycleOwner) { progress ->
            binding.progressBudget.progress = progress.percentage
            binding.tvBudgetRemaining.text = getString(
                R.string.budget_remaining_format,
                CurrencyUtils.formatZAR(progress.remaining),
                CurrencyUtils.formatZAR(progress.total)
            )
        }

        viewModel.recentExpenses.observe(viewLifecycleOwner) { expenses ->
            expenseAdapter.submitList(expenses)
            binding.tvNoRecentExpenses.visibility = if (expenses.isEmpty()) View.VISIBLE else View.GONE
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
