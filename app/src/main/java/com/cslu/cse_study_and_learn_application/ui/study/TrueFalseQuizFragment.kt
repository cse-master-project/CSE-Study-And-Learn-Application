package com.cslu.cse_study_and_learn_application.ui.study

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.cslu.cse_study_and_learn_application.R
import com.cslu.cse_study_and_learn_application.connector.ConnectorRepository
import com.cslu.cse_study_and_learn_application.databinding.FragmentTrueFalseQuizBinding
import com.cslu.cse_study_and_learn_application.model.RandomQuiz
import com.cslu.cse_study_and_learn_application.model.TrueFalseQuizJsonContent
import com.cslu.cse_study_and_learn_application.ui.login.AccountAssistant
import com.cslu.cse_study_and_learn_application.ui.other.DesignToast
import com.google.gson.Gson
import kotlinx.coroutines.launch


/**
 * True false quiz fragment
 *
 * @constructor Create empty True false quiz fragment
 * @author JYH
 * @since 2024-03-18
 */
class TrueFalseQuizFragment : Fragment(), AppBarImageButtonListener {

    private lateinit var binding: FragmentTrueFalseQuizBinding
    private var loadNextQuiz: (() -> Unit)? = null

    private var userAnswer: String? = null
    private lateinit var answer: String
    private lateinit var commentary: String
    private var quizId: Int? = null
    private var quizType: Int? = null
    private var image: Bitmap? = null

    private var isAnswerSubmitted = false
    private var bottomSheet: BottomSheetGradingFragment? = null

    private var explanationDialog: BottomSheetGradingFragment? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTrueFalseQuizBinding.inflate(inflater)
        (activity as? QuizActivity)?.setExplanationButtonEnabled(false)
        (activity as? QuizActivity)?.setGradingButtonClickListener { onAnswerSubmit() }

        requireArguments().let {
            quizId = it.getInt("quizId")
            quizType = it.getInt("quizType")
            val hasImg = it.getBoolean("hasImg")
            val jsonString = it.getString("contents")

            val content = Gson().fromJson(jsonString, TrueFalseQuizJsonContent::class.java)
            answer = content.answer
            commentary = content.commentary

            binding.tvQuizText.text = content.quiz

            if (hasImg) {
                binding.ivQuizImage.visibility = View.VISIBLE
                loadImage()
            }

            binding.rbTrue.setOnClickListener {
                if (!isAnswerSubmitted) {
                    userAnswer = "1"
                    updateRadioButtons()
                }
            }

            binding.rbFalse.setOnClickListener {
                if (!isAnswerSubmitted) {
                    userAnswer = "0"
                    updateRadioButtons()
                }
            }
        }

        return binding.root
    }

    private fun updateRadioButtons() {
        binding.rbTrue.isChecked = userAnswer == "1"
        binding.rbFalse.isChecked = userAnswer == "0"
    }

    private fun loadImage() {
        lifecycleScope.launch {
            try {
                val response = ConnectorRepository().getQuizImage(AccountAssistant.getServerAccessToken(requireContext()), quizId!!)
                val decoded = Base64.decode(response.string(), Base64.DEFAULT)
                image = BitmapFactory.decodeByteArray(decoded, 0, decoded.size)
                binding.ivQuizImage.setImageBitmap(image)
            } catch (e: Exception) {
                Log.e("TrueFalseQuizFragment", "get Image Failure", e)
            }
        }
    }

    override fun onAnswerSubmit() {
        if (userAnswer == null) {
            DesignToast.makeText(requireContext(), DesignToast.LayoutDesign.INFO, "답을 선택해주세요.")
                .show()
        } else {
            try {
                if (!isAnswerSubmitted) {
                    isAnswerSubmitted = true
                    (activity as? QuizActivity)?.setExplanationButtonEnabled(true)
                    (activity as? QuizActivity)?.setGradingButtonText("다음 문제")
                    (activity as? QuizActivity)?.setGradingButtonClickListener { loadNextQuiz?.invoke() }

                    val isCorrect = userAnswer == answer
                    (activity as? QuizActivity)?.resultSubmit(quizId!!, isCorrect) // 결과 제출

                    updateRadioButtonsAppearance(isCorrect)

                    explanationDialog = BottomSheetGradingFragment.newInstance(
                        isCorrect = isCorrect,
                        commentary = commentary,
                    )
                    explanationDialog?.setOnNextQuizListener {
                        (activity as? QuizActivity)?.setExplanationButtonEnabled(false)
                    }
                    disableRadioButtons()
                } else {
                    // 이미 답변을 제출한 경우, 다음 문제로 넘어갑니다.
                    loadNextQuiz?.invoke()
                }
            } catch (e: Exception) {
                Log.e("TrueFalseQuizFragment", "onAnswerSubmit", e)
            }
        }
    }

    private fun updateRadioButtonsAppearance(isCorrect: Boolean) {
        val correctAnswerButton = if (answer == "1") binding.rbTrue else binding.rbFalse
        val incorrectAnswerButton = if (answer == "1") binding.rbFalse else binding.rbTrue

        if (isCorrect) {
            correctAnswerButton.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.correct_card_background))
        } else {
            correctAnswerButton.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.correct_card_background))
            incorrectAnswerButton.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.incorrect_card_background))
        }
    }

    fun showExplanationDialog() {
        if (isAnswerSubmitted && explanationDialog != null) {
            explanationDialog?.show(parentFragmentManager, explanationDialog?.tag)
        }
    }

    fun setLoadNextQuizListener(listener: () -> Unit) {
        loadNextQuiz = {
            // 상태 초기화
            isAnswerSubmitted = false
            userAnswer = null
            binding.rbTrue.isChecked = false
            binding.rbFalse.isChecked = false
            binding.rbTrue.isEnabled = true
            binding.rbFalse.isEnabled = true
            binding.rbTrue.setBackgroundColor(ContextCompat.getColor(requireContext(), android.R.color.transparent))
            binding.rbFalse.setBackgroundColor(ContextCompat.getColor(requireContext(), android.R.color.transparent))
            (activity as? QuizActivity)?.setGradingButtonClickListener { onAnswerSubmit() }
            (activity as? QuizActivity)?.setExplanationButtonEnabled(false)
            listener.invoke()
        }
    }

    private fun disableRadioButtons() {
        binding.rbTrue.isEnabled = false
        binding.rbFalse.isEnabled = false
    }

    companion object {
        fun newInstance(response: RandomQuiz): TrueFalseQuizFragment {
            val args = Bundle()

            val fragment = TrueFalseQuizFragment()
            args.putInt("quizType", response.quizType)
            args.putInt("quizId", response.quizId)
            args.putString("contents", response.jsonContent)
            args.putBoolean("hasImg", response.hasImage)
            fragment.arguments = args
            return fragment
        }
    }
}