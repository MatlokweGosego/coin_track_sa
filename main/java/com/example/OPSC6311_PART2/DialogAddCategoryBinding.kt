package com.example.OPSC6311_PART2

import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView

class DialogAddCategoryBinding private constructor(val root: View) {
    val tvDialogTitle: TextView = root.findViewById(R.id.tvDialogTitle)
    val etCategoryName: EditText = root.findViewById(R.id.etCategoryName)
    val etBudget: EditText = root.findViewById(R.id.etBudget)
    val etMinGoal: EditText = root.findViewById(R.id.etMinGoal)  // Add this
    val etMaxGoal: EditText = root.findViewById(R.id.etMaxGoal)  // Add this

    // Color views
    val colorCoral: TextView = root.findViewById(R.id.colorCoral)
    val colorTeal: TextView = root.findViewById(R.id.colorTeal)
    val colorSunset: TextView = root.findViewById(R.id.colorSunset)
    val colorLavender: TextView = root.findViewById(R.id.colorLavender)
    val colorBerry: TextView = root.findViewById(R.id.colorBerry)
    val colorMint: TextView = root.findViewById(R.id.colorMint)
    val colorPeach: TextView = root.findViewById(R.id.colorPeach)
    val colorBlueGray: TextView = root.findViewById(R.id.colorBlueGray)

    val btnCancel: Button = root.findViewById(R.id.btnCancel)
    val btnSave: Button = root.findViewById(R.id.btnSave)

    companion object {
        fun inflate(layoutInflater: LayoutInflater): DialogAddCategoryBinding {
            val root = layoutInflater.inflate(R.layout.dialog_add_category, null)
            return DialogAddCategoryBinding(root)
        }
    }
}