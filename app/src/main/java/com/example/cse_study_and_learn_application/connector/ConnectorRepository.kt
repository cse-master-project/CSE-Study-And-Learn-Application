package com.example.cse_study_and_learn_application.connector

import com.example.cse_study_and_learn_application.model.QuizCategory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ConnectorRepository(private val quizCategory: QuizCategoryInterface) {
    fun getExample(onResult: (QuizCategory?) -> Unit) {
        quizCategory.getExample().enqueue(object : Callback<QuizCategory> {
            override fun onResponse(call: Call<QuizCategory>, response: Response<QuizCategory>) {
                if (response.isSuccessful) {
                    onResult(response.body())
                } else {
                    onResult(null)
                }
            }

            override fun onFailure(call: Call<QuizCategory>, t: Throwable) {
                onResult(null)
            }
        })
    }
}