package com.example.cse_study_and_learn_application.ui.login

import android.content.Context
import com.example.cse_study_and_learn_application.model.TokenResponse
import com.google.gson.Gson
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

object AccountAssistant {
    const val PREFS_NAME = "auth"
    const val KEY_TOKEN = "google_token"
    const val KEY_AUTH = "google_auth_code"
    const val KEY_IS_LOGIN = "isLoggedIn"

    fun setAuthCode(context: Context, token: String) {
        val preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        preferences.edit().apply {
            putString(KEY_AUTH, token)
            apply()
        }
    }

    fun getAuthCode(context: Context): String {
        val preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val code = preferences.getString(KEY_AUTH, "") ?: ""
        // Log.d("test", "code: $token")
        return code
    }


    /**
     * Exchange auth code for tokens
     *
     * @param authCode Google Sign-In에서 받은 인증 코드
     * @param clientId Google API Console에서 생성한 클라이언트 ID
     * @param clientSecret Google API Console에서 생성한 클라이언트 시크릿
     * @param redirectUri Google API Console에 등록한 리디렉션 URI
     * @return
     */
    fun exchangeAuthCodeForTokens(authCode: String, clientId: String, clientSecret: String, redirectUri: String): TokenResponse? {
        val client = OkHttpClient()
        val gson = Gson()

        val formBody = FormBody.Builder()
            .add("code", authCode)
            .add("client_id", clientId)
            .add("client_secret", clientSecret)
            .add("redirect_uri", redirectUri)
            .add("grant_type", "authorization_code")
            .build()

        val request = Request.Builder()
            .url("https://oauth2.googleapis.com/token")
            .post(formBody)
            .build()

        try {
            val response = client.newCall(request).execute()
            val responseBody = response.body().toString()
            return gson.fromJson(responseBody, TokenResponse::class.java)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return null
    }
}