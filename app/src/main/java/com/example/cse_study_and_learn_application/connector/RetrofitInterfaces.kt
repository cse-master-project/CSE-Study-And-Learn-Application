package com.example.cse_study_and_learn_application.connector

import com.example.cse_study_and_learn_application.model.UserQuizRequest
import com.example.cse_study_and_learn_application.model.UserQuizResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface QuizQueryApi {
    @GET("/api/quiz/user")
    suspend fun getUserQuizzes(
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("sort") sort: List<String>
    ): Response<List<UserQuizResponse>>


    // 다른 API 메서드 정의...
}
