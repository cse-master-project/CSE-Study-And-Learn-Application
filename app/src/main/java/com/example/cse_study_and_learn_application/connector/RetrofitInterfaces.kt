package com.example.cse_study_and_learn_application.connector

import com.example.cse_study_and_learn_application.model.DefaultQuizResponse
import com.example.cse_study_and_learn_application.model.QuizReport
import com.example.cse_study_and_learn_application.model.QuizReportRequest
import com.example.cse_study_and_learn_application.model.QuizResponse
import com.example.cse_study_and_learn_application.model.QuizSubject
import com.example.cse_study_and_learn_application.model.RandomQuiz
import com.example.cse_study_and_learn_application.model.UserQuizRequest
import com.example.cse_study_and_learn_application.model.UserQuizResponse
import com.example.cse_study_and_learn_application.model.UserQuizStatistics
import com.example.cse_study_and_learn_application.model.UserRegistrationRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface QuizQueryApi {
    @GET("/api/quiz/user")
    suspend fun getUserQuizzes(
        @Header("Authorization") token: String,
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("sort") sort: List<String>
    ): Response<List<UserQuizResponse>>

    @POST("/api/quiz/submit")
    suspend fun submitQuizResult(
        @Header("Authorization") token: String,
        @Body requestBody: Map<String, Any>
    ): Response<Unit>

    @GET("/api/quiz/report")
    suspend fun getReportedQuizzes(
        @Header("Authorization") token: String
    ): Response<List<QuizReport>>

    @GET("/api/quiz/default")
    suspend fun getDefaultQuizzes(
        @Header("Authorization") token: String,
        @Query("pageable") pageable: Map<String, Any>
    ): Response<DefaultQuizResponse>

    @GET("/api/quiz")
    suspend fun getAllQuizzes(
        @Header("Authorization") token: String,
        @Query("pageable") pageable: Map<String, Any>
    ): Response<QuizResponse>

    @GET("/api/quiz/{quizId}/image")
    suspend fun getQuizImage(
        @Header("Authorization") token: String,
        @Path("quizId") quizId: Int
    ): Response<Unit>

    @GET("/api/quiz/random")
    suspend fun getRandomQuiz(
        @Header("Authorization") token: String,
        @Query("subject") subject: String,
        @Query("detailSubject") detailSubject: String
    ): Response<RandomQuiz>

    @POST("/api/quiz/report")
    suspend fun reportQuiz(
        @Header("Authorization") token: String,
        @Body requestBody: QuizReportRequest
    ): Response<Unit>

    @GET("/api/quiz/subject")
    suspend fun getQuizSubjects(
        @Header("Authorization") token: String
    ): Response<List<QuizSubject>>
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

    @PUT("/api/user/info/nickname")
    suspend fun setUserNickname(
        @Header("Authorization") token: String,
        @Body nickname: String
    ): Response<Unit>

    @GET("/api/user/quiz-results")
    suspend fun getUserQuizStatistics(
        @Header("Authorization") token: String
    ): Response<UserQuizStatistics>

    @GET("/api/user/info")
    suspend fun getUserInfo(
        @Header("Authorization") token: String
    ): Response<Unit>

    @POST("/api/user/deactivate")
    suspend fun deactivateUser(
        @Header("Authorization") token: String
    ): Response<Unit>

    @POST("/api/user/auth/refresh")
    suspend fun refreshUserToken(
        @Header("Authorization") token: String
    ): Response<Unit>

    @POST("/api/user/auth/google/logout")
    suspend fun logoutUser(
        @Header("Authorization") token: String
    ): Response<Unit>
}