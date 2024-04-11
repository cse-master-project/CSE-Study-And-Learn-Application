package com.example.cse_study_and_learn_application.utils

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Url

interface RetrofitApi {

    @GET
    fun login(@Url url: String): Call<String>

}