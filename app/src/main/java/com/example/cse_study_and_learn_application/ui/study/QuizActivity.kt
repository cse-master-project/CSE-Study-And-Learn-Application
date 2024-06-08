package com.example.cse_study_and_learn_application.ui.study

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
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


        // 테스트용 RandomQuiz 객체 생성
        val response = RandomQuiz(
            quizId = 48,
            subject = "C",
            detailSubject = "배열",
            quizType = 3, // 선잇기 퀴즈 타입
            jsonContent = """
                {
                    "quiz": "선을 그어주세요.",
                    "left_option": ["aaa", "bbb", "ccc"],
                    "right_option": ["ddd", "eee"],
                    "answer": ["0t1", "1t0", "0t0"],
                    "commentary": "해설"
                }
            """.trimIndent(),
            hasImage = false
        )

        // showQuiz 메서드 호출하여 프래그먼트 표시
        showQuiz(response)

        // 테스트 임시 주석
        // val subjects = intent.getStringExtra("subject")
        // val detailSubject = intent.getStringExtra("detailSubject")
        // Log.d("detailSubject", detailSubject.toString())
        //
        // lifecycleScope.launch {
        //     val response = QuizUtils.loadQuizData(
        //         AccountAssistant.getServerAccessToken(applicationContext),
        //         subjects!!,
        //         detailSubject!!
        //     )
        //     showQuiz(response!!)
        // }

        binding.ibGrading.setOnClickListener {
            val currentFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView)
            if(currentFragment is OnAnswerSubmitListener) {
                currentFragment.onAnswerSubmit()
            }
        }

    }

    private fun showQuiz(response: RandomQuiz) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()

        val fragment = when (getQuizTypeFromInt(response.quizType)) {
            QuizType.MULTIPLE_CHOICE_QUIZ -> MultipleChoiceQuizFragment.newInstance(response.jsonContent, response.hasImage, response.quizId)
            QuizType.SHORT_ANSWER_QUIZ-> ShortAnswerQuizFragment.newInstance(response.jsonContent)
            QuizType.MATING_QUIZ-> MatingQuizFragment.newInstance(response.jsonContent, response.hasImage, response.quizId)
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