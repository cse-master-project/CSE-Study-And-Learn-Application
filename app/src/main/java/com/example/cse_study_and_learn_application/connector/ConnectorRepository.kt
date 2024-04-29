package com.example.cse_study_and_learn_application.connector

import android.util.Log
import com.example.cse_study_and_learn_application.model.AccessTokenResponse
import com.example.cse_study_and_learn_application.model.UserQuizRequest
import com.example.cse_study_and_learn_application.model.UserQuizResponse
import com.example.cse_study_and_learn_application.model.UserRegistrationRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ConnectorRepository {
    suspend fun getUserQuizzes(token: String, userQuizRequest: UserQuizRequest): List<UserQuizResponse> {
        val response = RetrofitInstance.quizQueryApi.getUserQuizzes(
            token,
            userQuizRequest.page,
            userQuizRequest.size,
            userQuizRequest.sort)
        if (response.isSuccessful) {
            return response.body() ?: emptyList()
        } else {
            val errorBody = response.errorBody()?.string()
            throw Exception("Failed to get user quizzes: ${response.message()}\n$errorBody")
        }
    }


    suspend fun getUserRegistration(token: String, nickname: String): Boolean {
        val requestBody = UserRegistrationRequest(token, nickname)
        val response = RetrofitInstance.userAccountQueryApi.getRegisterUserAccount(requestBody)
        if (response.isSuccessful) {
            return true
        } else {
            val errorBody = response.errorBody()?.string()
            throw Exception("Failed to register user: ${response.message()}\n$errorBody")
        }
    }

    suspend fun getUserLogin(token: String): Boolean {
        val response = RetrofitInstance.userAccountQueryApi.getUserLogin(token)
        if (response.isSuccessful) {
            Log.d("test", "user login 성공!")
            return true
        } else {
            val errorBody = response.errorBody()?.string()
            throw Exception("Failed to user login: ${response.message()}\n$errorBody")
        }
    }

    suspend fun getAccessToken(grantType: String, clientId: String, clientSecret: String, authCode: String?): String? {

        if (authCode.isNullOrBlank()) {
            throw NullPointerException("server auth code is null or blank")
        }

        var accessToken: String? = null

        val retrofit = Retrofit.Builder()
            .baseUrl("https://auth2.googleapis.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val googleAuthApi = retrofit.create(GoogleAuthApi::class.java)

        val response = googleAuthApi.exchangeAuthToken(grantType, clientId, clientSecret, authCode)
        response.enqueue(object : Callback<AccessTokenResponse> {
            override fun onResponse(
                call: Call<AccessTokenResponse>,
                response: Response<AccessTokenResponse>
            ) {
                if(response.isSuccessful) {
                    accessToken = response.body()?.accessToken
                    Log.d("test", "get access token 성공!")
                } else {
                    val errorBody = response.errorBody()?.string()
                    throw  Exception("Failed get access token: ${response.message()}\n$errorBody")
                }
            }

            override fun onFailure(call: Call<AccessTokenResponse>, t: Throwable) {
                throw Exception("Failed get access token: failed call back")
            }
        })

        return accessToken
    }
}