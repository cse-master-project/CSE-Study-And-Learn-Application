package com.example.cse_study_and_learn_application.ui.study

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.example.cse_study_and_learn_application.R
import com.example.cse_study_and_learn_application.databinding.ActivityQuizBinding
import com.example.cse_study_and_learn_application.model.RandomQuiz
import com.example.cse_study_and_learn_application.ui.login.AccountAssistant
import com.example.cse_study_and_learn_application.utils.QuizType
import com.example.cse_study_and_learn_application.utils.QuizUtils
import com.example.cse_study_and_learn_application.utils.getQuizTypeFromInt
import kotlinx.coroutines.launch

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

        val subjects = intent.getStringExtra("subject")
        val detailSubject = intent.getStringExtra("detailSubject")
        Log.d("detailSubject", detailSubject.toString())

        lifecycleScope.launch {
            val response = QuizUtils.loadQuizData(
                AccountAssistant.getServerAccessToken(applicationContext),
                subjects!!,
                detailSubject!!
            )
            Log.d("response", response.toString())
            showQuiz(response!!)
        }

        binding.ibGrading.setOnClickListener {
            val currentFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView)
            if(currentFragment is AppBarImageButtonListener) {
                currentFragment.onAnswerSubmit()
            }
        }

    }

    private fun showQuiz(response: RandomQuiz) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()

        val fragment = when (getQuizTypeFromInt(response.quizType)) {
            QuizType.MULTIPLE_CHOICE_QUIZ -> MultipleChoiceQuizFragment.newInstance(response.jsonContent, response.hasImage, response.quizId, response.quizType)
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