package com.example.cse_study_and_learn_application.ui.study

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.core.content.ContextCompat
import androidx.fragment.app.commit
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cse_study_and_learn_application.R
import com.example.cse_study_and_learn_application.connector.ConnectorRepository
import com.example.cse_study_and_learn_application.databinding.FragmentFillBlankQuizBinding
import com.example.cse_study_and_learn_application.model.FillBlankQuizJsonContent
import com.example.cse_study_and_learn_application.model.RandomQuiz
import com.example.cse_study_and_learn_application.ui.login.AccountAssistant
import com.example.cse_study_and_learn_application.ui.other.DesignToast
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import kotlinx.coroutines.launch


/**
 * Fill blank quiz fragment
 *
 * @constructor Create empty Fill blank quiz fragment
 * @author JYH
 * @since 2024-03-29
 */
class FillBlankQuizFragment : Fragment(), AppBarImageButtonListener {

    private lateinit var binding: FragmentFillBlankQuizBinding
    private lateinit var answerAdapter: FillBlankAnswerAdapter
    private lateinit var answer: List<String>
    private lateinit var commentary: String
    private var quizId: Int? = null
    private var quizType: Int? = null
    private var image: Bitmap? = null
    private lateinit var originalQuizText: String

    private var loadNextQuiz: (() -> Unit)? = null
    private var isAnswerSubmitted = false
    private var explanationDialog: BottomSheetGradingFragment? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFillBlankQuizBinding.inflate(inflater)
        (activity as? QuizActivity)?.setExplanationButtonEnabled(false)
        (activity as? QuizActivity)?.setGradingButtonClickListener { onAnswerSubmit() }

        requireArguments().let {
            quizId = it.getInt("quizId")
            quizType = it.getInt("quizType")
            val hasImg = it.getBoolean("hasImg")
            val jsonString = it.getString("contents")
            val content = Gson().fromJson(jsonString, FillBlankQuizJsonContent::class.java)
            answer = content.answer[0].split(",")
            commentary = content.commentary
            originalQuizText = content.quiz

            binding.tvQuizText.text = originalQuizText

            if (hasImg) {
                binding.ivQuizImage.visibility = View.VISIBLE
                loadImage()
            }

            setupRecyclerView(answer)
        }

        return binding.root
    }

    private fun setupRecyclerView(answers: List<String>) {
        answerAdapter = FillBlankAnswerAdapter(answers.toMutableList())
        binding.rvAnswers.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = answerAdapter
        }
    }

    private fun loadImage() {
        lifecycleScope.launch {
            try {
                val response = ConnectorRepository().getQuizImage(AccountAssistant.getServerAccessToken(requireContext()), quizId!!)
                val decoded = Base64.decode(response.string(), Base64.DEFAULT)
                image = BitmapFactory.decodeByteArray(decoded, 0, decoded.size)
                binding.ivQuizImage.setImageBitmap(image)
            } catch (e: Exception) {
                Log.e("FillBlankQuizFragment", "get Image Failure", e)
            }
        }
    }

    override fun onAnswerSubmit() {
        val userAnswers = answerAdapter.getAnswers()
        Log.d("useranswer", userAnswers.toString())
        if (userAnswers.any { it.isBlank() }) {
            DesignToast.makeText(requireContext(), DesignToast.LayoutDesign.INFO, "모든 빈칸을 채워주세요.").show()
            return  // 빈 답변이 있으면 여기서 함수를 종료합니다.
        }
        if (!isAnswerSubmitted) {
            isAnswerSubmitted = true
            (activity as? QuizActivity)?.setExplanationButtonEnabled(true)
            (activity as? QuizActivity)?.setGradingButtonText("다음 문제")
            (activity as? QuizActivity)?.setGradingButtonClickListener { loadNextQuiz?.invoke() }

            // 정답 비교 및 색상 업데이트
            val isCorrect = updateAnswersAppearance(userAnswers)

            // 문제 텍스트 업데이트
            updateQuizText()

            (activity as? QuizActivity)?.resultSubmit(quizId!!, isCorrect) // 결과 제출

            explanationDialog = BottomSheetGradingFragment.newInstance(
                isCorrect = isCorrect,
                commentary = commentary,
            )
            updateButtonText() // 버튼 텍스트 업데이트
            explanationDialog?.setOnNextQuizListener {
                (activity as? QuizActivity)?.setExplanationButtonEnabled(false)
            }
            disableAnswerInputs()
        } else {
            // 이미 답변을 제출한 경우, 다음 문제로 넘어갑니다.
            loadNextQuiz?.invoke()
        }
    }

    private fun updateAnswersAppearance(userAnswers: List<String>): Boolean {
        var allCorrect = true
        userAnswers.forEachIndexed { index, userAnswer ->
            if (userAnswer.isBlank()) {
                answerAdapter.setItemBackgroundColor(index, ContextCompat.getColor(requireContext(), R.color.incorrect_card_background))
                allCorrect = false
            } else {
                val isCorrect = userAnswer.trim().equals(answer[index].trim(), ignoreCase = true)
                if (isCorrect) {
                    answerAdapter.setItemBackgroundColor(index, ContextCompat.getColor(requireContext(), R.color.correct_card_background))
                } else {
                    answerAdapter.setItemBackgroundColor(index, ContextCompat.getColor(requireContext(), R.color.incorrect_card_background))
                    allCorrect = false
                }
            }
        }
        return allCorrect
    }

    private fun updateQuizText() {
        var updatedText = originalQuizText
        answer.forEachIndexed { _, correctAnswer ->
            updatedText = updatedText.replaceFirst("()", "($correctAnswer)")
        }
        binding.tvQuizText.text = updatedText
    }

    private fun updateButtonText() {
        (activity as? QuizActivity)?.setGradingButtonText("다음 문제")
    }
    fun showExplanationDialog() {
        if (isAnswerSubmitted && explanationDialog != null) {
            explanationDialog?.show(parentFragmentManager, explanationDialog?.tag)
        }
    }


    private fun disableAnswerInputs() {
        answerAdapter.disableAllInputs()
    }

    fun setLoadNextQuizListener(listener: () -> Unit) {
        loadNextQuiz = {
            // 상태 초기화
            isAnswerSubmitted = false
            answerAdapter.resetAnswers()
            binding.tvQuizText.text = originalQuizText
            (activity as? QuizActivity)?.setGradingButtonClickListener { onAnswerSubmit() }
            (activity as? QuizActivity)?.setExplanationButtonEnabled(false)

            // 입력 필드 활성화
            answerAdapter.enableAllInputs()

            listener.invoke()
        }
    }

    companion object {
        fun newInstance(response: RandomQuiz): FillBlankQuizFragment {
            val args = Bundle()

            val fragment = FillBlankQuizFragment()
            args.putInt("quizType", response.quizType)
            args.putInt("quizId", response.quizId)
            args.putString("contents", response.jsonContent)
            args.putBoolean("hasImg", response.hasImage)
            fragment.arguments = args

            return fragment
        }
    }
}