package com.example.coin_track_sa.utils
import android.content.Context
import android.content.SharedPreferences

object SessionManager {
    private const val PREF_NAME = "session_prefs"
    private const val KEY_LOGGED_IN = "logged_in"
    private const val KEY_USERNAME = "username"
    private const val KEY_USER_ID = "user_id"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun saveLogin(context: Context, username: String, userId: Long) {
        getPrefs(context).edit()
            .putBoolean(KEY_LOGGED_IN, true)
            .putString(KEY_USERNAME, username)
            .putLong(KEY_USER_ID, userId)
            .apply()
    }

    fun isLoggedIn(context: Context): Boolean {
        return getPrefs(context).getBoolean(KEY_LOGGED_IN, false)
    }

    fun logout(context: Context) {
        getPrefs(context).edit().clear().apply()
    }

    fun getUsername(context: Context): String? {
        return getPrefs(context).getString(KEY_USERNAME, null)
    }

    fun getUserId(context: Context): Long {
        return getPrefs(context).getLong(KEY_USER_ID, -1)
    }
}