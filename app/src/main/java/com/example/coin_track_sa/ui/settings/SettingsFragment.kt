package com.example.coin_track_sa.ui.settings


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.coin_track_sa.databinding.FragmentSettingsBinding
import com.example.coin_track_sa.ui.login.LoginActivity
import com.example.coin_track_sa.utils.SessionManager

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SettingsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadPreferences()
        setupListeners()
        observeViewModel()
    }

    private fun loadPreferences() {
        val prefs = requireContext().getSharedPreferences("settings_prefs", android.content.Context.MODE_PRIVATE)
        val resetDay = prefs.getInt("reset_day", 1)
        binding.npResetDay.value = resetDay

        val language = prefs.getString("language", "en")
        if (language == "zu") {
            binding.rbIsiZulu.isChecked = true
        } else {
            binding.rbEnglish.isChecked = true
        }

        val cloudSync = prefs.getBoolean("cloud_sync", false)
        binding.swCloudSync.isChecked = cloudSync
    }

    private fun setupListeners() {
        binding.rgLanguage.setOnCheckedChangeListener { _, checkedId ->
            val lang = if (checkedId == R.id.rbEnglish) "en" else "zu"
            requireContext().getSharedPreferences("settings_prefs", android.content.Context.MODE_PRIVATE)
                .edit().putString("language", lang).apply()
            // Language change would require app restart or recreate
        }

        binding.npResetDay.setOnValueChangedListener { _, _, newVal ->
            requireContext().getSharedPreferences("settings_prefs", android.content.Context.MODE_PRIVATE)
                .edit().putInt("reset_day", newVal).apply()
            viewModel.updateBudgetResetDay(newVal)
        }

        binding.swCloudSync.setOnCheckedChangeListener { _, isChecked ->
            requireContext().getSharedPreferences("settings_prefs", android.content.Context.MODE_PRIVATE)
                .edit().putBoolean("cloud_sync", isChecked).apply()
            if (isChecked) {
                Toast.makeText(requireContext(), "Cloud sync enabled", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnExportData.setOnClickListener {
            viewModel.exportData()
        }

        binding.btnLogout.setOnClickListener {
            SessionManager.logout(requireContext())
            startActivity(Intent(requireContext(), LoginActivity::class.java))
            requireActivity().finish()
        }
    }

    private fun observeViewModel() {
        viewModel.exportStatus.observe(viewLifecycleOwner) { message ->
            message?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                viewModel.clearExportStatus()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}