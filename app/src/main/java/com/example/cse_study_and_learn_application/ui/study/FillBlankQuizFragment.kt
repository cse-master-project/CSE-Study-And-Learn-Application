package com.example.cse_study_and_learn_application.ui.study

import FillBlankAnswerAdapter
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
        answerAdapter = FillBlankAnswerAdapter(requireContext(), answers.size)
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
        if (userAnswers.any { it.isBlank() }) {
            DesignToast.makeText(requireContext(), DesignToast.LayoutDesign.INFO, "모든 빈칸을 채워주세요.").show()
            return
        }

        if (!isAnswerSubmitted) {
            isAnswerSubmitted = true
            (activity as? QuizActivity)?.apply {
                setExplanationButtonEnabled(true)
                setGradingButtonText("다음 문제")
                setGradingButtonClickListener { loadNextQuiz?.invoke() }
            }

            answerAdapter.submitAnswers(answer)
            updateQuizText()

            val isCorrect = userAnswers.zip(answer).all { (user, correct) ->
                user.trim().equals(correct.trim(), ignoreCase = true)
            }
            (activity as? QuizActivity)?.resultSubmit(quizId!!, isCorrect)

            explanationDialog = BottomSheetGradingFragment.newInstance(
                isCorrect = isCorrect,
                commentary = commentary,
            )
        } else {
            loadNextQuiz?.invoke()
        }
    }


    private fun updateQuizText() {
        var updatedText = originalQuizText
        answer.forEachIndexed { _, correctAnswer ->
            updatedText = updatedText.replaceFirst("()", "($correctAnswer)")
        }
        binding.tvQuizText.text = updatedText
    }

    fun showExplanationDialog() {
        if (isAnswerSubmitted && explanationDialog != null) {
            explanationDialog?.show(parentFragmentManager, explanationDialog?.tag)
        }
    }

    fun setLoadNextQuizListener(listener: () -> Unit) {
        loadNextQuiz = {
            isAnswerSubmitted = false
            answerAdapter.resetAnswers()
            binding.tvQuizText.text = originalQuizText
            (activity as? QuizActivity)?.apply {
                setGradingButtonText("정답 확인")
                setGradingButtonClickListener { onAnswerSubmit() }
                setExplanationButtonEnabled(false)
            }
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