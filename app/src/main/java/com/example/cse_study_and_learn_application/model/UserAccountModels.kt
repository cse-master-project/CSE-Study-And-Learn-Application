package com.example.cse_study_and_learn_application.model

import com.google.gson.annotations.SerializedName


data class UserRegistrationRequest(
    @SerializedName("accessToken") val accessToken: String,
    @SerializedName("nickname") val nickname: String
)


data class TokenResponse(
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("expires_in") val expiresIn: Int,
    @SerializedName("refresh_token") val refreshToken: String?,
    @SerializedName("scope") val scope: String,
    @SerializedName("token_type") val tokenType: String
)

data class UserQuizStatistics(
    @SerializedName("totalCorrectRate")
    val totalCorrectRate: Int,
    @SerializedName("correctRateBySubject")
    val correctRateBySubject: Map<String, Int>
)