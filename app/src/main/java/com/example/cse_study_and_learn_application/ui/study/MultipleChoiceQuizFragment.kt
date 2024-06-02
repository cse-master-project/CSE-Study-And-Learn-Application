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
import com.example.cse_study_and_learn_application.databinding.FragmentMultipleChoiceQuizBinding
import com.example.cse_study_and_learn_application.model.MultipleChoiceQuizJsonContent
import com.google.android.material.card.MaterialCardView
import com.google.gson.Gson

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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMultipleChoiceQuizBinding.inflate(inflater)

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
            Toast.makeText(context, "답을 선택 해주세요.", Toast.LENGTH_SHORT).show()
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
        fun newInstance(contents: String, hasImg: Boolean, quizId: Int, quizType: Int): MultipleChoiceQuizFragment {
            val args = Bundle()

            val fragment = MultipleChoiceQuizFragment()
            args.putInt("quizType", quizType)
            args.putInt("quizId", quizId)
            args.putString("contents", contents)
            args.putBoolean("hasImg", hasImg)
            fragment.arguments = args
            return fragment
        }
    }
}