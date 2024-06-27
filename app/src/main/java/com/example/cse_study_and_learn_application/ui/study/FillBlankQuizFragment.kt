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
import com.example.cse_study_and_learn_application.databinding.FragmentFillBlankQuizBinding
import com.example.cse_study_and_learn_application.model.FillBlankQuizJsonContent
import com.example.cse_study_and_learn_application.model.RandomQuiz
import com.example.cse_study_and_learn_application.ui.login.AccountAssistant
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

    lateinit var binding: FragmentFillBlankQuizBinding

    private var userAnswer: ArrayList<String> = arrayListOf("", "", "")
    private lateinit var answer: List<String>
    private lateinit var commentary: String
    private var quizId: Int? = null
    private var quizType: Int? = null
    private var image: Bitmap? = null

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
            answer = content.answer
            commentary= content.commentary

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
                        Log.e("FillBlankQuizFragment", "get Image Failure", e)
                    }
                }
            }

            when (answer.count()) {
                2 -> binding.etAnswer1.visibility = View.VISIBLE
                3 -> {
                    binding.etAnswer2.visibility = View.VISIBLE
                    binding.etAnswer3.visibility = View.VISIBLE
                }
            }

        }


        return binding.root
    }

    override fun onAnswerSubmit() {
        when (answer.count()) {
            1 -> {
                userAnswer[0] = (binding.etAnswer1.text.toString())
            }
            2 -> {
                userAnswer[0] = binding.etAnswer1.text.toString()
                userAnswer[1] = binding.etAnswer2.text.toString()
            }
            3 -> {
                userAnswer[0] = binding.etAnswer1.text.toString()
                userAnswer[1] = binding.etAnswer2.text.toString()
                userAnswer[2] = binding.etAnswer3.text.toString()
            }
        }

        try {
            val bundle = Bundle().apply {
                putStringArrayList("userAnswer", userAnswer)
                putStringArrayList("answer", ArrayList(answer))
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
            Log.e("FillBlankQuizFragment", "onAnswerSubmit", e)
        }
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