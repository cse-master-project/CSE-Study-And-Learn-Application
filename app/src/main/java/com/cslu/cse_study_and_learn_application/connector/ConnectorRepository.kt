package com.cslu.cse_study_and_learn_application.connector

import android.util.Log
import com.cslu.cse_study_and_learn_application.model.AccessTokenResponse
import com.cslu.cse_study_and_learn_application.model.NicknameRequest
//import com.example.cse_study_and_learn_application.model.QuizReport
import com.cslu.cse_study_and_learn_application.model.QuizReportRequest
//import com.example.cse_study_and_learn_application.model.QuizResponse
import com.cslu.cse_study_and_learn_application.model.QuizSubject
import com.cslu.cse_study_and_learn_application.model.RandomQuiz
import com.cslu.cse_study_and_learn_application.model.UserInfo
//import com.example.cse_study_and_learn_application.model.UserQuizRequest
import com.cslu.cse_study_and_learn_application.model.UserQuizStatistics
import com.cslu.cse_study_and_learn_application.model.UserRegistrationRequest
import com.cslu.cse_study_and_learn_application.model.isSignedRequest
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ConnectorRepository {

    suspend fun getUserRegistration(token: String, nickname: String): String {
        val requestBody = UserRegistrationRequest(token, nickname)
        val response = RetrofitInstance.userAccountQueryApi.getRegisterUserAccount(requestBody)
        if (response.isSuccessful) {
            val serverResponse = response.body()
            if (serverResponse != null) {
                return serverResponse.accessToken
            } else {
                throw NullPointerException("getUserRegistration")
            }

        } else {
            val errorBody = response.errorBody()?.string()
            throw Exception("Failed to register user: ${response.message()}\n$errorBody")
        }
    }

    suspend fun getUserLogin(token: String): String {
        val response = RetrofitInstance.userAccountQueryApi.getUserLogin(token)
        if (response.isSuccessful) {
            val serverLoginResponse = response.body()
            if (serverLoginResponse != null) {
                // Log.d("test", "user login 성공!")
                return serverLoginResponse.accessToken
            } else {
                throw Exception("Empty response body")
            }
        } else {
            val errorBody = response.errorBody()?.string()
            throw Exception("Failed to user login: ${response.message()}\n$errorBody")
        }
    }

    fun getAccessToken(
        grantType: String,
        clientId: String,
        clientSecret: String,
        authCode: String?,
        callback: (String?, Exception?) -> Unit
    ) {

        if (authCode.isNullOrBlank()) {
            callback(null, NullPointerException("server auth code is null or blank"))
            return
        }

        val retrofit = Retrofit.Builder()
            .baseUrl("https://accounts.google.com/o/oauth2/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val googleAuthApi = retrofit.create(GoogleAuthApi::class.java)

        googleAuthApi.exchangeAuthToken(grantType, clientId, clientSecret, authCode)
            .enqueue(object : Callback<AccessTokenResponse> {
                override fun onResponse(
                    call: Call<AccessTokenResponse>,
                    response: Response<AccessTokenResponse>
                ) {
                    if(response.isSuccessful) {
                        val accessToken = response.body()?.accessToken
                        callback(accessToken, null)
                    } else {
                        val errorBody = response.errorBody()?.string()
                        callback(null, Exception("Failed get access token: ${response.message()}\n$errorBody"))
                    }
                }

                override fun onFailure(call: Call<AccessTokenResponse>, t: Throwable) {
                    callback(null, Exception("Failed get access token: failed call back"))
                }
            })
    }


    suspend fun submitQuizResult(token: String, quizId: Int, isCorrect: Boolean): Boolean {
        val requestBody = mapOf(
            "quizId" to quizId.toString(),
            "isCorrect" to isCorrect.toString()
        )
        val response = RetrofitInstance.quizQueryApi.submitQuizResult(token, requestBody)
        if (response.isSuccessful) {
            return true
        } else {
            val errorBody = response.errorBody()?.string()
            throw Exception("Failed to submit quiz result: ${response.message()}\n$errorBody")
        }
    }


    suspend fun getQuizImage(token: String, quizId: Int): ResponseBody {
        return RetrofitInstance.quizQueryApi.getQuizImage(token, quizId)
    }

    suspend fun getRandomQuiz(
        token: String,
        subject: String,
        chapters: List<String>,
        hasUserQuiz: Boolean = false,
        hasDefaultQuiz: Boolean = true,
        hasSolvedQuiz: Boolean = false): RandomQuiz {
        val response = RetrofitInstance.quizQueryApi.getRandomQuiz(
            token = token,
            subject = subject,
            chapters = chapters,
            hasUserQuiz = hasUserQuiz,
            hasDefaultQuiz = hasDefaultQuiz,
            hasSolvedQuiz = hasSolvedQuiz
        )
        Log.d("test", response.message())
        if (response.isSuccessful) {
            return response.body() ?: throw Exception("Empty response body")
        } else {
            val errorBody = response.errorBody()?.string()
            throw Exception("Failed to get random quiz: ${response.message()}\n$errorBody")
        }
    }

    suspend fun getRandomQuiz(
        token: String,
        subject: List<String>,
        hasUserQuiz: Boolean = false,
        hasDefaultQuiz: Boolean = true,
        hasSolvedQuiz: Boolean = false
    ): RandomQuiz {
        val response = RetrofitInstance.quizQueryApi.getRandomQuizOnlySubject(
            token = token,
            subjects = subject,
            hasUserQuiz = hasUserQuiz,
            hasDefaultQuiz = hasDefaultQuiz,
            hasSolvedQuiz = hasSolvedQuiz
        )
        Log.d("API Response", response.message())
        if (response.isSuccessful) {
            return response.body() ?: throw Exception("Empty response body")
        } else {
            val errorBody = response.errorBody()?.string()
            throw Exception("Failed to get random quiz: ${response.message()}\n$errorBody")
        }
    }




    suspend fun setUserNickname(token: String, nickname: String): Boolean {
        val nicknameRequest = NicknameRequest(nickname)
        val response = RetrofitInstance.userAccountQueryApi.setUserNickname(token, nicknameRequest)
        if (response.isSuccessful) {
            return true
        } else {
            val errorBody = response.errorBody()?.string()
            throw Exception("Failed to set user nickname: ${response.message()}\n$errorBody")
        }
    }

    suspend fun getUserQuizStatistics(token: String): UserQuizStatistics {
        val response = RetrofitInstance.userAccountQueryApi.getUserQuizStatistics(token)
        if (response.isSuccessful) {
            return response.body() ?: throw Exception("Empty response body")
        } else {
            val errorBody = response.errorBody()?.string()
            throw Exception("Failed to get user quiz statistics: ${response.code()} ${response.message()}\n$errorBody")
        }
    }

    suspend fun getUserInfo(token: String): UserInfo {
        val response = RetrofitInstance.userAccountQueryApi.getUserInfo(token)
        if (response.isSuccessful) {
            val userInfo = response.body()
            if (userInfo != null) {
                return userInfo
            } else {
                throw Exception("Empty response body")
            }
        } else {
            val errorBody = response.errorBody()?.string()
            throw Exception("Failed to get user info: ${response.message()}\n$errorBody")
        }
    }

    suspend fun deactivateUser(token: String): Boolean {
        val response = RetrofitInstance.userAccountQueryApi.deactivateUser(token)
        if (response.isSuccessful) {
            Log.d("test", "deactivateUser success")
            return true
        } else {
            val errorBody = response.errorBody()?.string()
            throw Exception("Failed to deactivate user: ${response.message()}\n$errorBody")
        }
    }

    suspend fun refreshUserToken(token: String): Boolean {
        val response = RetrofitInstance.userAccountQueryApi.refreshUserToken(token)
        if (response.isSuccessful) {
            return true
        } else {
            val errorBody = response.errorBody()?.string()
            throw Exception("Failed to refresh user token: ${response.message()}\n$errorBody")
        }
    }

    suspend fun logoutUser(token: String): Boolean {
        val response = RetrofitInstance.userAccountQueryApi.logoutUser(token)
        if (response.isSuccessful) {
            return true
        } else {
            val errorBody = response.errorBody()?.string()
            throw Exception("Failed to logout user: ${response.message()}\n$errorBody")
        }
    }

    suspend fun reportQuiz(token: String, quizId: Int, content: String): Boolean {
        val requestBody = QuizReportRequest(quizId, content)
        val response = RetrofitInstance.quizQueryApi.reportQuiz(token, requestBody)
        if (response.isSuccessful) {
            return true
        } else {
            val errorBody = response.errorBody()?.string()
            throw Exception("Failed to report quiz: ${response.message()}\n$errorBody")
        }
    }

    suspend fun getQuizSubjects(token: String, onlySubject: Boolean=false): List<QuizSubject> {
        val response = RetrofitInstance.quizQueryApi.getQuizSubjects(token, onlySubject)
        if (response.isSuccessful) {
            return response.body() ?: emptyList()
        } else {
            val errorBody = response.errorBody()?.string()
            throw Exception("Failed to get quiz subjects: ${response.message()}\n$errorBody")
        }
    }

    suspend fun isSignedUser(token: String): Boolean {
        val request = isSignedRequest(token)
        val response = RetrofitInstance.userAccountQueryApi.isSigned(request)
        if (response.isSuccessful) {
            return response.body()?.registered ?: false
        } else {
            val errorBody = response.errorBody()?.string()
            throw Exception("Failed to is Signed user: ${response.message()}\n$errorBody")
        }
    }

}