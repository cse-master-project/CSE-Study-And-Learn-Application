package com.cslu.cse_study_and_learn_application.ui.study

import FillBlankAnswerAdapter
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.cslu.cse_study_and_learn_application.connector.ConnectorRepository
import com.cslu.cse_study_and_learn_application.databinding.FragmentFillBlankQuizBinding
import com.cslu.cse_study_and_learn_application.model.FillBlankQuizJsonContent
import com.cslu.cse_study_and_learn_application.model.RandomQuiz
import com.cslu.cse_study_and_learn_application.ui.login.AccountAssistant
import com.cslu.cse_study_and_learn_application.ui.other.DesignToast
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.launch

class FillBlankQuizFragment : Fragment(), AppBarImageButtonListener {

    private lateinit var binding: FragmentFillBlankQuizBinding
    private lateinit var answerAdapter: FillBlankAnswerAdapter
    private lateinit var answer: List<List<String>>
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
        binding = FragmentFillBlankQuizBinding.inflate(inflater, container, false)
        setupQuizActivity()

        arguments?.let { bundle ->
            parseArguments(bundle)
        }

        return binding.root
    }

    private fun setupQuizActivity() {
        (activity as? QuizActivity)?.apply {
            setExplanationButtonEnabled(false)
            setGradingButtonClickListener { onAnswerSubmit() }
        }
    }

    private fun parseArguments(bundle: Bundle) {
        quizId = bundle.getInt("quizId")
        quizType = bundle.getInt("quizType")
        val hasImg = bundle.getBoolean("hasImg")
        val jsonString = bundle.getString("contents") ?: ""
        val creator = bundle.getString("creator") ?: "Unknown"

        // Log.d("test", "Quiz ID: $quizId")

        val contentType = object : TypeToken<FillBlankQuizJsonContent>() {}.type
        val content: FillBlankQuizJsonContent = Gson().fromJson(jsonString, contentType)
        answer = content.answer
        commentary = content.commentary
        originalQuizText = content.quiz

        setupUI(hasImg, creator)
    }

    private fun setupUI(hasImg: Boolean, creator: String) {
        binding.tvQuizText.text = originalQuizText
        updateQuizText()
        binding.tvCreator.text = "출제자: $creator"

        if (hasImg) {
            binding.ivQuizImage.visibility = View.VISIBLE
            loadImage()
        }

        setupRecyclerView(answer)
    }

    private fun setupRecyclerView(answers: List<List<String>>) {
        // 각 빈칸의 정답 수를 계산하여 어댑터를 초기화합니다.
        // 여기서는 전체 정답 수를 계산하여 각 빈칸을 채울 수 있도록 설정합니다.
        val totalAnswers = answers.flatten().size
        answerAdapter = FillBlankAnswerAdapter(requireContext(), totalAnswers)

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
                Log.e("FillBlankQuizFragment", "Failed to load image", e)
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
            handleAnswerSubmission(userAnswers)
        } else {
            loadNextQuiz?.invoke()
        }
    }

    private fun handleAnswerSubmission(userAnswers: List<String>) {
        isAnswerSubmitted = true
        (activity as? QuizActivity)?.apply {
            setExplanationButtonEnabled(true)
            setGradingButtonText("다음 문제")
            setGradingButtonClickListener { loadNextQuiz?.invoke() }
        }

        // answer는 List<List<String>> 형태이므로, 이를 flatten 하지 않고 그대로 전달
        answerAdapter.submitAnswers(answer)
        updateQuizText()

        // 각 사용자의 답변이 해당 위치의 정답 리스트 중 하나와 일치하는지 확인
        val isCorrect = userAnswers.zip(answer).all { (user, correctList) ->
            correctList.any { correct -> user.trim().equals(correct.trim(), ignoreCase = true) }
        }

        (activity as? QuizActivity)?.resultSubmit(quizId!!, isCorrect)

        explanationDialog = BottomSheetGradingFragment.newInstance(
            isCorrect = isCorrect,
            commentary = commentary,
        )
    }


    private fun updateQuizText() {
        var updatedText = originalQuizText.replace("<<빈칸>>", "(   )")

        // 정답이 제출된 후 정답으로 업데이트
        if(isAnswerSubmitted) {
            answer.forEach { correctAnswers ->
                val correctAnswer = correctAnswers.joinToString(" / ") { "[$it]" }
                updatedText = updatedText.replaceFirst("(   )", correctAnswer)
            }
        }

        binding.tvQuizText.text = updatedText
    }

    fun showExplanationDialog() {
        if (isAnswerSubmitted) {
            explanationDialog?.show(parentFragmentManager, explanationDialog?.tag)
        }
    }

    fun setLoadNextQuizListener(listener: () -> Unit) {
        loadNextQuiz = {
            resetQuizState()
            listener.invoke()
        }
    }

    private fun resetQuizState() {
        isAnswerSubmitted = false
        answerAdapter.resetAnswers()
        binding.tvQuizText.text = originalQuizText
        (activity as? QuizActivity)?.apply {
            setGradingButtonText("정답 확인")
            setGradingButtonClickListener { onAnswerSubmit() }
            setExplanationButtonEnabled(false)
        }
    }

    companion object {
        fun newInstance(response: RandomQuiz): FillBlankQuizFragment {
            return FillBlankQuizFragment().apply {
                arguments = Bundle().apply {
                    putInt("quizType", response.quizType)
                    putInt("quizId", response.quizId)
                    putString("contents", response.jsonContent)
                    putBoolean("hasImg", response.hasImage)
                    putString("creator", response.creator)
                }
            }
        }
    }
}