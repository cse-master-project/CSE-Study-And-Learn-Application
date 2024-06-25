package com.example.cse_study_and_learn_application.ui.study

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.commit
import com.example.cse_study_and_learn_application.R
import com.example.cse_study_and_learn_application.databinding.FragmentShortAnswerQuizBinding
import com.example.cse_study_and_learn_application.model.MultipleChoiceQuizJsonContent
import com.google.gson.Gson

/**
 * Short answer fragment
 *
 * @constructor Create empty Short answer fragment
 * @author JYH
 * @since 2024-03-18
 */
class ShortAnswerQuizFragment : Fragment(), AppBarImageButtonListener {

    private lateinit var binding: FragmentShortAnswerQuizBinding

    private var userAnswer: String? = null
    private lateinit var answer: String
    private lateinit var commentary: String
    private var quizId: Int? = null
    private var quizType: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentShortAnswerQuizBinding.inflate(inflater)
        quizId = arguments?.getInt("quizId")
        quizType = arguments?.getInt("quizType")
        val hasImg = arguments?.getBoolean("hasImg")
        val jsonString = arguments?.getString("contents")
        val content = Gson().fromJson(jsonString, MultipleChoiceQuizJsonContent::class.java)
        val quiz = content.quiz
        val options = content.option
        answer = content.answer
        commentary = content.commentary

        // 이미지 유무 판별
        if (hasImg!!) {
            binding.ivQuizImage.visibility = View.VISIBLE
            // binding.ivQuizImage.setImageResource()
        }

        binding.tvQuizText.text = quiz

        return binding.root
    }

    // 앱바의 채점 버튼 클릭
    override fun onAnswerSubmit() {
        userAnswer = binding.etAnswer.text.toString()
        if(userAnswer == "") {
            Toast.makeText(context, "답을 입력 해주세요.", Toast.LENGTH_SHORT).show()
        } else {
            try {
                val bundle = Bundle().apply {
                    putString("userAnswer", userAnswer)
                    putString("answer", answer)
                    putString("commentary", commentary)
                    putInt("quizId", quizId!!)
                    putInt("quizType", quizType!!)
                }
//                Log.d("test","ua: ${userAnswer}, a: $answer, c: $commentary, qi: $quizId, qt: $quizType")
                parentFragmentManager.commit {
                    replace(R.id.fragmentContainerView, GradingFragment().apply {
                        arguments = bundle
                    })
                    addToBackStack(null)
                }
            } catch (e: Exception) {
                Log.e("MultipleChoiceQuizFragment", "onAnswerSubmit", e)
            }

        }
    }

    companion object {
        fun newInstance(contents: String, hasImg: Boolean, quizId: Int, quizType: Int): ShortAnswerQuizFragment {
            val args = Bundle()

            val fragment = ShortAnswerQuizFragment()
            args.putInt("quizType", quizType)
            args.putInt("quizId", quizId)
            args.putString("contents", contents)
            args.putBoolean("hasImg", hasImg)
            fragment.arguments = args
            return fragment
        }
    }


}