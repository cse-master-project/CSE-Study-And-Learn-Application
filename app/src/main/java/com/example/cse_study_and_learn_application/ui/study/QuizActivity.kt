package com.example.cse_study_and_learn_application.ui.study

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.JsonToken
import android.util.Log
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.lifecycleScope
import com.example.cse_study_and_learn_application.R
import com.example.cse_study_and_learn_application.connector.ConnectorRepository
import com.example.cse_study_and_learn_application.connector.RetrofitInstance
import com.example.cse_study_and_learn_application.databinding.ActivityQuizBinding
import com.example.cse_study_and_learn_application.model.FillBlankQuizJsonContent
import com.example.cse_study_and_learn_application.model.MatingQuizJsonContent
import com.example.cse_study_and_learn_application.model.MultipleChoiceQuizJsonContent
import com.example.cse_study_and_learn_application.model.RandomQuiz
import com.example.cse_study_and_learn_application.model.ShortAnswerQuizJsonContent
import com.example.cse_study_and_learn_application.model.TrueFalseQuizJsonContent
import com.example.cse_study_and_learn_application.ui.login.AccountAssistant
import com.example.cse_study_and_learn_application.utils.QuizType
import com.example.cse_study_and_learn_application.utils.getQuizTypeFromInt
import com.google.gson.Gson
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlin.properties.Delegates

/**
 * Quiz activity
 *
 * @constructor Create empty Quiz activity
 * @author JYH
 * @since 2024-03-22
 */
class QuizActivity() : AppCompatActivity() {
    private lateinit var binding: ActivityQuizBinding;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityQuizBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val subject = intent.getStringExtra("subject")
        val detailSubject = intent.getStringExtra("detailSubject")

        lifecycleScope.launch {
            val response = QuizUtils.loadQuizData(AccountAssistant.getServerAccessToken(applicationContext), subject!!, detailSubject!!)
            showQuiz(response!!)
        }

    }

    private fun showQuiz(response: RandomQuiz) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()

        val fragment = when (getQuizTypeFromInt(response.quizType)) {
            QuizType.MULTIPLE_CHOICE_QUIZ -> MultipleChoiceQuizFragment.newInstance(response.jsonContent)
            QuizType.SHORT_ANSWER_QUIZ-> ShortAnswerQuizFragment.newInstance(response.jsonContent)
            QuizType.MATING_QUIZ-> MatingQuizFragment.newInstance(response.jsonContent)
            QuizType.TRUE_FALSE_QUIZ-> TrueFalseQuizFragment.newInstance(response.jsonContent)
            QuizType.FILL_BLANK_QUIZ-> FillBlankQuizFragment.newInstance(response.jsonContent)
            else-> {
                Log.e("QuizActivity", "showQuiz : Not Found fragment")
                throw NullPointerException()
            }
        }

        fragmentTransaction.replace(R.id.fragmentContainerView, fragment)
        fragmentTransaction.commit()
    }


}