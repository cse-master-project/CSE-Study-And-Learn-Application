package com.example.cse_study_and_learn_application.ui.study

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.cse_study_and_learn_application.R
import com.example.cse_study_and_learn_application.connector.ConnectorRepository
import com.example.cse_study_and_learn_application.databinding.FragmentGradingBinding
import com.example.cse_study_and_learn_application.ui.login.AccountAssistant
import com.example.cse_study_and_learn_application.ui.statistics.QuizViewModel
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


    private val quizViewModel: QuizViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGradingBinding.inflate(inflater)

        requireArguments().let {
            val quizId = it.getInt("quizId")
            val userAnswer = it.getString("userAnswer").toString()
            val answer = it.getString("answer").toString()

            grading(quizId, userAnswer, answer)
        }

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


    private fun grading(quizId: Int, userAnswer: String, answer: String) {
        when (getQuizTypeFromInt(requireArguments().getInt("quizType"))) {
            QuizType.MULTIPLE_CHOICE_QUIZ -> {
                if (userAnswer == answer) {
                    binding.ivGnuChar.setImageResource(R.drawable.gnu_hei)
                    resultSubmit(quizId, true)
                } else {
                    binding.ivGnuChar.setImageResource(R.drawable.gnu_no)
                    resultSubmit(quizId, false)
                }
                binding.btnAnswer.text = answer
                binding.tvAnswer.text = requireArguments().getString("answerString").toString()
            }
            QuizType.SHORT_ANSWER_QUIZ -> {
                if (userAnswer == answer) {
                    binding.ivGnuChar.setImageResource(R.drawable.gnu_hei)
                    resultSubmit(quizId, true)
                } else {
                    binding.ivGnuChar.setImageResource(R.drawable.gnu_no)
                    resultSubmit(quizId, false)
                }
                binding.btnAnswer.text = "답"
                binding.tvAnswer.text = answer
            }
            QuizType.MATING_QUIZ -> {
                val userAnswer = requireArguments().getStringArrayList("userAnswer")
                val answer = requireArguments().getStringArrayList("answer")

                if (userAnswer != null && answer != null && userAnswer.toSet() == answer.toSet()) {
                    resultSubmit(quizId, true)
                } else {
                    resultSubmit(quizId, false)
                }
            }
            QuizType.TRUE_FALSE_QUIZ -> {
                if (userAnswer == answer) {
                    // 정답
                    binding.ivGnuChar.setImageResource(R.drawable.gnu_hei)
                    resultSubmit(quizId, true)
                } else {
                    // 오답
                    binding.ivGnuChar.setImageResource(R.drawable.gnu_no)
                    resultSubmit(quizId, false)
                }
                binding.btnAnswer.text = "답"
                binding.tvAnswer.text = answer
            }
            QuizType.FILL_BLANK_QUIZ -> {
                var flag = true
                for (i: Int in answer.indices) {
                    if (answer[i] != userAnswer[i]) {
                        flag = false
                    }
                }

                if (flag) {
                    binding.ivGnuChar.setImageResource(R.drawable.gnu_hei)
                    resultSubmit(quizId, true)
                } else {
                    binding.ivGnuChar.setImageResource(R.drawable.gnu_no)
                    resultSubmit(quizId, false)
                }
                binding.btnAnswer.text = "답"

                var answers = ""

                for(i: Int in answer.indices) {
                    answers += " ${i}번 정답: ${answer[i]}"
                }

                binding.tvAnswer.text = answers
            }
            else -> {
                Log.e("failure", "not found quiz type: ${requireArguments().getInt("quizType")}")
            }
        }
        binding.tvCommentary.text = requireArguments().getString("commentary")
    }

}