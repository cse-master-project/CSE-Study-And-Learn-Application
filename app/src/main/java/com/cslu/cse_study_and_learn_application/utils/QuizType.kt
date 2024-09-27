package com.cslu.cse_study_and_learn_application.utils

enum class QuizType {
    NONE,
    MULTIPLE_CHOICE_QUIZ,
    SHORT_ANSWER_QUIZ,
    MATING_QUIZ,
    TRUE_FALSE_QUIZ,
    FILL_BLANK_QUIZ
}

fun getQuizTypeFromInt(type: Int): QuizType? {
    return enumValues<QuizType>().getOrNull(type)
}