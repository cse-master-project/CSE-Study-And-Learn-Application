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
import kotlinx.coroutines.launch

/**
 * A simple [Fragment] subclass.
 * Use the [GradingFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class GradingFragment : Fragment() {

    private lateinit var binding: FragmentGradingBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGradingBinding.inflate(inflater)

        val commentary = arguments?.getString("commentary")
        val quizId = arguments?.getInt("quizId")
        val quizType = arguments?.getInt("quizType")

        if (quizType == QuizType.MATING_QUIZ.ordinal) {
            val userAnswer = arguments?.getStringArrayList("userAnswer")
            val answer = arguments?.getStringArrayList("answer")

            if (userAnswer != null && answer != null && userAnswer.toSet() == answer.toSet()) {
                Log.d("test", "정답")
                Log.d("test", commentary.toString())
                resultSubmit(quizId!!, true)
            } else {
                Log.d("test", "오답")
                Log.d("test", commentary.toString())
                resultSubmit(quizId!!, false)
            }

        } else {
            val userAnswer = arguments?.getString("userAnswer")
            val answer = arguments?.getString("answer")
            if (userAnswer == answer) {
                Log.d("test", "정답")
                Log.d("test", commentary.toString())
                resultSubmit(quizId!!, true)
            } else {
                Log.d("test", "오답")
                Log.d("test", commentary.toString())
                resultSubmit(quizId!!, false)
            }
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
}
