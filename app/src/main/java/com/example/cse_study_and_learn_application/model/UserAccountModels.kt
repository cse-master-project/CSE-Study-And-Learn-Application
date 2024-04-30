package com.example.cse_study_and_learn_application.model

import com.google.android.gms.common.api.Scope
import com.google.gson.annotations.SerializedName


data class UserRegistrationRequest(
    @SerializedName("accessToken") val accessToken: String,
    @SerializedName("nickname") val nickname: String
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