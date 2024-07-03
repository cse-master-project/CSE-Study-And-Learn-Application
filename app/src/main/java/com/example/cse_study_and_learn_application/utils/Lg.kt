package com.example.cse_study_and_learn_application.utils

import android.util.Log

object Lg {
    fun e(tag: String, location: String, message: String) {
        Log.e(tag, "[$location] $message")
    }

    fun d(tag: String, location: String, message: String) {
        Log.d(tag, "[$location] $message")
    }

    fun i(tag: String, location: String, message: String) {
        Log.i(tag, "[$location] $message")
    }

}