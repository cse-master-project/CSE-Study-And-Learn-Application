package com.example.cse_study_and_learn_application.ui.study

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.example.cse_study_and_learn_application.R
import com.example.cse_study_and_learn_application.connector.ConnectorRepository
import com.example.cse_study_and_learn_application.databinding.ActivityQuizBinding
import com.example.cse_study_and_learn_application.model.RandomQuiz
import com.example.cse_study_and_learn_application.ui.login.AccountAssistant
import com.example.cse_study_and_learn_application.utils.Lg
import com.example.cse_study_and_learn_application.utils.QuizType
import com.example.cse_study_and_learn_application.utils.getQuizTypeFromInt
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.launch
import kotlin.properties.Delegates

/**
 * Quiz activity
 *
 * @constructor Create empty Quiz activity
 * @author JYH
 * @since 2024-03-22
 */
class QuizActivity() : AppCompatActivity() {
    private lateinit var binding: ActivityQuizBinding

    private var subjects: String? = null
    private var subjectList: ArrayList<String>? = null
    private var detailSubject: String? = null
    private var hasUserQuiz by Delegates.notNull<Boolean>()
    private var hasDefaultQuiz by Delegates.notNull<Boolean>()
    private var hasSolvedQuiz by Delegates.notNull<Boolean>()
    private var isRandom = false
    private var quizResponse: RandomQuiz? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityQuizBinding.inflate(layoutInflater)
        setContentView(binding.root)

        intent.let {
            isRandom = it.getBooleanExtra("isRandom", false)
            if (isRandom) {
                subjectList = it.getStringArrayListExtra("subjectList")

            } else {
                subjects = it.getStringExtra("subject").toString()
                detailSubject = it.getStringExtra("detailSubject").toString()
            }
            hasUserQuiz = it.getBooleanExtra("hasUserQuiz", true)
            hasDefaultQuiz = it.getBooleanExtra("hasDefaultQuiz", true)
            hasSolvedQuiz = it.getBooleanExtra("hasSolvedQuiz", true)

            // Log.d("detailSubject", detailSubject)

            binding.ibBackPres.setOnClickListener {
                onBackPressed()
            }

            binding.ibReport.setOnClickListener {
                showReportDialog(quizResponse)
            }

            binding.ibGrading.setOnClickListener {
                when(val currentFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView)) {
                    is GradingFragment -> {
                        getQuiz()

                    }
                    is MultipleChoiceQuizFragment -> currentFragment.onAnswerSubmit()

                    is ShortAnswerQuizFragment -> currentFragment.onAnswerSubmit()

                    is TrueFalseQuizFragment -> currentFragment.onAnswerSubmit()

                    is FillBlankQuizFragment -> currentFragment.onAnswerSubmit()
                }

            }
            getQuiz()
        }
    }

    private fun getQuiz() {
        // Lg.d("test", this@QuizActivity.toString(), "subjectList = $subjectList")
        // Lg.d("test", this@QuizActivity.toString(), "hasUserQuiz = $hasUserQuiz")
        // Lg.d("test", this@QuizActivity.toString(), "hasDefaultQuiz = $hasDefaultQuiz")
        // Lg.d("test", this@QuizActivity.toString(), "hasSolvedQuiz = $hasSolvedQuiz")

        if (isRandom) {
            requestRandomQuiz(subjectList!!)
        } else {
            requestQuiz(subjects!!, detailSubject!!)
        }
    }

    private fun requestRandomQuiz(
        subjects: ArrayList<String>,
    ) {
        lifecycleScope.launch {
            try {
                val token = AccountAssistant.getServerAccessToken(this@QuizActivity)
                val response = ConnectorRepository().getRandomQuiz(token, subjects, hasDefaultQuiz, hasUserQuiz, hasSolvedQuiz)
                quizResponse = response
                showQuiz(response)
            } catch (e: Exception) {
                // Handle the error
                e.printStackTrace()
                Lg.e("test", QuizActivity::class.java.name, e.toString())
                Toast.makeText(this@QuizActivity, "더 이상 풀 문제가 없습니다.", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun requestQuiz(subjects: String, detailSubject: String){
        lifecycleScope.launch {
            try {
                Log.d("test",
                    "token = ${AccountAssistant.getServerAccessToken(applicationContext)}\n" +
                        "subject = ${subjects}\n" +
                        "detailSubject = ${detailSubject}\n " +
                        "hasUserQuiz = ${hasUserQuiz}\n " +
                        "hasDefaultQuiz = ${hasDefaultQuiz}\n " +
                        "hasSolvedQuiz = ${hasSolvedQuiz}\n "
                )
                val response = ConnectorRepository().getRandomQuiz(
                    token = AccountAssistant.getServerAccessToken(applicationContext),
                    subject = subjects,
                    detailSubject = detailSubject,
                    hasUserQuiz = hasUserQuiz,
                    hasDefaultQuiz = hasDefaultQuiz,
                    hasSolvedQuiz = hasSolvedQuiz
                )
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