package com.example.cse_study_and_learn_application.ui.login

import android.content.Context
import android.util.Log

object AccountAssistant {
    const val PREFS_NAME = "auth"
    const val KEY_TOKEN = "google_token"
    const val KEY_IS_LOGIN = "isLoggedIn"

    fun setUserToken(context: Context, token: String) {
        val preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        preferences.edit().apply {
            putString(KEY_TOKEN, token)
            apply()
        }
    }

    fun getUserToken(context: Context): String {
        val preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val token = preferences.getString(KEY_TOKEN, "") ?: ""
        // Log.d("test", "token: $token")
        return token
    }
}