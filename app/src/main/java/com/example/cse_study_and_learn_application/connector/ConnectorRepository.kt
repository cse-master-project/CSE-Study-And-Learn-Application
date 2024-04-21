package com.example.cse_study_and_learn_application.connector

import android.content.Context
import android.util.Log
import com.example.cse_study_and_learn_application.model.QuizCategory
import com.example.cse_study_and_learn_application.model.UserQuizRequest
import com.example.cse_study_and_learn_application.model.UserQuizResponse
import com.example.cse_study_and_learn_application.model.UserRegistrationRequest
import com.example.cse_study_and_learn_application.ui.login.AccountAssistant
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ConnectorRepository {
    suspend fun getUserQuizzes(token: String, userQuizRequest: UserQuizRequest): List<UserQuizResponse> {
        val response = RetrofitInstance.quizQueryApi.getUserQuizzes(
            token,
            userQuizRequest.page,
            userQuizRequest.size,
            userQuizRequest.sort)
        if (response.isSuccessful) {
            return response.body() ?: emptyList()
        } else {
            val errorBody = response.errorBody()?.string()
            throw Exception("Failed to get user quizzes: ${response.message()}\n$errorBody")
        }
    }


    suspend fun getUserRegistration(token: String, nickname: String): Boolean {
        val requestBody = UserRegistrationRequest(token, nickname)
        val response = RetrofitInstance.userAccountQueryApi.getRegisterUserAccount(requestBody)
        if (response.isSuccessful) {
            return true
        } else {
            val errorBody = response.errorBody()?.string()
            throw Exception("Failed to register user: ${response.message()}\n$errorBody")
        }
    }

    suspend fun getUserLogin(token: String): Boolean {
        val response = RetrofitInstance.userAccountQueryApi.getUserLogin(token)
        if (response.isSuccessful) {
            Log.d("test", "user login 성공!")
            return true
        } else {
            val errorBody = response.errorBody()?.string()
            throw Exception("Failed to user login: ${response.message()}\n$errorBody")
        }
    }
}