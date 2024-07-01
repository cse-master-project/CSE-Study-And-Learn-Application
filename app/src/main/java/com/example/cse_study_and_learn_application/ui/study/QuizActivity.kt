package com.example.cse_study_and_learn_application.ui.study

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.cse_study_and_learn_application.R
import com.example.cse_study_and_learn_application.connector.ConnectorRepository
import com.example.cse_study_and_learn_application.databinding.ActivityQuizBinding
import com.example.cse_study_and_learn_application.model.RandomQuiz
import com.example.cse_study_and_learn_application.model.TrueFalseQuizJsonContent
import com.example.cse_study_and_learn_application.ui.login.AccountAssistant
import com.example.cse_study_and_learn_application.utils.QuizType
import com.example.cse_study_and_learn_application.utils.QuizUtils
import com.example.cse_study_and_learn_application.utils.getQuizTypeFromInt
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.launch
import javax.security.auth.Subject

/**
 * Quiz activity
 *
 * @constructor Create empty Quiz activity
 * @author JYH
 * @since 2024-03-22
 */
class QuizActivity() : AppCompatActivity() {
    private lateinit var binding: ActivityQuizBinding

    private lateinit var subjects: String
    private lateinit var detailSubject: String

    private var quizResponse: RandomQuiz? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityQuizBinding.inflate(layoutInflater)
        setContentView(binding.root)

        subjects = intent.getStringExtra("subject").toString()
        detailSubject = intent.getStringExtra("detailSubject").toString()
        Log.d("detailSubject", detailSubject)

        binding.ibBackPres.setOnClickListener {
            onBackPressed()
        }

        binding.ibReport.setOnClickListener {
            showReportDialog(quizResponse)
        }

        binding.ibGrading.setOnClickListener {
            when(val currentFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView)) {
                is GradingFragment -> {
                    requestQuiz(subjects, detailSubject)
                }
                is MultipleChoiceQuizFragment -> currentFragment.onAnswerSubmit()

                is ShortAnswerQuizFragment -> currentFragment.onAnswerSubmit()

                is TrueFalseQuizFragment -> currentFragment.onAnswerSubmit()

                is FillBlankQuizFragment -> currentFragment.onAnswerSubmit()
            }

        }
        requestQuiz(subjects, detailSubject)

    }

    private fun requestQuiz(subjects: String, detailSubject: String){
        lifecycleScope.launch {
            try {
                val response = QuizUtils.loadQuizData(
                AccountAssistant.getServerAccessToken(applicationContext),
                subjects,
                detailSubject
                ) ?: throw NullPointerException()
                Log.i("Server Response", "Get Random Quiz: $response")
                quizResponse = response
                showQuiz(response)
            } catch (e: Exception) {
                Log.e("Server Response", "failed Load Quiz Data", e)
                Toast.makeText(this@QuizActivity, "더 이상 풀 문제가 없습니다.", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun showQuiz(response: RandomQuiz?) {

        val fragmentTransaction = supportFragmentManager.beginTransaction()
        val fragment = when (getQuizTypeFromInt(response!!.quizType)) {
            QuizType.MULTIPLE_CHOICE_QUIZ -> MultipleChoiceQuizFragment.newInstance(response)
            QuizType.SHORT_ANSWER_QUIZ-> ShortAnswerQuizFragment.newInstance(response)
            QuizType.MATING_QUIZ-> MatingQuizFragment.newInstance(response)
            QuizType.TRUE_FALSE_QUIZ-> TrueFalseQuizFragment.newInstance(response)
            QuizType.FILL_BLANK_QUIZ-> FillBlankQuizFragment.newInstance(response)
            else-> {
                Log.e("QuizActivity", "showQuiz : Not Found fragment")
                throw NullPointerException()
            }
        }

        fragmentTransaction.replace(R.id.fragmentContainerView, fragment)
        fragmentTransaction.commit()

    }

    private fun showReportDialog(response: RandomQuiz?) {
        if (response != null) {
            val dialogView = layoutInflater.inflate(R.layout.dialog_quiz_report, null)
            val dropdown = dialogView.findViewById<TextInputLayout>(R.id.dropdown_menu)
            val dropdownText = dropdown.editText as AutoCompleteTextView

            // 드롭다운 메뉴에 표시할 항목들
            val items = listOf("문제에 오타가 있습니다.", "정답이 올바르지 않습니다.", "그림이 올바르지 않습니다.", "해설이 올바르지 않습니다.", "기타")

            // ArrayAdapter를 사용하여 항목들을 AutoCompleteTextView에 연결
            val adapter = ArrayAdapter(this, R.layout.item_report_list, items)
            dropdownText.setAdapter(adapter)

            // 다이얼로그 생성
            MaterialAlertDialogBuilder(this)
                .setTitle(Html.fromHtml("<b>문제 신고<b>", Html.FROM_HTML_MODE_LEGACY) )
                .setView(dialogView)
                .setPositiveButton("신고") { dialog, which ->
                    lifecycleScope.launch {
                        val report = ConnectorRepository().reportQuiz(
                            token = AccountAssistant.getServerAccessToken(applicationContext),
                            quizId = response.quizId,
                            content = dropdownText.text.toString()
                        )
                        if (report) {
                            Toast.makeText(applicationContext, "문제를 신고했습니다.", Toast.LENGTH_SHORT).show()
                        }
                    }

                }
                .setNegativeButton("취소", null)
                .show()
        }


    }
}