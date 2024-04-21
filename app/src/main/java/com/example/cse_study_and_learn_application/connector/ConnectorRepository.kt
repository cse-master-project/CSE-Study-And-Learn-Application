package com.example.cse_study_and_learn_application.connector

import com.example.cse_study_and_learn_application.model.QuizCategory
import com.example.cse_study_and_learn_application.model.UserQuizRequest
import com.example.cse_study_and_learn_application.model.UserQuizResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ConnectorRepository {
    suspend fun getUserQuizzes(userQuizRequest: UserQuizRequest): List<UserQuizResponse> {
        val response = RetrofitInstance.quizQueryApi.getUserQuizzes(
            userQuizRequest.page,
            userQuizRequest.size,
            userQuizRequest.sort)
        if (response.isSuccessful) {
            return response.body() ?: emptyList()
        } else {
            throw Exception("Failed to get user quizzes")
        }
    }
}