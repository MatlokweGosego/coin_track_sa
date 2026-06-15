package com.example.OPSC6311_PART2

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.OPSC6311_PART2.databinding.FragmentCategoriesBinding

class CategoriesFragment : Fragment() {

    private var _binding: FragmentCategoriesBinding? = null
    private val binding get() = _binding!!

    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var sessionManager: SessionManager
    private lateinit var categoryAdapter: CategoryAdapter

    private var selectedColor = "#B22222" // Default color

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoriesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        databaseHelper = DatabaseHelper(requireContext())
        sessionManager = SessionManager(requireContext())

        setupUI()
        loadCategories()
    }

    private fun setupUI() {
        binding.rvCategories.layoutManager = LinearLayoutManager(requireContext())
        categoryAdapter = CategoryAdapter(
            emptyList(),
            onEditClick = { category -> showAddEditCategoryDialog(category) },
            onDeleteClick = { category -> deleteCategory(category) }
        )
        binding.rvCategories.adapter = categoryAdapter

        binding.fabAddCategory.setOnClickListener {
            showAddEditCategoryDialog(null)
        }
    }

    private fun loadCategories() {
        val user = sessionManager.getUserDetails() ?: return
        val categories = databaseHelper.getAllCategories(user.id)

        if (categories.isEmpty()) {
            binding.tvNoCategories.visibility = View.VISIBLE
            binding.rvCategories.visibility = View.GONE
        } else {
            binding.tvNoCategories.visibility = View.GONE
            binding.rvCategories.visibility = View.VISIBLE
            categoryAdapter.updateCategories(categories)
        }
    }

    private fun showAddEditCategoryDialog(category: Category?) {
        val dialogBinding = DialogAddCategoryBinding.inflate(layoutInflater)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
            .create()

        // Set dialog title based on whether we're adding or editing
        dialogBinding.tvDialogTitle.text = if (category == null) "Add New Category" else "Edit Category"

        // Pre-fill fields if editing
        if (category != null) {
            dialogBinding.etCategoryName.setText(category.name)
            dialogBinding.etBudget.setText(category.budget.toString())
            dialogBinding.etMinGoal.setText(category.minGoal.toString())
            dialogBinding.etMaxGoal.setText(category.maxGoal.toString())
            selectedColor = category.color
        }

        // Setup color selection
        val colorViews = listOf(
            dialogBinding.colorCoral to "#FF6B6B",
            dialogBinding.colorTeal to "#4ECDC4",
            dialogBinding.colorSunset to "#FFE66D",
            dialogBinding.colorLavender to "#A8E6CF",
            dialogBinding.colorBerry to "#FF8C94",
            dialogBinding.colorMint to "#B5EAD7",
            dialogBinding.colorPeach to "#FFD3B6",
            dialogBinding.colorBlueGray to "#667EEA"
        )

        // Mark the selected color
        updateSelectedColor(colorViews, selectedColor)

        // Set click listeners for color selection
        colorViews.forEach { (view, color) ->
            view.setOnClickListener {
                selectedColor = color
                updateSelectedColor(colorViews, selectedColor)
            }
        }

        dialogBinding.btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialogBinding.btnSave.setOnClickListener {
            val name = dialogBinding.etCategoryName.text.toString().trim()
            val budgetStr = dialogBinding.etBudget.text.toString().trim()
            val minGoalStr = dialogBinding.etMinGoal.text.toString().trim()
            val maxGoalStr = dialogBinding.etMaxGoal.text.toString().trim()

            if (name.isEmpty() || budgetStr.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill all required fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            try {
                val budget = budgetStr.toDouble()
                val minGoal = if (minGoalStr.isNotEmpty()) minGoalStr.toDouble() else 0.0
                val maxGoal = if (maxGoalStr.isNotEmpty()) maxGoalStr.toDouble() else 0.0
                val user = sessionManager.getUserDetails() ?: return@setOnClickListener

                // Validate goals
                if (minGoal > 0 && maxGoal > 0 && minGoal > maxGoal) {
                    Toast.makeText(requireContext(), "Minimum goal cannot be greater than maximum goal", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                if (category == null) {
                    // Add new category
                    val newCategory = Category(
                        name = name,
                        color = selectedColor,
                        budget = budget,
                        minGoal = minGoal,
                        maxGoal = maxGoal,
                        userId = user.id
                    )

                    val id = databaseHelper.addCategory(newCategory)
                    if (id > 0) {
                        Toast.makeText(requireContext(), "Category added successfully", Toast.LENGTH_SHORT).show()
                        loadCategories()
                        dialog.dismiss()
                    } else {
                        Toast.makeText(requireContext(), "Failed to add category", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // Update existing category
                    val updatedCategory = Category(
                        id = category.id,
                        name = name,
                        color = selectedColor,
                        budget = budget,
                        minGoal = minGoal,
                        maxGoal = maxGoal,
                        userId = user.id
                    )

                    val result = databaseHelper.updateCategory(updatedCategory)
                    if (result > 0) {
                        Toast.makeText(requireContext(), "Category updated successfully", Toast.LENGTH_SHORT).show()
                        loadCategories()
                        dialog.dismiss()
                    } else {
                        Toast.makeText(requireContext(), "Failed to update category", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Invalid input: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.show()
    }

    private fun updateSelectedColor(colorViews: List<Pair<TextView, String>>, selectedColor: String) {
        colorViews.forEach { (view, color) ->
            view.text = if (color == selectedColor) "✓" else ""
        }
    }

    private fun deleteCategory(category: Category) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Category")
            .setMessage("Are you sure you want to delete this category? All expenses in this category will be affected.")
            .setPositiveButton("Delete") { _, _ ->
                val result = databaseHelper.deleteCategory(category.id)
                if (result > 0) {
                    Toast.makeText(requireContext(), "Category deleted successfully", Toast.LENGTH_SHORT).show()
                    loadCategories()
                } else {
                    Toast.makeText(requireContext(), "Failed to delete category", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}