package com.example.cse_study_and_learn_application.utils

import android.util.Log
import com.example.cse_study_and_learn_application.connector.ConnectorRepository
import com.example.cse_study_and_learn_application.model.FillBlankQuizJsonContent
import com.example.cse_study_and_learn_application.model.MatingQuizJsonContent
import com.example.cse_study_and_learn_application.model.MultipleChoiceQuizJsonContent
import com.example.cse_study_and_learn_application.model.RandomQuiz
import com.example.cse_study_and_learn_application.model.ShortAnswerQuizJsonContent
import com.example.cse_study_and_learn_application.model.TrueFalseQuizJsonContent
import com.example.cse_study_and_learn_application.utils.QuizType
import com.example.cse_study_and_learn_application.utils.getQuizTypeFromInt
import com.google.gson.Gson

object QuizUtils {
    suspend fun loadQuizData(token: String, subject: String, detailSubject: String): RandomQuiz? {
        Log.d("test", "subject = ${subject}, details = $detailSubject")
        return try {
            ConnectorRepository().getRandomQuiz(
                token = token,
                subject = subject,
                detailSubject = detailSubject
            )
        } catch (e: Exception) {
            Log.e("QuizActivity","getQuizFailed", e)
            null
        }
    }
}