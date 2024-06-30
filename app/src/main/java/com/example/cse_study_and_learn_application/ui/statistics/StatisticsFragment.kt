package com.example.cse_study_and_learn_application.ui.statistics

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cse_study_and_learn_application.MainViewModel
import com.example.cse_study_and_learn_application.R
import com.example.cse_study_and_learn_application.databinding.FragmentStatisticsBinding

/**
 * Statistics fragment
 *
 * @constructor Create empty Statistics fragment
 *
 * @author kjy
 * @since 2024-03-05
 */
class StatisticsFragment : Fragment() {

    private var _binding: FragmentStatisticsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var mainViewModel: MainViewModel
    private lateinit var statisticsViewModel: StatisticsViewModel


    private val quizViewModel: QuizViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentStatisticsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        mainViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        statisticsViewModel = ViewModelProvider(requireActivity())[StatisticsViewModel::class.java]

        val adapter = CategorySuccessRatioAdapter(statisticsViewModel.ratioList, requireContext())
        binding.rvCategoryRatios.adapter = adapter
        binding.rvCategoryRatios.layoutManager = LinearLayoutManager(context)

        statisticsViewModel.userQuizStatistics.observe(viewLifecycleOwner) { statistics ->
            if (statistics != null) {
                // UI 업데이트 등의 동작 수행
                adapter.itemUpdate(statisticsViewModel.ratioList)
                // Log.d("test", "성공?")

            } else {
                // 통계 데이터 가져오기 실패 처리
                // 오류 메시지 표시 등의 동작 수행
                Toast.makeText(requireContext(), "정답률을 불러오는데 실패했습니다.", Toast.LENGTH_SHORT).show()
            }
        }

        return root
    }


    override fun onResume() {
        super.onResume()
        // 액티비티 레벨의 앱바를 다시 보여주고 마진 적용
        (activity as AppCompatActivity).let {
            it.supportActionBar?.apply {
                show()
                val navHostFragment = it.supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main)
                val layoutParams = navHostFragment?.view?.layoutParams as ViewGroup.MarginLayoutParams
                layoutParams.topMargin = mainViewModel.appBarHeight
            }
        }

        statisticsViewModel.getUserQuizStatistics(requireContext()) // 서버로부터 유저의 과목별 정답률 불러옴

        statisticsViewModel.getQuizCount(requireContext(), quizViewModel) { quizStats ->
            quizStats.let {
                binding.tvTotalSuccessQuizCount.text = it.correctAnswers.toString() + " 문제"
                binding.tvTotalFailQuizCount.text = it.wrongAnswers.toString() + " 문제"
                binding.tvTotalQuizCount.text = (it.correctAnswers + it.wrongAnswers).toString() + " 문제"
            }
        }  // 유저의 결과별 문제 수를 불러옴


    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}