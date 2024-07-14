package com.example.cse_study_and_learn_application.ui.statistics

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cse_study_and_learn_application.MainViewModel
import com.example.cse_study_and_learn_application.R
import com.example.cse_study_and_learn_application.databinding.FragmentStatisticsBinding
import com.example.cse_study_and_learn_application.ui.other.DesignToast
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener

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

        setupPieChart()

        statisticsViewModel.userQuizStatistics.observe(viewLifecycleOwner) { statistics ->
            if (statistics != null) {
                // UI 업데이트 등의 동작 수행
                adapter.itemUpdate(statisticsViewModel.ratioList)
                // Log.d("test", "성공?")
                binding.tvTotalQuizCount.text = statistics.totalSolved.toString() + " 문제"
                binding.tvTotalSuccessQuizCount.text = statistics.totalCorrect.toString() + " 문제"
                binding.tvTotalFailQuizCount.text = statistics.totalIncorrect.toString() + " 문제"

                loadPieChartData(statistics.totalIncorrect, statistics.totalCorrect)

            } else {
                // 통계 데이터 가져오기 실패 처리
                // 오류 메시지 표시 등의 동작 수행
                // Toast.makeText(requireContext(), "정답률을 불러오는데 실패했습니다.", Toast.LENGTH_SHORT).show()

                DesignToast.makeText(requireContext(), DesignToast.LayoutDesign.ERROR, "통계 정보를 불러오지 못하였습니다.").show()
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




    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupPieChart() {
        binding.pieChart.apply {
            isDrawHoleEnabled = true
            setHoleColor(Color.TRANSPARENT)
            setUsePercentValues(true)
            description.isEnabled = false
            legend.isEnabled = false
        }

    }

    private fun loadPieChartData(a: Int, b: Int) {
        val sum = (a + b).toFloat()
        val entries = listOf(
            PieEntry(a/sum, ""),
            PieEntry(b/sum, "")
        )

        val colors = listOf(
            ContextCompat.getColor(requireContext(), R.color.light_blue_800),
            ContextCompat.getColor(requireContext(), R.color.light_blue_500)
        )

        val dataSet = PieDataSet(entries, "Category Results").apply {
            setColors(colors)
            sliceSpace = 4f
            valueLinePart1OffsetPercentage = 80f
            valueLinePart1Length = 0.2f
            valueLinePart2Length = 0.4f
            isUsingSliceColorAsValueLineColor = true

            valueLineWidth = 5f  // 값 라인 너비 설정 (테두리 효과)
            valueLineColor = Color.BLACK  // 값 라인 색상 설정 (테두리 색상)
        }

        val data = PieData(dataSet).apply {
            setDrawValues(true)
            setValueFormatter(PercentFormatter(binding.pieChart))
            setValueTextSize(10f)
            setValueTextColor(Color.WHITE)

        }

        binding.pieChart.data = data
        binding.pieChart.invalidate()
    }
}