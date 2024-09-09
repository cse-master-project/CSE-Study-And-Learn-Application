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

    private var loadNextQuiz: (() -> Unit)? = null
    private var isAnswerSubmitted = false
    private var bottomSheet: BottomSheetGradingFragment? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFillBlankQuizBinding.inflate(inflater)

        requireArguments().let {
            quizId = it.getInt("quizId")
            quizType = it.getInt("quizType")
            val hasImg = it.getBoolean("hasImg")
            val jsonString = it.getString("contents")
            val content = Gson().fromJson(jsonString, FillBlankQuizJsonContent::class.java)
            answer = content.answer[0].split(",")
            commentary = content.commentary

            binding.tvQuizText.text = content.quiz

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
        if (!isAnswerSubmitted) {
            val userAnswers = answerAdapter.getAnswers()
            Log.d("test", userAnswers.toString())
            if (userAnswers.any { it.isBlank() }) {
                DesignToast.makeText(requireContext(), DesignToast.LayoutDesign.INFO, "모든 빈칸을 채워주세요.").show()
            } else {
                try {
                    isAnswerSubmitted = true
                    bottomSheet = BottomSheetGradingFragment.newInstance(
                        quizId = quizId!!,
                        userAnswer = userAnswers.joinToString(","),
                        answer = answer.joinToString(","),
                        answerString = answer.mapIndexed { index, s -> "${('a' + index)}: $s" }.joinToString("\n"),
                        commentary = commentary,
                        quizType = quizType!!
                    )
                    bottomSheet?.setOnNextQuizListener {
                        loadNextQuiz?.invoke()
                    }
                    disableAnswerInputs()
                    bottomSheet?.show(parentFragmentManager, bottomSheet?.tag)
                } catch (e: Exception) {
                    Log.e("FillBlankQuizFragment", "onAnswerSubmit", e)
                }
            }
        } else {
            bottomSheet?.show(parentFragmentManager, bottomSheet?.tag)
        }
    }

    private fun disableAnswerInputs() {
        answerAdapter.disableAllInputs()
    }

    fun setLoadNextQuizListener(listener: () -> Unit) {
        loadNextQuiz = listener
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