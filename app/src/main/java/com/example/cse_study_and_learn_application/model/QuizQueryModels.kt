package com.example.cse_study_and_learn_application.model

data class UserQuizRequest(
    val page: Int,
    val size: Int,
    val sort: List<String>
)

data class UserQuizResponse(
    val totalPages: Int,
    val totalElements: Int,
    val first: Boolean,
    val last: Boolean,
    val size: Int,
    val content: List<Quiz>,
    val number: Int,
    val sort: Sort,
    val numberOfElements: Int,
    val pageable: Pageable,
    val empty: Boolean
)

data class QuizRandom(
    val quizId: Int,
    val subject: String,
    val detailSubject: String,
    val jsonContent: String,
    val hasImage: Boolean
)

data class Quiz(
    val quizId: Int,
    val subject: String,
    val detailSubject: String,
    val correctRate: Int,
    val jsonContent: String,
    val createAt: String,
    val hasImage: Boolean
)

data class MultipleChoiceQuizJsonContent(
    val type: Int,
    val quiz: String,
    val option: List<String>,
    val answer: String,
    val commentary: String
)

data class ShortAnswerQuizJsonContent(
    val type: Int,
    val quiz: String,
    val answer: String,
    val commentary: String
)

data class MatingQuizJsonContent(
    val type: Int,
    val quiz: String,
    val leftOption: List<String>,
    val rightOption: List<String>,
    val answer: List<String>,
    val commentary: String
)

data class TrueFalseQuizJsonContent(
    val type: Int,
    val quiz: String,
    val answer: Int,
    val commentary: String
)

data class FillBlankQuizFragment(
    val type: Int,
    val quiz: String,
    val answer: List<String>,
    val commentary: String
)

data class Sort(
    val empty: Boolean,
    val sorted: Boolean,
    val unsorted: Boolean
)

data class Pageable(
    val offset: Int,
    val sort: Sort,
    val pageNumber: Int,
    val pageSize: Int,
    val paged: Boolean,
    val unpaged: Boolean
)