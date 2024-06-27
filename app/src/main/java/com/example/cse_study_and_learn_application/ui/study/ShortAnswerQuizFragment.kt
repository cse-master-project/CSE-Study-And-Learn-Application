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
import androidx.fragment.app.commit
import androidx.lifecycle.lifecycleScope
import com.example.cse_study_and_learn_application.R
import com.example.cse_study_and_learn_application.connector.ConnectorRepository
import com.example.cse_study_and_learn_application.databinding.FragmentShortAnswerQuizBinding
import com.example.cse_study_and_learn_application.model.RandomQuiz
import com.example.cse_study_and_learn_application.model.ShortAnswerQuizJsonContent
import com.example.cse_study_and_learn_application.ui.login.AccountAssistant
import com.google.gson.Gson
import kotlinx.coroutines.launch

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
    private var image: Bitmap? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentShortAnswerQuizBinding.inflate(inflater)

        requireArguments().let {
            quizId = it.getInt("quizId")
            quizType = it.getInt("quizType")
            val hasImg = it.getBoolean("hasImg")
            val jsonString = it.getString("contents")

            val content = Gson().fromJson(jsonString, ShortAnswerQuizJsonContent::class.java)
            val quiz = content.quiz
            answer = content.answer
            commentary = content.commentary

            // 이미지 유무 판별
            if (hasImg) {
                binding.ivQuizImage.visibility = View.VISIBLE
                lifecycleScope.launch {
                    try {
                        val response = ConnectorRepository().getQuizImage(AccountAssistant.getServerAccessToken(requireContext()), quizId!!)
                        val decoded = Base64.decode(response.string(), Base64.DEFAULT)
                        image = BitmapFactory.decodeByteArray(decoded, 0, decoded.size)
                        binding.ivQuizImage.visibility = View.VISIBLE
                        binding.ivQuizImage.setImageBitmap(image)
                    } catch (e: Exception) {
                        Log.e("MultipleChoiceQuizFragment", "get Image Failure", e)
                    }
                }
            }

            binding.tvQuizText.text = quiz
        }



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
                Log.e("ShortAnswerQuizFragment", "onAnswerSubmit", e)
            }
        }
    }

    companion object {
        fun newInstance(response: RandomQuiz): ShortAnswerQuizFragment {
            val args = Bundle()

            val fragment = ShortAnswerQuizFragment()
            args.putInt("quizType", response.quizType)
            args.putInt("quizId", response.quizId)
            args.putString("contents", response.jsonContent)
            args.putBoolean("hasImg", response.hasImage)
            fragment.arguments = args
            return fragment
        }
    }


}