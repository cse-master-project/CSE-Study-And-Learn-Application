package com.example.cse_study_and_learn_application.model

import com.google.gson.annotations.SerializedName

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
    @SerializedName("quizId") val quizId: Int,
    @SerializedName("subject") val subject: String,
    @SerializedName("detailSubject") val detailSubject: String,
    @SerializedName("correctRate") val correctRate: Int,
    @SerializedName("jsonContent") val jsonContent: String,
    @SerializedName("createAt") val createAt: String,
    @SerializedName("hasImage") val hasImage: Boolean
)

data class Sort(
    @SerializedName("empty")
    val empty: Boolean,
    @SerializedName("sorted")
    val sorted: Boolean,
    @SerializedName("unsorted")
    val unsorted: Boolean
)

data class Pageable(
    @SerializedName("offset")
    val offset: Int,
    @SerializedName("sort")
    val sort: Sort,
    @SerializedName("pageNumber")
    val pageNumber: Int,
    @SerializedName("pageSize")
    val pageSize: Int,
    @SerializedName("paged")
    val paged: Boolean,
    @SerializedName("unpaged")
    val unpaged: Boolean
)

data class QuizReport(
    @SerializedName("quizReportId")
    val quizReportId: Int,
    @SerializedName("quizId")
    val quizId: Int,
    @SerializedName("userId")
    val userId: String,
    @SerializedName("content")
    val content: String,
    @SerializedName("reportAt")
    val reportAt: String
)

data class DefaultQuiz(
    @SerializedName("quizId")
    val quizId: Int,
    @SerializedName("subject")
    val subject: String,
    @SerializedName("detailSubject")
    val detailSubject: String,
    @SerializedName("correctRate")
    val correctRate: Int,
    @SerializedName("jsonContent")
    val jsonContent: String,
    @SerializedName("createAt")
    val createAt: String,
    @SerializedName("hasImage")
    val hasImage: Boolean
)

data class DefaultQuizResponse(
    @SerializedName("totalPages")
    val totalPages: Int,
    @SerializedName("totalElements")
    val totalElements: Int,
    @SerializedName("first")
    val first: Boolean,
    @SerializedName("last")
    val last: Boolean,
    @SerializedName("size")
    val size: Int,
    @SerializedName("content")
    val content: List<DefaultQuiz>,
    @SerializedName("number")
    val number: Int,
    @SerializedName("sort")
    val sort: Sort,
    @SerializedName("numberOfElements")
    val numberOfElements: Int,
    @SerializedName("pageable")
    val pageable: Pageable
)

data class QuizResponse(
    @SerializedName("totalPages")
    val totalPages: Int,
    @SerializedName("totalElements")
    val totalElements: Int,
    @SerializedName("first")
    val first: Boolean,
    @SerializedName("last")
    val last: Boolean,
    @SerializedName("size")
    val size: Int,
    @SerializedName("content")
    val content: List<Quiz>,
    @SerializedName("number")
    val number: Int,
    @SerializedName("sort")
    val sort: Sort,
    @SerializedName("numberOfElements")
    val numberOfElements: Int,
    @SerializedName("pageable")
    val pageable: Pageable,
    @SerializedName("empty")
    val empty: Boolean
)

data class RandomQuiz(
    @SerializedName("quizId")
    val quizId: Int,
    @SerializedName("subject")
    val subject: String,
    @SerializedName("detailSubject")
    val detailSubject: String,
    @SerializedName("jsonContent")
    val jsonContent: String,
    @SerializedName("hasImage")
    val hasImage: Boolean
)

data class QuizReportRequest(
    @SerializedName("quizId")
    val quizId: Int,
    @SerializedName("content")
    val content: String
)

data class QuizSubject(
    @SerializedName("subjectId")
    val subjectId: Int,
    @SerializedName("subject")
    val subject: String,
    @SerializedName("detailSubject")
    val detailSubject: List<String>
)