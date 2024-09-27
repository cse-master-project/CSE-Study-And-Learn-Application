package com.cslu.cse_study_and_learn_application.model

import com.google.gson.annotations.SerializedName

//data class UserQuizResponse(
//    val totalPages: Int,
//    val totalElements: Int,
//    val first: Boolean,
//    val last: Boolean,
//    val size: Int,
//    val content: List<Quiz>,
//    val number: Int,
//    val sort: Sort,
//    val numberOfElements: Int,
//    val pageable: Pageable,
//    val empty: Boolean
//)

//data class Quiz(
//    @SerializedName("quizId") val quizId: Int,
//    @SerializedName("subject") val subject: String,
//    @SerializedName("detailSubject") val detailSubject: String,
//    @SerializedName("correctRate") val correctRate: Int,
//    @SerializedName("jsonContent") val jsonContent: String,
//    @SerializedName("createAt") val createAt: String,
//    @SerializedName("hasImage") val hasImage: Boolean
//
//)

data class MultipleChoiceQuizJsonContent(
    val quiz: String,
    val option: List<String>,
    val answer: String,
    val commentary: String
)

data class ShortAnswerQuizJsonContent(
    val quiz: String,
    val answer: List<String>,
    val commentary: String
)

data class MatingQuizJsonContent(
    @SerializedName("quiz")
    val quiz: String,
    @SerializedName("left_option")
    val leftOption: List<String>,
    @SerializedName("right_option")
    val rightOption: List<String>,
    @SerializedName("answer")
    val answer: List<String>,
    @SerializedName("commentary")
    val commentary: String
)

data class TrueFalseQuizJsonContent(
    val quiz: String,
    val answer: String,
    val commentary: String
)

data class FillBlankQuizJsonContent(
    val quiz: String,
    val answer: List<String>,
    val commentary: String
)

//data class Sort(
//    @SerializedName("empty")
//    val empty: Boolean,
//    @SerializedName("sorted")
//    val sorted: Boolean,
//    @SerializedName("unsorted")
//    val unsorted: Boolean
//)

//data class Pageable(
//    @SerializedName("offset")
//    val offset: Int,
//    @SerializedName("sort")
//    val sort: Sort,
//    @SerializedName("pageNumber")
//    val pageNumber: Int,
//    @SerializedName("pageSize")
//    val pageSize: Int,
//    @SerializedName("paged")
//    val paged: Boolean,
//    @SerializedName("unpaged")
//    val unpaged: Boolean
//)

data class RandomQuiz(
    @SerializedName("quizId")
    val quizId: Int,
    @SerializedName("subject")
    val subject: String,
    @SerializedName("chapter")
    val chapter: String,
    @SerializedName("quizType")
    val quizType: Int,
    @SerializedName("jsonContent")
    val jsonContent: String,
    @SerializedName("hasImage")
    val hasImage: Boolean,
    @SerializedName("creator")
    val creator: String
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
    @SerializedName("chapters")
    val chapters: List<String>
)

//data class ErrorResponse(
//    @SerializedName("error")
//    val error: String,
//    @SerializedName("description")
//    val description: String
//)