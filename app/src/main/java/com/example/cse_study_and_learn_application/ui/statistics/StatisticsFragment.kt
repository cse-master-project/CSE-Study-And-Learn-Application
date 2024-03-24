package com.example.cse_study_and_learn_application.ui.statistics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cse_study_and_learn_application.MainViewModel
import com.example.cse_study_and_learn_application.R
import com.example.cse_study_and_learn_application.databinding.FragmentStatisticsBinding
import com.example.cse_study_and_learn_application.ui.home.SubjectContentItemAdapter

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
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val statisticsViewModel =
            ViewModelProvider(this).get(StatisticsViewModel::class.java)

        _binding = FragmentStatisticsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        mainViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]

        val adapter = CategorySuccessRatioAdapter(statisticsViewModel.testRatio, requireContext())
        binding.rvCategoryRatios.adapter = adapter
        binding.rvCategoryRatios.layoutManager = LinearLayoutManager(context)




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
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}