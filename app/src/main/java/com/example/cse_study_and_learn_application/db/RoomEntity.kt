package com.example.cse_study_and_learn_application.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "quiz_stats")
data class QuizStats(
    @PrimaryKey val nickname: String,
    val correctAnswers: Int,
    val wrongAnswers: Int
)
