package com.example.cse_study_and_learn_application.ui.home

import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.cse_study_and_learn_application.R
import com.example.cse_study_and_learn_application.databinding.FragmentHomeBinding
import com.example.cse_study_and_learn_application.model.Subject

/**
 * Home fragment
 *
 * @constructor Create empty Home fragment
 * @author kjy
 * @since 2024-03-05
 */
class HomeFragment : Fragment(), OnSubjectItemClickListener {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        homeViewModel = ViewModelProvider(requireActivity())[HomeViewModel::class.java]

        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        setupRecyclerView()

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        // 액티비티 레벨의 앱바를 다시 보여주고 마진 적용
        (activity as AppCompatActivity).let {
            it.supportActionBar?.apply {
                if (!isShowing) {
                    show()
                    val navHostFragment = it.supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main)
                    val layoutParams = navHostFragment?.view?.layoutParams as ViewGroup.MarginLayoutParams
                    layoutParams.topMargin = homeViewModel.appBarHeight
                }
            }
        }
    }


    private fun setupRecyclerView() {
        // 임시 Subject 데이터 생성
        val subjects = List(30) {
            Subject("Subject ${it + 1}", "images/subjects/test_subject.png", "0문제 / 30문제", if (it % 2 == 0) "💡" else "⭐")
        }

        // 어댑터 생성 및 설정
        val adapter = SubjectItemAdapter(subjects, this)
        binding.rvSubjects.adapter = adapter

        // RecyclerView에 LayoutManager 설정
        binding.rvSubjects.layoutManager = GridLayoutManager(context, 2)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onSubjectItemClick(subject: Subject) {
        // Toast.makeText(context, subject.title, Toast.LENGTH_SHORT).show()
        homeViewModel.setSubject(subject)

        // 과목 아이템 클릭하면 액티비티 레벨의 앱바의 크기를 저장하고 숨김
        (activity as AppCompatActivity).let {
            it.supportActionBar?.apply {
                val typedValue = TypedValue()
                it.theme.resolveAttribute(android.R.attr.actionBarSize, typedValue, true)
                val actionBarSize = TypedValue.complexToDimensionPixelSize(
                    typedValue.data,
                    resources.displayMetrics
                )

                homeViewModel.setAppBarHeight(actionBarSize)
                hide()
            }
        }

        val action = HomeFragmentDirections.actionNavigationHomeToSubjectContentsFragment()
        findNavController().navigate(action)
    }
}


interface OnSubjectItemClickListener {
    fun onSubjectItemClick(subject: Subject)
}
