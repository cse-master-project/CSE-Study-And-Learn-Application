package com.cslu.cse_study_and_learn_application.ui.study

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
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.cslu.cse_study_and_learn_application.R
import com.cslu.cse_study_and_learn_application.connector.ConnectorRepository
import com.cslu.cse_study_and_learn_application.databinding.FragmentMultipleChoiceQuizBinding
import com.cslu.cse_study_and_learn_application.model.MultipleChoiceQuizJsonContent
// import com.example.cse_study_and_learn_application.model.QuizResponse
import com.cslu.cse_study_and_learn_application.model.RandomQuiz
import com.cslu.cse_study_and_learn_application.ui.login.AccountAssistant
import com.cslu.cse_study_and_learn_application.ui.other.DesignToast
import com.google.android.material.card.MaterialCardView
import com.google.gson.Gson
import kotlinx.coroutines.launch

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
    private var explanationDialog: BottomSheetGradingFragment? = null

    private var loadNextQuiz: (() -> Unit)? = null

    fun setLoadNextQuizListener(listener: () -> Unit) {
        loadNextQuiz = {
            // 카드 색상 초기화
            cards.forEach { card ->
                card.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.card_background_color))
            }
            isAnswerSubmitted = false
            userAnswer = null
            (activity as? QuizActivity)?.setGradingButtonClickListener { onAnswerSubmit() }
            listener.invoke()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMultipleChoiceQuizBinding.inflate(inflater)
        (activity as? QuizActivity)?.setExplanationButtonEnabled(false)
        (activity as? QuizActivity)?.setGradingButtonClickListener { onAnswerSubmit() }

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

    fun showExplanationDialog() {
        if (isAnswerSubmitted && explanationDialog != null) {
            explanationDialog?.show(parentFragmentManager, explanationDialog?.tag)
        }
    }

    // 카드뷰 한 개만 선택
    private fun updateCardSelection(selectedCard:MaterialCardView, cards: List<MaterialCardView>) {
        cards.forEachIndexed { index, card ->
            val isSelected = card == selectedCard
            card.isChecked = isSelected

            // 번호 TextView 찾기
            val numberTextView = card.findViewById<TextView>(getNumberTextViewId(index))

            // 선택 여부에 따라 색상 변경
            if (isSelected) {
                numberTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.selected_number_color))
                numberTextView.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_button_number_rounded_selected)
            } else {
                numberTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                numberTextView.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_button_number_rounded)
            }
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

    private fun getNumberTextViewId(index: Int): Int {
        return when (index) {
            0 -> R.id.btn_1
            1 -> R.id.btn_2
            2 -> R.id.btn_3
            3 -> R.id.btn_4
            else -> throw IllegalArgumentException("Invalid index")
        }
    }

    private fun updateCardColors() {
        cards.forEachIndexed { index, card ->
            val isCorrectAnswer = (index + 1).toString() == answer
            val isUserAnswer = (index + 1).toString() == userAnswer
            val numberTextView = card.findViewById<TextView>(getNumberTextViewId(index))

            when {
                isCorrectAnswer -> {
                    card.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.correct_card_background))
                    numberTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                    numberTextView.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_button_number_rounded_correct)
                }
                isUserAnswer && !isCorrectAnswer -> {
                    card.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.incorrect_card_background))
                    numberTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                    numberTextView.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_button_number_rounded_wrong)
                }
                else -> {
                    card.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.card_background_color))
                    numberTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                    numberTextView.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_button_number_rounded)
                }
            }
        }
    }

    private fun updateButtonText() {
        (activity as? QuizActivity)?.setGradingButtonText("다음 문제")
    }

    // 채점 버튼 클릭
    override fun onAnswerSubmit() {
        if (userAnswer == null) {
            DesignToast.makeText(requireContext(), DesignToast.LayoutDesign.INFO, "답을 선택해주세요.").show()
        } else {
            try {
                if (!isAnswerSubmitted) {
                    isAnswerSubmitted = true
                    updateCardColors() // 카드 색상 업데이트
                    (activity as? QuizActivity)?.setExplanationButtonEnabled(true)
                    (activity as? QuizActivity)?.setGradingButtonText("다음 문제")
                    (activity as? QuizActivity)?.setGradingButtonClickListener { loadNextQuiz?.invoke() }

                    val isCorrect = userAnswer == answer

                    (activity as? QuizActivity)?.resultSubmit(quizId!!, isCorrect) // 결과 제출



                    explanationDialog = BottomSheetGradingFragment.newInstance(
                        isCorrect = true,
                        commentary = commentary,
                    )
                    updateButtonText() // 버튼 텍스트 업데이트
                    explanationDialog?.setOnNextQuizListener {
                        (activity as? QuizActivity)?.setExplanationButtonEnabled(false)
                    }

                }
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