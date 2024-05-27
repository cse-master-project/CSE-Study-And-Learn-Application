package com.example.cse_study_and_learn_application.utils

import android.content.Context

fun Context.dpToPx(dp: Int): Int {
    return (dp * resources.displayMetrics.density).toInt()
}


fun convertToIntOrZero(value: String): Int {
    return if (value == "NaN") {
        0
    } else {
        value.toInt()
    }
}