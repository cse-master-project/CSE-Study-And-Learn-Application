package com.example.cse_study_and_learn_application.connector

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private const val BASE_URL = "http://43.203.19.235"
    private const val PORT = "8080"
    private const val SERVER_URL = "$BASE_URL:$PORT"
    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(SERVER_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val quizQueryApi : QuizQueryApi by lazy {
        retrofit.create(QuizQueryApi::class.java)
    }
}