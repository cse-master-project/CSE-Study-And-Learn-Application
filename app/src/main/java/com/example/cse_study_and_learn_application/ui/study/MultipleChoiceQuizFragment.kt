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
import com.example.cse_study_and_learn_application.databinding.FragmentMultipleChoiceQuizBinding
import com.example.cse_study_and_learn_application.model.MultipleChoiceQuizJsonContent
import com.example.cse_study_and_learn_application.model.QuizResponse
import com.example.cse_study_and_learn_application.model.RandomQuiz
import com.example.cse_study_and_learn_application.ui.login.AccountAssistant
import com.example.cse_study_and_learn_application.ui.other.DesignToast
import com.google.android.material.card.MaterialCardView
import com.google.gson.Gson
import kotlinx.coroutines.launch
import kotlinx.serialization.builtins.serializer

/**
 *
 * Use the [MultipleChoiceQuizFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 * @constructor A simple [Fragment] subclass.
 * @author JYH
 * @since 2024-03-17
 */
class MultipleChoiceQuizFragment : Fragment(), AppBarImageButtonListener {

    private lateinit var binding: FragmentMultipleChoiceQuizBinding
    private val cards: ArrayList<MaterialCardView> = ArrayList()
    private var userAnswer: String? = null
    private lateinit var answer: String
    private lateinit var answerString: String
    private lateinit var commentary: String
    private var quizId: Int? = null
    private var quizType: Int? = null
    private var image: Bitmap? = null
    private var options: List<String>? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMultipleChoiceQuizBinding.inflate(inflater)

        requireArguments().let {
            quizId = it.getInt("quizId")
            quizType = it.getInt("quizType")
            val jsonString = it.getString("contents")
            val content = Gson().fromJson(jsonString, MultipleChoiceQuizJsonContent::class.java)
            val hasImg = requireArguments().getBoolean("hasImg")
            val options = content.option
            answer = content.answer
            commentary = content.commentary
            binding.tvQuizText.text = content.quiz

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

            binding.tvAnswer1.text = options[0]
            binding.tvAnswer2.text = options[1]
            binding.tvAnswer3.text = options[2]
            binding.tvAnswer4.text = options[3]

            cards.add(binding.cvAnswer1)
            cards.add(binding.cvAnswer2)
            cards.add(binding.cvAnswer3)
            cards.add(binding.cvAnswer4)

            cards[0].setOnClickListener {
                onlyOneCardViewToggle(cards[0])
                userAnswer = "1"
                answerString = binding.tvAnswer1.text.toString()
            }
            cards[1].setOnClickListener {
                onlyOneCardViewToggle(cards[1])
                userAnswer = "2"
                answerString = binding.tvAnswer2.text.toString()
            }
            cards[2].setOnClickListener {
                onlyOneCardViewToggle(cards[2])
                userAnswer = "3"
                answerString = binding.tvAnswer3.text.toString()
            }
            cards[3].setOnClickListener {
                onlyOneCardViewToggle(cards[3])
                userAnswer = "4"
                answerString = binding.tvAnswer4.text.toString()
            }
        }

        return binding.root
    }

    // 카드뷰 한 개만 선택
    private fun onlyOneCardViewToggle(cardView:MaterialCardView) {
        for(i in cards) {
            if (i.isChecked) {
                i.toggle()
            }
        }
        cardView.toggle()
    }

    // 앱바의 채점 버튼 클릭
    override fun onAnswerSubmit() {
        if(userAnswer == null) {
            //Toast.makeText(context, "답을 선택 해주세요.", Toast.LENGTH_SHORT).show()
            DesignToast.makeText(requireContext(), DesignToast.LayoutDesign.ERROR, "답을 선택해주세요.").show()
        } else {
            try {
                val bundle = Bundle().apply {
                    putString("userAnswer", userAnswer)
                    putString("answer", answer)
                    putString("answerString", answerString)
                    putString("commentary", commentary)
                    putInt("quizId", quizId!!)
                    putInt("quizType", quizType!!)
                }
                Log.d("test","ua: ${userAnswer}, a: $answer, as: $answerString, c: $commentary, qi: $quizId, qt: $quizType")
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
                Log.e("MultipleChoiceQuizFragment", "onAnswerSubmit", e)
            }

        }
    }

    companion object {
        fun newInstance(response: RandomQuiz): MultipleChoiceQuizFragment {
            val args = Bundle()

            val fragment = MultipleChoiceQuizFragment()
            args.putInt("quizType", response.quizType)
            args.putInt("quizId", response.quizId)
            args.putString("contents", response.jsonContent)
            args.putBoolean("hasImg", response.hasImage)
            fragment.arguments = args
            return fragment
        }
    }
}