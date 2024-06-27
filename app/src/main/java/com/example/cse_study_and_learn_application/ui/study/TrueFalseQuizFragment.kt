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

    lateinit var binding: FragmentTrueFalseQuizBinding
    private var userAnswer: String? = null
    private lateinit var answer: String
    private lateinit var commentary: String
    private var quizId: Int? = null
    private var quizType: Int? = null
    private var image: Bitmap? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTrueFalseQuizBinding.inflate(inflater)

        requireArguments().let{
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

            binding.rbTrue.setOnClickListener {
                userAnswer = "1"
            }

            binding.rbFalse.setOnClickListener {
                userAnswer = "0"
            }
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