package com.example.cse_study_and_learn_application.ui.study

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.commit
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cse_study_and_learn_application.R
import com.example.cse_study_and_learn_application.connector.ConnectorRepository
import com.example.cse_study_and_learn_application.databinding.FragmentMatingQuizBinding
import com.example.cse_study_and_learn_application.model.MatingQuizJsonContent
import com.example.cse_study_and_learn_application.model.RandomQuiz
import com.example.cse_study_and_learn_application.ui.login.AccountAssistant
import com.example.cse_study_and_learn_application.ui.other.DesignToast
import com.example.cse_study_and_learn_application.utils.Lg
import com.example.cse_study_and_learn_application.utils.QuizType
import com.google.gson.Gson
import kotlinx.coroutines.launch

/**
 * Mating quiz fragment
 *
 * @constructor Create empty Mating quiz fragment
 * author JYH, KJY
 * @since 2024-03-28
 *
 * 2024-06-08 선잇기 수정
 */
class MatingQuizFragment : Fragment(), OnAnswerSubmitListener {

    private lateinit var binding: FragmentMatingQuizBinding
    private var loadNextQuiz: (() -> Unit)? = null

    private var quizId = -1
    private var hasImg = false
    private lateinit var content: MatingQuizJsonContent

    // 선택한 버튼의 위치를 저장할 변수
    private var leftSelectedPosition: Int? = null
    private var rightSelectedPosition: Int? = null

    // 선이 이어진 상태 저장
    // 정답과 오답 선을 저장하는 리스트
    private val gradedLines: MutableList<Pair<Pair<Int, Int>, Boolean>> = mutableListOf()
    // 본인이 선택한 선을 저장하는 리스트
    private val connectedPairs: MutableSet<Pair<Int, Int>> = mutableSetOf()

    private var isAnswerSubmitted = false
    private var explanationDialog: BottomSheetGradingFragment? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMatingQuizBinding.inflate(inflater)
        setupUI()
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupUI() {
        (activity as? QuizActivity)?.apply {
            setExplanationButtonEnabled(false)
            setGradingButtonText("채점하기")
            setGradingButtonClickListener { onAnswerSubmit() }
        }

        requireArguments().let {
            quizId = it.getInt("quizId")
            hasImg = it.getBoolean("hasImg")
            val jsonString = it.getString("contents", "")
            content = Gson().fromJson(jsonString, MatingQuizJsonContent::class.java)

            // 왼쪽 RecyclerView (숫자가 증가)
            val leftAdapter = MatingOptionAdapter(content.leftOption, true)
            binding.leftOptionInfoRecyclerview.adapter = leftAdapter
            binding.leftOptionInfoRecyclerview.layoutManager = LinearLayoutManager(context)

            // 오른쪽 RecyclerView (알파벳이 증가)
            val rightAdapter = MatingOptionAdapter(content.rightOption, false)
            binding.rightOptionInfoRecyclerview.adapter = rightAdapter
            binding.rightOptionInfoRecyclerview.layoutManager = LinearLayoutManager(context)

            binding.tvQuizText.text = content.quiz

            if (hasImg) {
                binding.ivQuizImage.visibility = View.VISIBLE
                loadImage()
            }

            setupRecyclerViews()
        }

        binding.btnMyAnswer.setOnTouchListener { _, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    showUserLinesOnly()
                    true
                }
                MotionEvent.ACTION_UP -> {
                    showGradedLinesOnly()
                    true
                }
                else -> false
            }
        }
    }

    private fun loadImage() {
        lifecycleScope.launch {
            try {
                val response = ConnectorRepository().getQuizImage(AccountAssistant.getServerAccessToken(requireContext()), quizId)
                val decoded = Base64.decode(response.string(), Base64.DEFAULT)
                val image = BitmapFactory.decodeByteArray(decoded, 0, decoded.size)
                binding.ivQuizImage.setImageBitmap(image)
            } catch (e: Exception) {
                Log.e("MatingQuizFragment", "get Image Failure", e)
            }
        }
    }

    private fun setupRecyclerViews() {
        val leftAdapter = MatingRecyclerViewAdapter(content.leftOption, true,
            object : MatingRecyclerViewAdapter.OnItemClickListener {
                override fun onItemClick(position: Int, option: String?) {
                    if (!isAnswerSubmitted) {
                        leftSelectedPosition = position
                        checkAndDrawLine()
                    }
                }
            })

        val rightAdapter = MatingRecyclerViewAdapter(content.rightOption, false,
            object : MatingRecyclerViewAdapter.OnItemClickListener {
                override fun onItemClick(position: Int, option: String?) {
                    if (!isAnswerSubmitted) {
                        rightSelectedPosition = position
                        checkAndDrawLine()
                    }
                }
            })

        binding.leftRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.leftRecyclerView.adapter = leftAdapter

        binding.rightRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.rightRecyclerView.adapter = rightAdapter
    }

    private fun checkAndDrawLine() {
        if (leftSelectedPosition != null && rightSelectedPosition != null) {
            val selectedPair = Pair(leftSelectedPosition!!, rightSelectedPosition!!)

            if (connectedPairs.contains(selectedPair)) {
                connectedPairs.remove(selectedPair)
                removeLineForSelectedPair(selectedPair)
            } else {
                connectedPairs.add(selectedPair)
                drawLineForSelectedPair(selectedPair)
            }

            leftSelectedPosition = null
            rightSelectedPosition = null
        }
    }


    // 선잇기
    private fun drawLineForSelectedPair(pair: Pair<Int, Int>) {
        val leftViewHolder = binding.leftRecyclerView.findViewHolderForAdapterPosition(pair.first)
        val rightViewHolder = binding.rightRecyclerView.findViewHolderForAdapterPosition(pair.second)

        if (leftViewHolder != null && rightViewHolder != null) {
            val leftViewLocation = IntArray(2)
            leftViewHolder.itemView.getLocationOnScreen(leftViewLocation)
            val startX = leftViewLocation[0] + leftViewHolder.itemView.width
            val startY = leftViewLocation[1] + leftViewHolder.itemView.height / 2

            val rightViewLocation = IntArray(2)
            rightViewHolder.itemView.getLocationOnScreen(rightViewLocation)
            val endX = rightViewLocation[0]
            val endY = rightViewLocation[1] + rightViewHolder.itemView.height / 2

            val lineDrawingViewLocation = IntArray(2)
            binding.lineDrawingView.getLocationOnScreen(lineDrawingViewLocation)

            val startXInView = startX - lineDrawingViewLocation[0]
            val startYInView = startY - lineDrawingViewLocation[1]
            val endXInView = endX - lineDrawingViewLocation[0]
            val endYInView = endY - lineDrawingViewLocation[1]

            binding.lineDrawingView.addLine(startXInView.toFloat(), startYInView.toFloat(), endXInView.toFloat(), endYInView.toFloat(), ContextCompat.getColor(requireContext(), R.color.light_blue_600))
            // Log.d("test", "Screen coordinates: $startX, $startY, $endX, $endY")
            // Log.d("test", "View coordinates: $startXInView, $startYInView, $endXInView, $endYInView")
        }
    }

    // 선 제거
    private fun removeLineForSelectedPair(pair: Pair<Int, Int>) {
        val leftViewHolder = binding.leftRecyclerView.findViewHolderForAdapterPosition(pair.first)
        val rightViewHolder = binding.rightRecyclerView.findViewHolderForAdapterPosition(pair.second)

        if (leftViewHolder != null && rightViewHolder != null) {
            val leftViewLocation = IntArray(2)
            leftViewHolder.itemView.getLocationOnScreen(leftViewLocation)
            val startX = leftViewLocation[0] + leftViewHolder.itemView.width
            val startY = leftViewLocation[1] + leftViewHolder.itemView.height / 2

            val rightViewLocation = IntArray(2)
            rightViewHolder.itemView.getLocationOnScreen(rightViewLocation)
            val endX = rightViewLocation[0]
            val endY = rightViewLocation[1] + rightViewHolder.itemView.height / 2

            val lineDrawingViewLocation = IntArray(2)
            binding.lineDrawingView.getLocationOnScreen(lineDrawingViewLocation)

            val startXInView = startX - lineDrawingViewLocation[0]
            val startYInView = startY - lineDrawingViewLocation[1]
            val endXInView = endX - lineDrawingViewLocation[0]
            val endYInView = endY - lineDrawingViewLocation[1]

            binding.lineDrawingView.removeLine(startXInView.toFloat(), startYInView.toFloat(), endXInView.toFloat(), endYInView.toFloat(), ContextCompat.getColor(requireContext(), R.color.light_blue_600))
            // Log.d("test", "Removing line: Screen coordinates: $startX, $startY, $endX, $endY")
            // Log.d("test", "Removing line: View coordinates: $startXInView, $startYInView, $endXInView, $endYInView")
        }
    }

    private fun getAnswerAsIndexList(): List<Pair<Int, Int>> {
        // content.answer 배열에서 "인덱스t인덱스" 형식의 요소를 파싱하여 정답 리스트로 변환
        return content.answer.map { answer ->
            val parts = answer.split("t")
            Pair(parts[0].toInt(), parts[1].toInt())
        }
    }


    private fun convertSelectedAnswersToIndex(): List<Pair<Int, Int>> {
        // 사용자가 선택한 선을 인덱스 쌍으로 변환
        return connectedPairs.toList()
    }


    @SuppressLint("ClickableViewAccessibility")
    override fun onAnswerSubmit() {
        if (connectedPairs.isEmpty()) {
            DesignToast.makeText(requireContext(), DesignToast.LayoutDesign.INFO, "답을 선택해 주세요.").show()
        } else {
            try {
                if (!isAnswerSubmitted) {
                    isAnswerSubmitted = true
                    (activity as? QuizActivity)?.setExplanationButtonEnabled(true)
                    (activity as? QuizActivity)?.setGradingButtonText("다음 문제")
                    (activity as? QuizActivity)?.setGradingButtonClickListener { loadNextQuiz?.invoke() }

                    val correctAnswers = getAnswerAsIndexList()
                    val isCorrect = gradeAnswers(connectedPairs.toList(), correctAnswers)
                    (activity as? QuizActivity)?.resultSubmit(quizId, isCorrect)

                    showGradedLinesOnly()
                    updateButtonText()

                    explanationDialog = BottomSheetGradingFragment.newInstance(
                        isCorrect = isCorrect,
                        commentary = content.commentary,
                    )
                    explanationDialog?.setOnNextQuizListener {
                        (activity as? QuizActivity)?.setExplanationButtonEnabled(false)
                    }

                    binding.btnMyAnswer.visibility = View.VISIBLE
                }
            } catch (e: Exception) {
                Log.e("MatingQuizFragment", "onAnswerSubmit", e)
            }
        }
    }

    private fun updateButtonText() {
        (activity as? QuizActivity)?.setGradingButtonText("다음 문제")
    }

    fun showExplanationDialog() {
        if (isAnswerSubmitted && explanationDialog != null) {
            explanationDialog?.show(parentFragmentManager, explanationDialog?.tag)
        }
    }

    private fun gradeAnswers(selectedAnswers: List<Pair<Int, Int>>, correctAnswers: List<Pair<Int, Int>>): Boolean {
        gradedLines.clear()
        var allCorrect = true

        // 정답만 gradedLines에 추가
        for (pair in correctAnswers) {
            val isSelected = selectedAnswers.contains(pair)
            gradedLines.add(Pair(pair, isSelected))
            if (!isSelected) allCorrect = false
        }

        return allCorrect
    }

    // 사용자 선을 숨기고 채점된 선을 보여줌
    private fun showGradedLinesOnly() {
        binding.lineDrawingView.clearLines() // 기존 선 지우기

        for ((pair, isSelected) in gradedLines) {
            val color = if (isSelected) {
                ContextCompat.getColor(requireContext(), R.color.correct_card_number_background)
            } else {
                ContextCompat.getColor(requireContext(), R.color.incorrect_true_false)
            }
            drawLineForResult(pair, color)
        }
    }


    // 채점된 선을 숨기고 사용자의 원래 선을 보여줌
    private fun showUserLinesOnly() {
        binding.lineDrawingView.clearLines() // 모든 선 제거

        // 사용자가 선택한 선을 다시 보여줌
        for (pair in connectedPairs) {
            drawLineForSelectedPair(pair)
        }
    }

    private fun drawLineForResult(pair: Pair<Int, Int>, color: Int) {
        val leftViewHolder = binding.leftRecyclerView.findViewHolderForAdapterPosition(pair.first)
        val rightViewHolder = binding.rightRecyclerView.findViewHolderForAdapterPosition(pair.second)

        if (leftViewHolder != null && rightViewHolder != null) {
            val leftViewLocation = IntArray(2)
            leftViewHolder.itemView.getLocationOnScreen(leftViewLocation)
            val startX = leftViewLocation[0] + leftViewHolder.itemView.width
            val startY = leftViewLocation[1] + leftViewHolder.itemView.height / 2

            val rightViewLocation = IntArray(2)
            rightViewHolder.itemView.getLocationOnScreen(rightViewLocation)
            val endX = rightViewLocation[0]
            val endY = rightViewLocation[1] + rightViewHolder.itemView.height / 2

            val lineDrawingViewLocation = IntArray(2)
            binding.lineDrawingView.getLocationOnScreen(lineDrawingViewLocation)

            val startXInView = startX - lineDrawingViewLocation[0]
            val startYInView = startY - lineDrawingViewLocation[1]
            val endXInView = endX - lineDrawingViewLocation[0]
            val endYInView = endY - lineDrawingViewLocation[1]

            binding.lineDrawingView.addLine(startXInView.toFloat(), startYInView.toFloat(), endXInView.toFloat(), endYInView.toFloat(), color)

        }
    }


    fun setLoadNextQuizListener(listener: () -> Unit) {
        loadNextQuiz = {
            isAnswerSubmitted = false
            connectedPairs.clear()
            gradedLines.clear()
            binding.lineDrawingView.clearLines()
            binding.btnMyAnswer.visibility = View.GONE

            (activity as? QuizActivity)?.setGradingButtonText("채점하기")
            (activity as? QuizActivity)?.setGradingButtonClickListener { onAnswerSubmit() }
            (activity as? QuizActivity)?.setExplanationButtonEnabled(false)

            setupUI()
            listener.invoke()
        }
    }

    companion object {
        fun newInstance(response: RandomQuiz): MatingQuizFragment {
            val args = Bundle()
            val fragment = MatingQuizFragment()
            args.putInt("quizId", response.quizId)
            args.putString("contents", response.jsonContent)
            args.putBoolean("hasImg", response.hasImage)
            fragment.arguments = args
            return fragment
        }
    }
}