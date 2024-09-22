package com.example.cse_study_and_learn_application.ui.study

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Base64
import android.util.Log
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.commit
import androidx.lifecycle.lifecycleScope
import com.example.cse_study_and_learn_application.R
import com.example.cse_study_and_learn_application.connector.ConnectorRepository
import com.example.cse_study_and_learn_application.databinding.FragmentShortAnswerQuizBinding
import com.example.cse_study_and_learn_application.model.RandomQuiz
import com.example.cse_study_and_learn_application.model.ShortAnswerQuizJsonContent
import com.example.cse_study_and_learn_application.ui.login.AccountAssistant
import com.example.cse_study_and_learn_application.ui.other.DesignToast
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
    private var loadNextQuiz: (() -> Unit)? = null

    private var userAnswer: String? = null
    private lateinit var answer: List<String>
    private lateinit var commentary: String
    private var quizId: Int? = null
    private var quizType: Int? = null
    private var image: Bitmap? = null
    private lateinit var originalQuizText: String

    private var isAnswerSubmitted = false
    private var explanationDialog: BottomSheetGradingFragment? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI(view)
        val editTextAnswer = view.findViewById<EditText>(R.id.et_answer)
        editTextAnswer.setOnTouchListenerForKeyboard()
        editTextAnswer.setOnEditorActionListener { v, i, e ->
            if (i == EditorInfo.IME_ACTION_DONE ||
                (e != null && e.keyCode == KeyEvent.KEYCODE_ENTER && e.action == KeyEvent.ACTION_DOWN)
                ) {
                onAnswerSubmit()
                true
            } else {
                false
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentShortAnswerQuizBinding.inflate(inflater)
        (activity as? QuizActivity)?.apply {
            setExplanationButtonEnabled(false)
            setGradingButtonText("정답 확인")
            setGradingButtonClickListener { onAnswerSubmit() }
        }

        requireArguments().let {
            quizId = it.getInt("quizId")
            quizType = it.getInt("quizType")
            val hasImg = it.getBoolean("hasImg")
            val jsonString = it.getString("contents")

            val content = Gson().fromJson(jsonString, ShortAnswerQuizJsonContent::class.java)
            originalQuizText = content.quiz
            binding.tvQuizText.text = originalQuizText
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
                        Log.e("ShortAnswerQuizFragment", "get Image Failure", e)
                    }
                }
            }
        }

        binding.etAnswer.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (!isAnswerSubmitted) {
                    userAnswer = s.toString()
                } else {
                    // 이미 답변을 제출했다면 입력을 무시
                    binding.etAnswer.removeTextChangedListener(this)
                    binding.etAnswer.setText(userAnswer)
                    binding.etAnswer.setSelection(userAnswer?.length ?: 0)
                    binding.etAnswer.addTextChangedListener(this)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        return binding.root
    }

    // 앱바의 채점 버튼 클릭
    override fun onAnswerSubmit() {
        userAnswer = binding.etAnswer.text.toString()
        if (userAnswer.isNullOrEmpty()) {
            DesignToast.makeText(requireContext(), DesignToast.LayoutDesign.INFO, "답을 입력해주세요.").show()
        } else {
            try {
                if (!isAnswerSubmitted) {
                    isAnswerSubmitted = true
                    (activity as? QuizActivity)?.setExplanationButtonEnabled(true)
                    (activity as? QuizActivity)?.setGradingButtonText("다음 문제")
                    (activity as? QuizActivity)?.setGradingButtonClickListener { loadNextQuiz?.invoke() }

                    val isCorrect = answer.any { it.trim().equals(userAnswer?.trim(), ignoreCase = true) }
                    (activity as? QuizActivity)?.resultSubmit(quizId!!, isCorrect) // 결과 제출

                    updateInputTextColor(isCorrect)
                    updateQuizText()

                    updateButtonText() // 버튼 텍스트 업데이트

                    // 여기서 explanationDialog를 초기화하고 표시합니다
                    explanationDialog = BottomSheetGradingFragment.newInstance(
                        isCorrect = isCorrect,
                        commentary = commentary
                    )
                    explanationDialog?.setOnNextQuizListener {
                        (activity as? QuizActivity)?.setExplanationButtonEnabled(false)
                    }
                    // explanationDialog?.show(parentFragmentManager, explanationDialog?.tag)
                }
            } catch (e: Exception) {
                Log.e("ShortAnswerQuizFragment", "onAnswerSubmit", e)
            }
        }
    }

    private fun updateQuizText() {
        val updatedText = originalQuizText.replace("(   )", "($answer)")
        binding.tvQuizText.text = updatedText
    }

    private fun updateButtonText() {
        (activity as? QuizActivity)?.setGradingButtonText("다음 문제")
    }

    fun showExplanationDialog() {
        if (isAnswerSubmitted) {
            if (explanationDialog == null) {
                val isCorrect = answer.any { it.trim().equals(userAnswer?.trim(), ignoreCase = true) }
                explanationDialog = BottomSheetGradingFragment.newInstance(
                    isCorrect = isCorrect,
                    commentary = commentary
                )
                explanationDialog?.setOnNextQuizListener {
                    (activity as? QuizActivity)?.setExplanationButtonEnabled(false)
                }
            }
            explanationDialog?.show(parentFragmentManager, explanationDialog?.tag)
        } else {
            DesignToast.makeText(requireContext(), DesignToast.LayoutDesign.INFO, "먼저 정답을 확인해주세요.").show()
        }
    }

    private fun updateInputTextColor(isCorrect: Boolean) {
        val color = if (isCorrect) {
            ContextCompat.getColor(requireContext(), R.color.correct_card_background)
        } else {
            ContextCompat.getColor(requireContext(), R.color.incorrect_card_background)
        }

        binding.etAnswer.backgroundTintList = ColorStateList.valueOf(color)
    }

    fun setLoadNextQuizListener(listener: () -> Unit) {
        loadNextQuiz = {
            // 상태 초기화
            isAnswerSubmitted = false
            userAnswer = null
            binding.etAnswer.setText("")
            binding.etAnswer.isEnabled = true
            binding.etAnswer.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.default_input_text_background))
            (activity as? QuizActivity)?.setGradingButtonText("정답 확인")
            (activity as? QuizActivity)?.setGradingButtonClickListener { onAnswerSubmit() }
            (activity as? QuizActivity)?.setExplanationButtonEnabled(false)
            listener.invoke()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupUI(view: View) {
        if(view !is EditText) {
            view.setOnTouchListener { _, _ ->
                hideKeyboard()
                false
            }
        }

        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                val innerView = view.getChildAt(i)
                setupUI(innerView)
            }
        }
    }

    private fun hideKeyboard() {
        val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(requireView().windowToken, 0)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun EditText.setOnTouchListenerForKeyboard() {
        setOnTouchListener { v, event ->
            v.onTouchEvent(event)
            when (event.action) {
                MotionEvent.ACTION_UP -> {
                    if (v is EditText) {
                        v.requestFocus()
                        val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        imm.showSoftInput(v, InputMethodManager.SHOW_IMPLICIT)
                    }
                }
            }
            true
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