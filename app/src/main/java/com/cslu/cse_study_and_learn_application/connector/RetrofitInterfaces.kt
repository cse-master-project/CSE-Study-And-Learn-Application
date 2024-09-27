package com.cslu.cse_study_and_learn_application.connector

import androidx.annotation.Keep
import com.cslu.cse_study_and_learn_application.model.AccessTokenResponse
import com.cslu.cse_study_and_learn_application.model.NicknameRequest
import com.cslu.cse_study_and_learn_application.model.QuizReportRequest
import com.cslu.cse_study_and_learn_application.model.QuizSubject
import com.cslu.cse_study_and_learn_application.model.RandomQuiz
import com.cslu.cse_study_and_learn_application.model.ServerLoginResponse
import com.cslu.cse_study_and_learn_application.model.UserInfo
// import com.example.cse_study_and_learn_application.model.UserQuizResponse
import com.cslu.cse_study_and_learn_application.model.UserQuizStatistics
import com.cslu.cse_study_and_learn_application.model.UserRegistrationRequest
import com.cslu.cse_study_and_learn_application.model.isSignedRequest
import com.cslu.cse_study_and_learn_application.model.isSignedResponse
import okhttp3.ResponseBody
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
    @POST("/api/v2/quiz/submit")
    suspend fun submitQuizResult(
        @Header("Authorization") token: String,
        @Body requestBody: Map<String, String>
    ): Response<Unit>


    @GET("/api/v2/quiz/{quizId}/image")
    suspend fun getQuizImage(
        @Header("Authorization") token: String,
        @Path("quizId") quizId: Int
    ): ResponseBody

    @GET("/api/v2/quiz/random")
    suspend fun getRandomQuiz(
        @Header("Authorization") token: String,
        @Query("subject") subject: String,
        @Query("chapters") chapters: List<String>,
        @Query("hasUserQuiz") hasUserQuiz: Boolean,
        @Query("hasDefaultQuiz") hasDefaultQuiz: Boolean,
        @Query("hasSolvedQuiz") hasSolvedQuiz: Boolean
    ): Response<RandomQuiz>

    @POST("/api/v2/quiz/report")
    suspend fun reportQuiz(
        @Header("Authorization") token: String,
        @Body requestBody: QuizReportRequest
    ): Response<Unit>

    @GET("/api/v2/quiz/subject")
    suspend fun getQuizSubjects(
        @Header("Authorization") token: String,
        @Query("onlySubject") onlySubject: Boolean
    ): Response<List<QuizSubject>>

    @GET("api/v2/quiz/random/only-subject")
    suspend fun getRandomQuizOnlySubject(
        @Header("Authorization") token: String,
        @Query("subject") subjects: List<String>,
        @Query("hasUserQuiz") hasUserQuiz: Boolean,
        @Query("hasDefaultQuiz") hasDefaultQuiz: Boolean,
        @Query("hasSolvedQuiz") hasSolvedQuiz: Boolean
    ): Response<RandomQuiz>
}


interface UserAccountApi {
    @POST("/api/v2/user/auth/google/sign-up")
    suspend fun getRegisterUserAccount(
        @Body requestBody: UserRegistrationRequest
    ): Response<ServerLoginResponse>

    @POST("/api/v2/user/auth/google/login")
    suspend fun getUserLogin(
        @Body requestBody: String
    ): Response<ServerLoginResponse>

    @PUT("/api/v2/user/info/nickname")
    suspend fun setUserNickname(
        @Header("Authorization") token: String,
        @Body nicknameRequest: NicknameRequest
    ): Response<Unit>

    @GET("/api/v2/user/quiz-results")
    suspend fun getUserQuizStatistics(
        @Header("Authorization") token: String
    ): Response<UserQuizStatistics>

    @GET("/api/v2/user/info")
    suspend fun getUserInfo(
        @Header("Authorization") token: String
    ): Response<UserInfo>

    @POST("/api/v2/user/deactivate")
    suspend fun deactivateUser(
        @Header("Authorization") token: String
    ): Response<Unit>

    @POST("/api/v2/user/auth/refresh")
    suspend fun refreshUserToken(
        @Header("Authorization") token: String
    ): Response<Unit>

    @POST("/api/v2/user/auth/google/logout")
    suspend fun logoutUser(
        @Header("Authorization") token: String
    ): Response<Unit>

    @POST("/api/v2/user/auth/google/check")
    suspend fun isSigned(
        @Body request: isSignedRequest
    ): Response<isSignedResponse>
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