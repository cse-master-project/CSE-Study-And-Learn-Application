package com.example.cse_study_and_learn_application.ui.study

import android.annotation.SuppressLint
import android.os.Bundle
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
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cse_study_and_learn_application.R
import com.example.cse_study_and_learn_application.databinding.FragmentMatingQuizBinding
import com.example.cse_study_and_learn_application.model.MatingQuizJsonContent
import com.example.cse_study_and_learn_application.model.RandomQuiz
import com.example.cse_study_and_learn_application.ui.other.DesignToast
import com.example.cse_study_and_learn_application.utils.Lg
import com.example.cse_study_and_learn_application.utils.QuizType
import com.google.gson.Gson

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
    private lateinit var jsonString: String
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
    private var bottomSheet: BottomSheetGradingFragment? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMatingQuizBinding.inflate(inflater)

        requireArguments().let {
            quizId = it.getInt("quizId")
            hasImg = it.getBoolean("hasImg")
            jsonString = it.getString("contents", "")
            content = Gson().fromJson(jsonString, MatingQuizJsonContent::class.java)

            if (hasImg) {
                binding.ivQuizImage.visibility = View.VISIBLE
            }

            // 선 잇기 보기 표시
            val quizWithOptions = StringBuilder()
            quizWithOptions.append(content.quiz).append("\n\n")

            quizWithOptions.append("보기:\n")
            content.leftOption.forEachIndexed { idx, option ->
                quizWithOptions.append(idx).append(": ").append(option).append(", ")
            }
            quizWithOptions.append("\n")

            content.rightOption.forEachIndexed { idx, option ->
                val charIdx = ('A' + idx)
                quizWithOptions.append(charIdx).append(": ").append(option).append(", ")
            }

            binding.tvQuizText.text = quizWithOptions.toString()

            // 선 잇기 버튼 리사이클러뷰 설정
            val leftAdapter = MatingRecyclerViewAdapter(content.leftOption, true, object: MatingRecyclerViewAdapter.OnItemClickListener {
                override fun onItemClick(position: Int, option: String?) {
                    leftSelectedPosition = position
                    checkAndDrawLine()
                }
            })

            val rightAdapter = MatingRecyclerViewAdapter(content.rightOption, false, object: MatingRecyclerViewAdapter.OnItemClickListener {
                override fun onItemClick(position: Int, option: String?) {
                    rightSelectedPosition = position
                    checkAndDrawLine()
                }
            })

            binding.leftRecyclerView.layoutManager = LinearLayoutManager(context)
            binding.leftRecyclerView.adapter = leftAdapter

            binding.rightRecyclerView.layoutManager = LinearLayoutManager(context)
            binding.rightRecyclerView.adapter = rightAdapter
        }

        val leftAdapter = MatingRecyclerViewAdapter(content.leftOption, true, object: MatingRecyclerViewAdapter.OnItemClickListener {
            override fun onItemClick(position: Int, option: String?) {
                if (!isAnswerSubmitted) {
                    leftSelectedPosition = position
                    checkAndDrawLine()
                }
            }
        })

        val rightAdapter = MatingRecyclerViewAdapter(content.rightOption, false, object: MatingRecyclerViewAdapter.OnItemClickListener {
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

        return binding.root
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
            val leftIndex = parts[0].toInt() // 't' 앞의 왼쪽 인덱스
            val rightIndex = parts[1].toInt() // 't' 뒤의 오른쪽 인덱스
            Pair(leftIndex, rightIndex)
        }
    }


    private fun convertSelectedAnswersToIndex(): List<Pair<Int, Int>> {
        // 사용자가 선택한 선을 인덱스 쌍으로 변환
        return connectedPairs.toList()
    }


    @SuppressLint("ClickableViewAccessibility")
    override fun onAnswerSubmit() {

        // 사용자가 선택한 답을 인덱스로 변환
        val selectedAnswers = convertSelectedAnswersToIndex()

        // 정답을 인덱스 형태로 변환
        val correctAnswers = getAnswerAsIndexList()

        Log.d("test", selectedAnswers.toString())  // 사용자 선택
        Log.d("test", correctAnswers.toString())   // 정답 리스트
        Log.d("test", content.leftOption.toString())   // 정답 리스트
        Log.d("test", content.rightOption.toString())   // 정답 리스트


        if (selectedAnswers.isEmpty()) {
            DesignToast.makeText(requireContext(), DesignToast.LayoutDesign.INFO, "답을 선택해 주세요.").show()
        } else {
            try {
                if (!isAnswerSubmitted) {
                    isAnswerSubmitted = true

                    // 정답과 오답 비교
                    for (pair in selectedAnswers) {
                        if (correctAnswers.contains(pair)) {
                            drawLineForResult(pair, true)  // 정답인 경우 초록색
                        } else {
                            drawLineForResult(pair, false) // 오답인 경우 빨간색
                        }
                    }

                    showGradedLinesOnly()

                    bottomSheet = BottomSheetGradingFragment.newInstance(
                        isCorrect = true,
                        commentary = content.commentary,
                    )
                    bottomSheet?.setOnNextQuizListener {
                        loadNextQuiz?.invoke()
                    }
                }
                bottomSheet?.show(parentFragmentManager, bottomSheet?.tag)
            } catch (e: Exception) {
                Log.e("MatingQuizFragment", "onAnswerSubmit", e)
            }

            // "내 답 보기" 버튼 활성화
            val myAnswerBtn = requireActivity().findViewById<LinearLayout>(R.id.btn_my_answer)
            myAnswerBtn.visibility = View.VISIBLE

            // "내 답 보기" 버튼을 꾹 누르면 나의 답을 보여줌
            myAnswerBtn.setOnTouchListener { _, motionEvent ->
                when (motionEvent.action) {
                    MotionEvent.ACTION_DOWN -> {
                        // 채점된 선을 숨기고 내 답을 보여줌
                        showUserLinesOnly()
                        true
                    }
                    MotionEvent.ACTION_UP -> {
                        // 내 답을 숨기고 채점된 선을 다시 보여줌
                        showGradedLinesOnly()
                        true
                    }
                    else -> false
                }
            }

        }
    }

    // 사용자 선을 숨기고 채점된 선을 보여줌
    private fun showGradedLinesOnly() {
        binding.lineDrawingView.clearLines() // 기존 선 지우기

        // gradedLines의 복사본을 사용하여 반복
        val gradedLinesCopy = gradedLines.toList()

        for ((pair, isCorrect) in gradedLinesCopy) {
            drawLineForResult(pair, isCorrect)
            Lg.d("test", MatingQuizFragment::class.java.simpleName, "$isCorrect")
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

    private fun drawLineForResult(pair: Pair<Int, Int>, isCorrect: Boolean) {
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

            val color = if (isCorrect) ContextCompat.getColor(requireContext(), R.color.correct_card_number_background) else ContextCompat.getColor(requireContext(), R.color.incorrect_true_false)
            binding.lineDrawingView.addLine(startXInView.toFloat(), startYInView.toFloat(), endXInView.toFloat(), endYInView.toFloat(), color)

            // 정답 여부와 함께 선을 저장
            gradedLines.add(Pair(pair, isCorrect))
        }
    }


    fun setLoadNextQuizListener(listener: () -> Unit) {
        loadNextQuiz = listener
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