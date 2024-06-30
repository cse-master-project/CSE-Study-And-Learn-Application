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

    private var quizId: Int? = null
    private var quizType: Int? = null
    private lateinit var userAnswer: String
    private lateinit var answer: String
    private lateinit var answerString: String
    private lateinit var commentary: String

    private val quizViewModel: QuizViewModel by viewModels()

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

    private fun failUpdate() {
        val nickname = AccountAssistant.nickname
        quizViewModel.getStatsByNickname(nickname) {
            it?.let {
                Log.d("test", "변경전: $it")
                quizViewModel.insertOrUpdate(nickname, it.correctAnswers, it.wrongAnswers + 1)
                quizViewModel.getStatsByNickname(nickname) { qs ->
                    qs?.let {
                        Log.d("test", "변경후: $qs")
                    }
                }
            }
        }
    }

    private fun successUpdate() {
        val nickname = AccountAssistant.nickname
        quizViewModel.getStatsByNickname(nickname) {
            it?.let {
                Log.d("test", "변경전: $it")
                quizViewModel.insertOrUpdate(nickname, it.correctAnswers + 1, it.wrongAnswers)
                quizViewModel.getStatsByNickname(nickname) { qs ->
                    qs?.let {
                        Log.d("test", "변경후: $qs")
                    }
                }
            }
        }
    }

    private fun grading() {
        when (getQuizTypeFromInt(quizType!!)) {
            QuizType.MULTIPLE_CHOICE_QUIZ -> {
                if (userAnswer == answer) {
                    // 정답
                    binding.ivGnuChar.setImageResource(R.drawable.gnu_hei)
                    successUpdate()
                } else {
                    // 오답
                    binding.ivGnuChar.setImageResource(R.drawable.gnu_no)
                    failUpdate()

                }
                binding.btnAnswer.text = answer
                binding.tvAnswer.text = answerString
            }
            QuizType.SHORT_ANSWER_QUIZ -> {
                if (userAnswer == answer) {
                    // 정답
                    binding.ivGnuChar.setImageResource(R.drawable.gnu_hei)
                    successUpdate()
                } else {
                    // 오답
                    binding.ivGnuChar.setImageResource(R.drawable.gnu_no)
                    failUpdate()
                }
                binding.btnAnswer.text = "답"
                binding.tvAnswer.text = answer
            }
            QuizType.MATING_QUIZ -> {
                val userAnswer = arguments?.getStringArrayList("userAnswer")
                val answer = arguments?.getStringArrayList("answer")

                if (userAnswer != null && answer != null && userAnswer.toSet() == answer.toSet()) {
                    Log.d("test", "정답")
                    Log.d("test", commentary.toString())
                    // resultSubmit(quizId!!, true)
                    successUpdate()
                } else {
                    Log.d("test", "오답")
                    Log.d("test", commentary.toString())
                    // resultSubmit(quizId!!, false)
                    failUpdate()
                }
            }
            QuizType.TRUE_FALSE_QUIZ -> {
                if (userAnswer == answer) {
                    // 정답
                    binding.ivGnuChar.setImageResource(R.drawable.gnu_hei)
                    successUpdate()
                } else {
                    // 오답
                    binding.ivGnuChar.setImageResource(R.drawable.gnu_no)
                    failUpdate()
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
                    successUpdate()
                } else {
                    binding.ivGnuChar.setImageResource(R.drawable.gnu_no)
                    failUpdate()
                }
                binding.btnAnswer.text = "답"

                when (answer.length) {
                    1 -> binding.tvAnswer.text = "1: ${answer[0]}"
                    2 -> binding.tvAnswer.text = "1: ${answer[0]} 2: ${answer[1]}"
                    3 -> binding.tvAnswer.text = "1: ${answer[0]} 2: ${answer[1]} 3: ${answer[2]}"
                }
            }
            else -> {
                Log.d("failure", "not found quiz type: ${quizType.toString()}")
            }
        }
        binding.tvCommentary.text = commentary
    }

}