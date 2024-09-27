package com.cslu.cse_study_and_learn_application.utils

data class HighlightItem(
    val viewId: Int,
    val description: String,
    val showPosition: HighlightPosition = HighlightPosition.UI_BOTTOM,
    val position: Int? = null,
    val scaleFactor: Float = 1.2f // 크기 비율 변수, 기본값 1.2
)

enum class HighlightPosition {
    SCREEN_TOP,
    SCREEN_BOTTOM,
    UI_TOP,
    UI_BOTTOM
}
