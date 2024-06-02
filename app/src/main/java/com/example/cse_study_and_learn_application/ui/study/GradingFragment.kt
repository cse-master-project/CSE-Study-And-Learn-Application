package com.example.cse_study_and_learn_application.ui.study

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.cse_study_and_learn_application.R
import com.example.cse_study_and_learn_application.connector.ConnectorRepository
import com.example.cse_study_and_learn_application.databinding.FragmentGradingBinding
import com.example.cse_study_and_learn_application.ui.login.AccountAssistant
import com.example.cse_study_and_learn_application.utils.QuizType
import com.example.cse_study_and_learn_application.utils.getQuizTypeFromInt
import kotlinx.coroutines.launch

/**
 * A simple [Fragment] subclass.
 * Use the [GradingFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class GradingFragment : Fragment() {

    private lateinit var binding: FragmentGradingBinding

    private var quizId: Int? = null
    private var quizType: Int? = null
    private lateinit var userAnswer: String
    private lateinit var answer: String
    private lateinit var answerString: String
    private lateinit var commentary: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGradingBinding.inflate(inflater)

        arguments?.let {
            quizId = it.getInt("quizId")
            quizType = it.getInt("quizType")
            userAnswer = it.getString("userAnswer").toString()
            answer = it.getString("answer").toString()
            answerString = it.getString("answerString").toString()
            commentary = it.getString("commentary").toString()
        }

        grading()

        //resultSubmit(quizId!!, userAnswer == answer)

        return binding.root
    }

    private fun resultSubmit(quizId: Int, isCorrect: Boolean) {
        lifecycleScope.launch {
            val response = ConnectorRepository().submitQuizResult(
                token = AccountAssistant.getServerAccessToken(requireContext()),
                quizId = quizId,
                isCorrect = isCorrect
            )
        }
    }

    private fun grading() {
        if (userAnswer == answer) {
            // 정답
            binding.ivGnuChar.setImageResource(R.drawable.gnu_hei)
        } else {
            // 오답
            binding.ivGnuChar.setImageResource(R.drawable.gnu_no)
        }

        when (getQuizTypeFromInt(quizType!!)) {
            QuizType.MULTIPLE_CHOICE_QUIZ -> {
                binding.btnAnswer.text = answer
                binding.tvAnswer.text = answerString
                binding.tvCommentary.text = commentary
            }
            QuizType.SHORT_ANSWER_QUIZ -> {

            }
            QuizType.MATING_QUIZ -> {

            }
            QuizType.TRUE_FALSE_QUIZ -> {

            }
            QuizType.FILL_BLANK_QUIZ -> {

            }
            else -> {
                Log.d("test", quizType.toString())
            }
        }


    }

}