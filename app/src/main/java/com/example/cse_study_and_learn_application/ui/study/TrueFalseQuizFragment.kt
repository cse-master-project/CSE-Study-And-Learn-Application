package com.example.cse_study_and_learn_application.ui.study

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.commit
import com.example.cse_study_and_learn_application.R
import com.example.cse_study_and_learn_application.databinding.FragmentTrueFalseQuizBinding
import com.example.cse_study_and_learn_application.model.MultipleChoiceQuizJsonContent
import com.example.cse_study_and_learn_application.model.RandomQuiz
import com.example.cse_study_and_learn_application.model.TrueFalseQuizJsonContent
import com.google.gson.Gson
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

    lateinit var binding: FragmentTrueFalseQuizBinding
    private var userAnswer: String? = null
    private lateinit var answer: String
    private lateinit var commentary: String
    private var quizId: Int? = null
    private var quizType: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTrueFalseQuizBinding.inflate(inflater)

        quizId = arguments?.getInt("quizId")
        quizType = arguments?.getInt("quizType")
        val hasImg = arguments?.getBoolean("hasImg")
        val jsonString = arguments?.getString("contents")
        val content = Gson().fromJson(jsonString, TrueFalseQuizJsonContent::class.java)
        val quiz = content.quiz
        answer = content.answer
        commentary = content.commentary

        if (hasImg!!) {
            binding.ivQuizImage.visibility = View.VISIBLE
            // binding.ivQuizImage.setImageResource()
        }

        binding.tvQuizText.text = quiz

        binding.rbTrue.setOnClickListener {
            userAnswer = "1"
        }

        binding.rbFalse.setOnClickListener {
            userAnswer = "0"
        }

        return binding.root
    }

    override fun onAnswerSubmit() {
        if(userAnswer == null) {
            Toast.makeText(context, "답을 선택 해주세요.", Toast.LENGTH_SHORT).show()
        } else {
            try {
                val bundle = Bundle().apply {
                    putString("userAnswer", userAnswer!!)
                    putString("answer", answer)
                    putString("commentary", commentary)
                    putInt("quizId", quizId!!)
                    putInt("quizType", quizType!!)
                }
                Log.d("test","ua: ${userAnswer}, a: $answer, c: $commentary, qi: $quizId, qt: $quizType")
                parentFragmentManager.commit {
                    val prevFragment = parentFragmentManager.findFragmentById(R.id.fragmentContainerView)
                    if (prevFragment != null) {
                        remove(prevFragment)
                    }
                    add(R.id.fragmentContainerView, GradingFragment().apply {
                        arguments = bundle
                    })
                }
            } catch (e: Exception) {
                Log.e("TrueFalseQuizFragment", "onAnswerSubmit", e)
            }

        }
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