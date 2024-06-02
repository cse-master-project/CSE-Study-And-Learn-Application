package com.example.cse_study_and_learn_application.connector

import androidx.annotation.Keep
import com.example.cse_study_and_learn_application.model.AccessTokenResponse
import com.example.cse_study_and_learn_application.model.NicknameRequest
import com.example.cse_study_and_learn_application.model.QuizReport
import com.example.cse_study_and_learn_application.model.QuizReportRequest
import com.example.cse_study_and_learn_application.model.QuizResponse
import com.example.cse_study_and_learn_application.model.QuizSubject
import com.example.cse_study_and_learn_application.model.RandomQuiz
import com.example.cse_study_and_learn_application.model.ServerLoginResponse
import com.example.cse_study_and_learn_application.model.UserInfo
import com.example.cse_study_and_learn_application.model.UserQuizRequest
import com.example.cse_study_and_learn_application.model.UserQuizResponse
import com.example.cse_study_and_learn_application.model.UserQuizStatistics
import com.example.cse_study_and_learn_application.model.UserRegistrationRequest
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Quiz query api
 *
 * @constructor Create empty Quiz query api
 * @author kjy
 * @since 2024-04-28
 */
interface QuizQueryApi {
    @GET("/api/quiz/user")
    suspend fun getUserQuizzes(
        @Header("Authorization") token: String,
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("sort") sort: List<String>
    ): Response<QuizResponse>

    @POST("/api/quiz/submit")
    suspend fun submitQuizResult(
        @Header("Authorization") token: String,
        @Body requestBody: Map<String, String>
    ): Response<Unit>

    @GET("/api/quiz/report")
    suspend fun getReportedQuizzes(
        @Header("Authorization") token: String
    ): Response<List<QuizReport>>

    @GET("/api/quiz/default")
    suspend fun getDefaultQuizzes(
        @Header("Authorization") token: String,
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("sort") sort: List<String>
    ): Response<QuizResponse>

    @GET("/api/quiz")
    suspend fun getAllQuizzes(
        @Header("Authorization") token: String,
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("sort") sort: List<String>
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

    @POST("/api/user/auth/google/login")
    suspend fun getUserLogin(
        @Body requestBody: String
    ): Response<ServerLoginResponse>

    @PUT("/api/user/info/nickname")
    suspend fun setUserNickname(
        @Header("Authorization") token: String,
        @Body nicknameRequest: NicknameRequest
    ): Response<Unit>

    @GET("/api/user/quiz-results")
    suspend fun getUserQuizStatistics(
        @Header("Authorization") token: String
    ): Response<UserQuizStatistics>

    @GET("/api/user/info")
    suspend fun getUserInfo(
        @Header("Authorization") token: String
    ): Response<UserInfo>

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

@Keep
interface GoogleAuthApi {
    @POST("token")
    @FormUrlEncoded
    fun exchangeAuthToken(
        @Field("grant_type") grantType: String,
        @Field("client_id") clientId: String,
        @Field("client_secret") clientSecret: String,
        @Field("code") authCode: String
    ): Call<AccessTokenResponse>
}