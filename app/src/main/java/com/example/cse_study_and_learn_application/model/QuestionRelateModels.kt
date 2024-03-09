package com.example.cse_study_and_learn_application.model

/**
 * Subject
 *
 * @property title
 * @property bg
 * @property cnt
 * @property icon
 * @constructor Create empty Subject
 * @author kjy
 * @since 2024-03-09
 */
data class Subject(val title: String, val bg: String, val cnt: String, val icon: String)


/**
 * Subject content
 *
 * @property title
 * @constructor Create empty Subject content
 * @author kjy
 * @since 2024-03-09
 */
data class SubjectContent(val title: String)
