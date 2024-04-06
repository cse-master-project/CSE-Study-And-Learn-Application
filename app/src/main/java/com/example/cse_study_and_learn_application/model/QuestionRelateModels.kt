package com.example.cse_study_and_learn_application.model

/**
 * Subject
 *
 * @property title
 * @property bg
 * @property cnt
 * @property icon
 * @constructor Create empty QuizCategory
 * @author kjy
 * @since 2024-03-09
 *
 * (2024-04-06)
 * Subject -> QuizCategory 클래스 이름 변경
 */
data class QuizCategory(val title: String, val bg: String, val cnt: String, val icon: String)


/**
 * Subject content
 *
 * @property title
 * @constructor Create empty Subject content
 * @author kjy
 * @since 2024-03-09
 *
 *
 * (2024-04-06)
 * SubjectContent -> QuizContentCategory 클래스 이름 변경
 */
data class QuizContentCategory(val title: String, val selected: Boolean)
