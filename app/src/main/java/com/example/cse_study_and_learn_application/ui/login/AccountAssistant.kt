package com.example.cse_study_and_learn_application.ui.login

import android.content.Context

object AccountAssistant {
    const val PREFS_NAME = "auth"
    private const val KEY_AUTH_CODE = "google_auth_code"
    private const val KEY_ACCESS_TOKEN = "google_access_token"
    const val KEY_IS_LOGIN = "isLoggedIn"

    fun setAuthCode(context: Context, token: String) {
        val preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        preferences.edit().apply {
            putString(KEY_AUTH_CODE, token)
            apply()
        }
    }

    fun getAuthCode(context: Context): String {
        val preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return preferences.getString(KEY_AUTH_CODE, "") ?: ""
    }

    fun setAccessToken(context: Context, token: String) {
        val preferences = context.getSharedPreferences(KEY_ACCESS_TOKEN, Context.MODE_PRIVATE)
        preferences.edit().apply {
            putString(KEY_ACCESS_TOKEN, token)
            apply()
        }
    }


    fun getAccessToken(context: Context): String {
        val preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return preferences.getString(KEY_ACCESS_TOKEN, "") ?: ""
    }

}