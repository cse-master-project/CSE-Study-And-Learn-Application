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

data class Quiz(
    val quizId: Int,
    val subject: String,
    val detailSubject: String,
    val correctRate: Int,
    val jsonContent: String,
    val createAt: String,
    val hasImage: Boolean
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