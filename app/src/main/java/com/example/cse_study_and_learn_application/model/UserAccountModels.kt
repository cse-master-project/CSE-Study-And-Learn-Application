package com.example.cse_study_and_learn_application.model

import com.google.android.gms.common.api.Scope
import com.google.gson.annotations.SerializedName


data class UserRegistrationRequest(
    @SerializedName("accessToken") val accessToken: String,
    @SerializedName("nickname") val nickname: String
)

data class ServerLoginResponse(
    @SerializedName("grantType") val grantType: String,
    @SerializedName("accessToken") val accessToken: String,
    @SerializedName("refreshToken") val refreshToken: String,
    @SerializedName("refreshIseAt") val refreshIseAt: String,
    @SerializedName("refreshExpAt") val refreshExpAt: String
)

data class AccessTokenResponse(
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("expires_in") val expiresIn: Long,
    @SerializedName("refresh_token") val refreshToken: String,
    @SerializedName("scope") val scope: String,
    @SerializedName("token_type") val tokenType: String,
    @SerializedName("id_token") val idToken: String
) {
    constructor() : this("a", 0, "r", "s", "t", "i")
}



data class TokenResponse(
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("expires_in") val expiresIn: Int,
    @SerializedName("refresh_token") val refreshToken: String?,
    @SerializedName("scope") val scope: String,
    @SerializedName("token_type") val tokenType: String
)

data class UserQuizStatistics(
    @SerializedName("totalCorrectRate")
    val totalCorrectRate: String,
    @SerializedName("correctRateBySubject")
    val correctRateBySubject: Map<String, String>
)

data class NicknameRequest(
    val nickname: String
)

data class UserInfo(
    @SerializedName("nickname") val nickname: String,
    @SerializedName("createAt") val createAt: String
)

data class isSignedResponse(
    @SerializedName("registered") val registered: Boolean
)

data class isSignedRequest(
    @SerializedName("accessToken") val accessToken: String
)