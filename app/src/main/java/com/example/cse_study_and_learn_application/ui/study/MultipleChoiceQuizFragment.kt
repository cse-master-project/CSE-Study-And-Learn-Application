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
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.children
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

    private var isAnswerSubmitted = false
    private var bottomSheet: BottomSheetGradingFragment? = null

    private var loadNextQuiz: (() -> Unit)? = null

    fun setLoadNextQuizListener(listener: () -> Unit) {
        loadNextQuiz = listener
    }

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

            cards.forEachIndexed { index, card ->
                card.setOnClickListener {
                    if (!isAnswerSubmitted) {
                        updateCardSelection(card, cards)
                        userAnswer = (index + 1).toString()
                        val textView = cards[index].findViewById<TextView>(getTextViewId(index))
                        answerString = textView.text.toString()
                    }
                }
            }
        }

        return binding.root
    }

    // 카드뷰 한 개만 선택
    private fun updateCardSelection(selectedCard:MaterialCardView, cards: List<MaterialCardView>) {
        cards.forEach { card ->
            card.isChecked = card == selectedCard
        }
    }

    // 인덱스를 넣으면 카드뷰 내의 텍스트 뷰 ID 반환
    private fun getTextViewId(index: Int): Int {
        return when (index) {
            0 -> R.id.tv_answer_1
            1 -> R.id.tv_answer_2
            2 -> R.id.tv_answer_3
            3 -> R.id.tv_answer_4
            else -> throw IllegalArgumentException("Invalid index")
        }
    }



    // 앱바의 채점 버튼 클릭
    override fun onAnswerSubmit() {
        if (userAnswer == null) {
            DesignToast.makeText(requireContext(), DesignToast.LayoutDesign.ERROR, "답을 선택해주세요.").show()
        } else {
            try {
                if (!isAnswerSubmitted) {
                    isAnswerSubmitted = true
                    bottomSheet = BottomSheetGradingFragment.newInstance(
                        quizId = quizId!!,
                        userAnswer = userAnswer!!,
                        answer = answer,
                        answerString = answerString,
                        commentary = commentary,
                        quizType = quizType!!
                    )
                    bottomSheet?.setOnNextQuizListener {
                        loadNextQuiz?.invoke()
                    }
                }
                bottomSheet?.show(parentFragmentManager, bottomSheet?.tag)
            } catch (e: Exception) {
                Log.e("MultipleChoiceQuizFragment", "onAnswerSubmit", e)
            }
        }
    }

    companion object {
        fun newInstance(response: RandomQuiz): MultipleChoiceQuizFragment {
            val fragment = MultipleChoiceQuizFragment()
            fragment.arguments = Bundle().apply {
                putInt("quizType", response.quizType)
                putInt("quizId", response.quizId)
                putString("contents", response.jsonContent)
                putBoolean("hasImg", response.hasImage)
            }
            return fragment
        }
    }
}