package com.example.coin_track_sa.ui.login

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.coin_track_sa.data.AppDatabase
import com.example.coin_track_sa.data.entities.User
import kotlinx.coroutines.launch
import org.mindrot.jbcrypt.BCrypt

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val userDao = AppDatabase.getInstance(application).userDao()

    private val _loginResult = MutableLiveData<User?>()
    val loginResult: LiveData<User?> = _loginResult

    private val _registerResult = MutableLiveData<Boolean>()
    val registerResult: LiveData<Boolean> = _registerResult

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    fun login(username: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val user = userDao.getUserByUsername(username)
            if (user != null && BCrypt.checkpw(password, user.passwordHash)) {
                _loginResult.postValue(user)
            } else {
                _loginResult.postValue(null)
            }
            _isLoading.postValue(false)
        }
    }

    fun register(username: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val existing = userDao.getUserByUsername(username)
            if (existing != null) {
                _registerResult.postValue(false)
            } else {
                val hashed = BCrypt.hashpw(password, BCrypt.gensalt())
                val user = User(username = username, passwordHash = hashed)
                userDao.insertUser(user)
                _registerResult.postValue(true)
            }
            _isLoading.postValue(false)
        }
    }
}
