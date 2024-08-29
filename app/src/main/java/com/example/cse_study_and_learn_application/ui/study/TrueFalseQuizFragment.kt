package com.example.cse_study_and_learn_application.ui.study

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.commit
import androidx.lifecycle.lifecycleScope
import com.example.cse_study_and_learn_application.R
import com.example.cse_study_and_learn_application.connector.ConnectorRepository
import com.example.cse_study_and_learn_application.databinding.FragmentTrueFalseQuizBinding
import com.example.cse_study_and_learn_application.model.MultipleChoiceQuizJsonContent
import com.example.cse_study_and_learn_application.model.RandomQuiz
import com.example.cse_study_and_learn_application.model.TrueFalseQuizJsonContent
import com.example.cse_study_and_learn_application.ui.login.AccountAssistant
import com.example.cse_study_and_learn_application.ui.other.DesignToast
import com.google.gson.Gson
import kotlinx.coroutines.launch
import java.util.Random
import kotlin.properties.Delegates


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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTrueFalseQuizBinding.inflate(inflater)

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
            DesignToast.makeText(requireContext(), DesignToast.LayoutDesign.ERROR, "답을 선택해주세요.").show()
        } else {
            try {
                if (!isAnswerSubmitted) {
                    isAnswerSubmitted = true
                    bottomSheet = BottomSheetGradingFragment.newInstance(
                        quizId = quizId!!,
                        userAnswer = userAnswer!!,
                        answer = answer,
                        answerString = if (answer == "1") "참" else "거짓",
                        commentary = commentary,
                        quizType = quizType!!
                    )
                    bottomSheet?.setOnNextQuizListener {
                        loadNextQuiz?.invoke()
                    }
                    disableRadioButtons()
                }
                bottomSheet?.show(parentFragmentManager, bottomSheet?.tag)
            } catch (e: Exception) {
                Log.e("TrueFalseQuizFragment", "onAnswerSubmit", e)
            }
        }
    }

    fun setLoadNextQuizListener(listener: () -> Unit) {
        loadNextQuiz = listener
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