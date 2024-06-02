package com.example.cse_study_and_learn_application.ui.login

import android.content.Context
import android.util.Log

object AccountAssistant {
    private const val PREFS_NAME = "auth"
    private const val KEY_SERVER_ACCESS_TOKEN = "server_access_token"
    private const val KEY_ACCESS_TOKEN = "google_access_token"
    private const val KEY_USER_EMAIL = "user_email"

    fun setServerAccessToken(context: Context, token: String) {
        val preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        preferences.edit().apply {
            putString(KEY_SERVER_ACCESS_TOKEN, token)
            apply()
        }
        // Log.d("test", "KEY_SERVER_ACCESS_TOKEN 저장함: $token")
    }

    fun getServerAccessToken(context: Context): String {
        val preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val token = preferences.getString(KEY_SERVER_ACCESS_TOKEN, "") ?: ""
        // Log.d("test", "KEY_SERVER_ACCESS_TOKEN 불러옴: $token")
        return "Bearer " + token
    }

    fun setAccessToken(context: Context, token: String) {
        val preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        preferences.edit().apply {
            putString(KEY_ACCESS_TOKEN, token)
            apply()
        }
        // Log.d("test", "token 저장함: $token")
    }

    fun getAccessToken(context: Context): String {
        val preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val token = preferences.getString(KEY_ACCESS_TOKEN, "") ?: ""
        // Log.d("test", "token 불러옴: $token")
        return token
    }

    fun setUserEmail(context: Context, email: String) {
        val preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        preferences.edit().apply {
            putString(KEY_USER_EMAIL, email)
            apply()
        }
        // Log.d("test", "email 저장함: $email")
    }

    fun getUserEmail(context: Context): String {
        val preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val email = preferences.getString(KEY_USER_EMAIL, "") ?: ""
        // Log.d("test", "email 불러옴: $email")
        return email
    }
}
