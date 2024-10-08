package com.cslu.cse_study_and_learn_application.utils

import android.graphics.LinearGradient
import android.graphics.Shader
import android.widget.TextView

fun TextView.setTextColorAsLinearGradient(colors: Array<Int>) {
    if (colors.isEmpty()) {
        return
    }

    setTextColor(colors[0])
    this.paint.shader = LinearGradient(
        0f,
        0f,
        paint.measureText(this.text.toString()),
        this.textSize,
        colors.toIntArray(),
        arrayOf(0f, 1f).toFloatArray(),
        Shader.TileMode.CLAMP
    )
}