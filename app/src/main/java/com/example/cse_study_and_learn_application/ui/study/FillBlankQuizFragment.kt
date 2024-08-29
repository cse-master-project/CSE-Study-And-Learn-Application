package com.example.cse_study_and_learn_application.ui.study

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
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
import androidx.fragment.app.commit
import androidx.lifecycle.lifecycleScope
import com.example.cse_study_and_learn_application.R
import com.example.cse_study_and_learn_application.connector.ConnectorRepository
import com.example.cse_study_and_learn_application.databinding.FragmentFillBlankQuizBinding
import com.example.cse_study_and_learn_application.model.FillBlankQuizJsonContent
import com.example.cse_study_and_learn_application.model.RandomQuiz
import com.example.cse_study_and_learn_application.ui.login.AccountAssistant
import com.example.cse_study_and_learn_application.ui.other.DesignToast
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

    private lateinit var binding: FragmentFillBlankQuizBinding
    private var loadNextQuiz: (() -> Unit)? = null

    private var userAnswer: ArrayList<String> = arrayListOf("", "", "")
    private lateinit var answer: List<String>
    private lateinit var commentary: String
    private var quizId: Int? = null
    private var quizType: Int? = null
    private var image: Bitmap? = null

    private var isAnswerSubmitted = false
    private var bottomSheet: BottomSheetGradingFragment? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI(view)
        val editTexts: MutableList<EditText> = mutableListOf(
            binding.etAnswer1,
            binding.etAnswer2,
            binding.etAnswer3
        )

        for (et in editTexts) {
            et.setOnTouchListenerForKeyboard()
            et.setOnEditorActionListener { _, actionId, event ->
                if (actionId == EditorInfo.IME_ACTION_DONE ||
                    (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)
                ) {
                    onAnswerSubmit()
                    true
                } else {
                    false
                }
            }
        }
    }

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
            commentary = content.commentary

            binding.tvQuizText.text = content.quiz

            if (hasImg) {
                binding.ivQuizImage.visibility = View.VISIBLE
                loadImage()
            }
            setupAnswerFields()
        }

        return binding.root
    }

    private fun setupAnswerFields() {
        val editTexts = listOf(binding.etAnswer1, binding.etAnswer2, binding.etAnswer3)

        editTexts.forEachIndexed { index, editText ->
            if (index < answer.size) {
                editText.visibility = View.VISIBLE
                editText.setOnTouchListenerForKeyboard()
                editText.setOnEditorActionListener { _, actionId, event ->
                    if (actionId == EditorInfo.IME_ACTION_DONE ||
                        (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)
                    ) {
                        onAnswerSubmit()
                        true
                    } else {
                        false
                    }
                }
            } else {
                editText.visibility = View.GONE
            }
        }
    }

    private fun loadImage() {
        lifecycleScope.launch {
            try {
                val response = ConnectorRepository().getQuizImage(AccountAssistant.getServerAccessToken(requireContext()), quizId!!)
                val decoded = Base64.decode(response.string(), Base64.DEFAULT)
                image = BitmapFactory.decodeByteArray(decoded, 0, decoded.size)
                binding.ivQuizImage.setImageBitmap(image)
            } catch (e: Exception) {
                Log.e("FillBlankQuizFragment", "get Image Failure", e)
            }
        }
    }

    override fun onAnswerSubmit() {
        if (!isAnswerSubmitted) {
            userAnswer.clear()
            for (i in 0 until answer.size) {
                when (i) {
                    0 -> userAnswer.add(binding.etAnswer1.text.toString())
                    1 -> userAnswer.add(binding.etAnswer2.text.toString())
                    2 -> userAnswer.add(binding.etAnswer3.text.toString())
                }
            }
        }

        if (userAnswer.any { it.isBlank() }) {
            DesignToast.makeText(requireContext(), DesignToast.LayoutDesign.ERROR, "모든 빈칸을 채워주세요.").show()
        } else {
            try {
                if (!isAnswerSubmitted) {
                    isAnswerSubmitted = true
                    bottomSheet = BottomSheetGradingFragment.newInstance(
                        quizId = quizId!!,
                        userAnswer = userAnswer.joinToString(","),
                        answer = answer.joinToString(","),
                        answerString = answer.mapIndexed { index, s -> "${index + 1}번 답: $s" }.joinToString("\n"),
                        commentary = commentary,
                        quizType = quizType!!
                    )
                    bottomSheet?.setOnNextQuizListener {
                        loadNextQuiz?.invoke()
                    }
                    disableEditTexts()
                }
                bottomSheet?.show(parentFragmentManager, bottomSheet?.tag)
            } catch (e: Exception) {
                Log.e("FillBlankQuizFragment", "onAnswerSubmit", e)
            }
        }
    }

    fun setLoadNextQuizListener(listener: () -> Unit) {
        loadNextQuiz = listener
    }

    private fun disableEditTexts() {
        binding.etAnswer1.isEnabled = false
        binding.etAnswer2.isEnabled = false
        binding.etAnswer3.isEnabled = false
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