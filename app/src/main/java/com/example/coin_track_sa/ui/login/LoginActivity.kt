package com.example.coin_track_sa.ui.login

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.coin_track_sa.databinding.ActivityLoginBinding
import com.example.coin_track_sa.ui.main.MainActivity
import com.example.coin_track_sa.utils.SessionManager
import android.content.Intent

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (SessionManager.isLoggedIn(this)) {
            startMainActivity()
            finish()
            return
        }

        binding.btnLogin.setOnClickListener {
            attemptLogin()
        }

        binding.btnRegister.setOnClickListener {
            attemptRegister()
        }

        binding.tvForgotPassword.setOnClickListener {
            Toast.makeText(this, "Password recovery not implemented", Toast.LENGTH_SHORT).show()
        }

        observeViewModel()
    }

    private fun attemptLogin() {
        val username = binding.etUsername.text.toString().trim()
        val password = binding.etPassword.text.toString()

        if (validateInput(username, password)) {
            viewModel.login(username, password)
        }
    }

    private fun attemptRegister() {
        val username = binding.etUsername.text.toString().trim()
        val password = binding.etPassword.text.toString()

        if (validateInput(username, password)) {
            viewModel.register(username, password)
        }
    }

    private fun validateInput(username: String, password: String): Boolean {
        var isValid = true
        if (username.length < 4) {
            binding.tilUsername.error = "Username must be at least 4 characters"
            isValid = false
        } else {
            binding.tilUsername.error = null
        }

        if (password.length < 8 || !password.any { it.isDigit() }) {
            binding.tilPassword.error = "Password must be 8+ chars with a number"
            isValid = false
        } else {
            binding.tilPassword.error = null
        }
        return isValid
    }

    private fun observeViewModel() {
        viewModel.loginResult.observe(this) { user ->
            if (user != null) {
                SessionManager.saveLogin(this, user.username, user.id)
                startMainActivity()
            } else {
                Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.registerResult.observe(this) { success ->
            if (success) {
                Toast.makeText(this, "Registration successful! Please login.", Toast.LENGTH_SHORT).show()
                binding.etPassword.text?.clear()
            } else {
                Toast.makeText(this, "Username already taken", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.isLoading.observe(this) { isLoading ->
            binding.btnLogin.isEnabled = !isLoading
            binding.btnRegister.isEnabled = !isLoading
        }
    }

    private fun startMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
