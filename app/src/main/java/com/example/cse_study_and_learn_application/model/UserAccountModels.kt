package com.example.cse_study_and_learn_application.model

import com.google.gson.annotations.SerializedName


data class UserRegistrationRequest(
    @SerializedName("accessToken") val accessToken: String,
    @SerializedName("nickname") val nickname: String
)

data class AccessTokenResponse(
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("expires_int") val expiresIn: Long,
    @SerializedName("token_type") val tokenType: String
)