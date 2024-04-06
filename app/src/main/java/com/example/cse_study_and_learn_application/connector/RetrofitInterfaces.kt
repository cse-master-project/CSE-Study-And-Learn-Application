package com.example.cse_study_and_learn_application.connector

import com.example.cse_study_and_learn_application.model.QuizCategory
import retrofit2.Call
import retrofit2.http.GET

interface QuizCategoryInterface {
    @GET("endpoint")
    fun getExample(): Call<QuizCategory>
}
