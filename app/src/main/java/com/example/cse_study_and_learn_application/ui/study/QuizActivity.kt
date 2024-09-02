package com.example.cse_study_and_learn_application.ui.study

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.opengl.Matrix
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.example.cse_study_and_learn_application.R
import com.example.cse_study_and_learn_application.connector.ConnectorRepository
import com.example.cse_study_and_learn_application.databinding.ActivityQuizBinding
import com.example.cse_study_and_learn_application.model.RandomQuiz
import com.example.cse_study_and_learn_application.ui.login.AccountAssistant
import com.example.cse_study_and_learn_application.ui.other.DesignToast
import com.example.cse_study_and_learn_application.utils.Lg
import com.example.cse_study_and_learn_application.utils.QuizType
import com.example.cse_study_and_learn_application.utils.getQuizTypeFromInt
import com.example.cse_study_and_learn_application.utils.showAlert
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
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
                binding.tvTitle.text = "많은(?) 과목"
            } else {
                subjects = it.getStringExtra("subject").toString()
                detailSubject = it.getStringExtra("detailSubject").toString()
                binding.tvTitle.text = subjects
            }
            hasUserQuiz = it.getBooleanExtra("hasUserQuiz", true)
            hasDefaultQuiz = it.getBooleanExtra("hasDefaultQuiz", true)
            hasSolvedQuiz = it.getBooleanExtra("hasSolvedQuiz", true)

            binding.ibBackPres.scaleX = 1.2f
            binding.ibBackPres.scaleY = 1.2f

            onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    showAlert(
                        this@QuizActivity,
                        "알림",
                        "정말로 그만 푸시겠습니까?",
                        positiveButtonText = "확인",
                        onPositiveClick = {
                            finish()
                        },
                        negativeButtonText = "취소"
                    )
                }
            })

            binding.ibBackPres.setOnClickListener {
                showAlert(
                    this,
                    "알림",
                    "정말로 그만 푸시겠습니까?",
                    onPositiveClick = {
                        finish()
                    },
                    negativeButtonText = "취소"
                )
            }

            binding.ibReport.setOnClickListener {
                showReportDialog(quizResponse)
            }

            binding.btnGrading.setOnClickListener {
                when(val currentFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView)) {
                    is MultipleChoiceQuizFragment -> currentFragment.onAnswerSubmit()
                    is ShortAnswerQuizFragment -> currentFragment.onAnswerSubmit()
                    is MatingQuizFragment -> currentFragment.onAnswerSubmit()
                    is TrueFalseQuizFragment -> currentFragment.onAnswerSubmit()
                    is FillBlankQuizFragment -> currentFragment.onAnswerSubmit()
                }

            }
            getQuiz()
        }
    }


    private fun getQuiz() {
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
                //Toast.makeText(this@QuizActivity, "더 이상 풀 문제가 없습니다.", Toast.LENGTH_SHORT).show()
                DesignToast.makeText(this@QuizActivity, DesignToast.LayoutDesign.INFO, "더 이상 풀 문제가 없습니다.").show()
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
                //Toast.makeText(this@QuizActivity, "더 이상 풀 문제가 없습니다.", Toast.LENGTH_SHORT).show()
                DesignToast.makeText(this@QuizActivity, DesignToast.LayoutDesign.INFO, "더 이상 풀 문제가 없습니다.").show()
                finish()
            }
        }
    }

    private fun loadNextQuiz() {
        getQuiz()
    }

    private fun showQuiz(response: RandomQuiz?) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        val fragment = when (getQuizTypeFromInt(response!!.quizType)) {
            QuizType.MULTIPLE_CHOICE_QUIZ -> MultipleChoiceQuizFragment.newInstance(response)
            QuizType.SHORT_ANSWER_QUIZ -> ShortAnswerQuizFragment.newInstance(response)
            QuizType.MATING_QUIZ -> MatingQuizFragment.newInstance(response)
            QuizType.TRUE_FALSE_QUIZ -> TrueFalseQuizFragment.newInstance(response)
            QuizType.FILL_BLANK_QUIZ -> FillBlankQuizFragment.newInstance(response)
            else -> {
                Log.e("QuizActivity", "showQuiz : Not Found fragment")
                throw NullPointerException()
            }
        }

        // Fragment에 loadNextQuiz 메서드 설정
        when (fragment) {
            is MultipleChoiceQuizFragment -> fragment.setLoadNextQuizListener { loadNextQuiz() }
            is ShortAnswerQuizFragment -> fragment.setLoadNextQuizListener { loadNextQuiz() }
            is MatingQuizFragment -> fragment.setLoadNextQuizListener { loadNextQuiz() }
            is TrueFalseQuizFragment -> fragment.setLoadNextQuizListener { loadNextQuiz() }
            is FillBlankQuizFragment -> fragment.setLoadNextQuizListener { loadNextQuiz() }
        }

        fragmentTransaction.replace(R.id.fragmentContainerView, fragment)
        fragmentTransaction.commit()

    }

    private fun showReportDialog(response: RandomQuiz?) {
        if (response != null) {
            val dialogView = layoutInflater.inflate(R.layout.dialog_quiz_report, null)
            val dropdown = dialogView.findViewById<TextInputLayout>(R.id.dropdown_menu)
            val dropdownText = dropdown.editText as AutoCompleteTextView
            val otherReasonLayout = dialogView.findViewById<TextInputLayout>(R.id.ll_other_reason)
            val otherReasonEditText = dialogView.findViewById<TextInputEditText>(R.id.et_other_reason)

            // 드롭다운 메뉴에 표시할 항목들
            val items = listOf("문제에 오타가 있습니다.", "정답이 올바르지 않습니다.", "그림이 올바르지 않습니다.", "해설이 올바르지 않습니다.", "기타")

            // ArrayAdapter를 사용하여 항목들을 AutoCompleteTextView에 연결
            val adapter = ArrayAdapter(this, R.layout.item_report_list, items)
            dropdownText.setAdapter(adapter)

            dropdownText.setOnItemClickListener { _, _, position, _ ->
                if (items[position] == "기타") {
                    otherReasonLayout.visibility = View.VISIBLE
                } else {
                    otherReasonLayout.visibility = View.GONE
                }
            }

            val reportDialog = MaterialAlertDialogBuilder(this)
                .setTitle(Html.fromHtml("<b>문제 신고</b>", Html.FROM_HTML_MODE_LEGACY))
                .setView(dialogView)
                .setPositiveButton("신고", null)
                .setNegativeButton("취소", null)
                .create()

            reportDialog.setOnShowListener { dialog ->
                val positiveButton = (dialog as AlertDialog).getButton(AlertDialog.BUTTON_POSITIVE)
                positiveButton.setOnClickListener {
                    val selectedReason = dropdownText.text.toString()
                    if (selectedReason != "") {
                        val reportContent = if (selectedReason == "기타") {
                            val otherReason = otherReasonEditText.text.toString()
                            if (otherReason.isBlank()) {
                                showAlert(this, "알림", "기타 사유를 입력해주세요.")
                                return@setOnClickListener
                            }
                            "기타: $otherReason"
                        } else {
                            selectedReason
                        }

                        lifecycleScope.launch {
                            val report = ConnectorRepository().reportQuiz(
                                token = AccountAssistant.getServerAccessToken(applicationContext),
                                quizId = response.quizId,
                                content = reportContent
                            )
                            if (report) {
                                //Toast.makeText(applicationContext, "문제를 신고했습니다.", Toast.LENGTH_SHORT).show()
                                DesignToast.makeText(this@QuizActivity, DesignToast.LayoutDesign.INFO, "문제를 신고 했습니다.").show()
                                dialog.dismiss()
                            }
                        }
                    } else {
                        showAlert(this,"알림", "신고 사유를 선택해주세요.")
                    }
                }
            }
            reportDialog.show()
        }
    }

}

