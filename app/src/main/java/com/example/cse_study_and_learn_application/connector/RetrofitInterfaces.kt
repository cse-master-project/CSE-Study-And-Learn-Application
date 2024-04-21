package com.example.cse_study_and_learn_application.connector

import com.example.cse_study_and_learn_application.model.UserQuizRequest
import com.example.cse_study_and_learn_application.model.UserQuizResponse
import com.example.cse_study_and_learn_application.model.UserRegistrationRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface QuizQueryApi {
    @GET("/api/quiz/user")
    suspend fun getUserQuizzes(
        @Header("Authorization") token: String,
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("sort") sort: List<String>
    ): Response<List<UserQuizResponse>>


    // 다른 API 메서드 정의...
}


interface UserAccountApi {
    @POST("/api/user/auth/google/sign-up")
    suspend fun getRegisterUserAccount(
        @Body requestBody: UserRegistrationRequest
    ): Response<Unit>

    @POST("/api/user/auth/google/sign-up")
    suspend fun getUserLogin(
        @Body requestBody: String
    ): Response<Unit>
}