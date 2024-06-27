package com.example.cse_study_and_learn_application.ui.study

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.commit
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cse_study_and_learn_application.R
import com.example.cse_study_and_learn_application.databinding.FragmentMatingQuizBinding
import com.example.cse_study_and_learn_application.model.MatingQuizJsonContent
import com.example.cse_study_and_learn_application.model.RandomQuiz
import com.example.cse_study_and_learn_application.utils.QuizType
import com.example.cse_study_and_learn_application.utils.QuizUtils
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

    private var quizId = -1
    private var hasImg = false
    private lateinit var jsonString: String
    private lateinit var content: MatingQuizJsonContent

    // 선택한 버튼의 위치를 저장할 변수
    private var leftSelectedPosition: Int? = null
    private var rightSelectedPosition: Int? = null

    // 선이 이어진 상태 저장
    private val connectedPairs: MutableSet<Pair<Int, Int>> = mutableSetOf()

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

            binding.lineDrawingView.addLine(startXInView.toFloat(), startYInView.toFloat(), endXInView.toFloat(), endYInView.toFloat())
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

            binding.lineDrawingView.removeLine(startXInView.toFloat(), startYInView.toFloat(), endXInView.toFloat(), endYInView.toFloat())
            // Log.d("test", "Removing line: Screen coordinates: $startX, $startY, $endX, $endY")
            // Log.d("test", "Removing line: View coordinates: $startXInView, $startYInView, $endXInView, $endYInView")
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

    override fun onAnswerSubmit() {
        val answerList = connectedPairs.map { pair -> "${pair.first}t${pair.second}" }
        Log.d("test", answerList.toString())

        if(answerList.isEmpty()) {
            Toast.makeText(context, "답을 선택 해주세요.", Toast.LENGTH_SHORT).show()
        } else {
            try {
                val bundle = Bundle().apply {
                    putStringArrayList("userAnswer", ArrayList(answerList))
                    putStringArrayList("answer", ArrayList(content.answer))
                    putString("commentary", content.commentary)
                    putInt("quizId", quizId)
                    putInt("quizType", QuizType.MATING_QUIZ.ordinal)
                }
                parentFragmentManager.commit {
                    replace(R.id.fragmentContainerView, GradingFragment().apply {
                        arguments = bundle
                    })
                    addToBackStack(null)
                }
            } catch (e: Exception) {
                Log.e("MatingQuizFragment", "onAnswerSubmit", e)
            }

        }
    }
}