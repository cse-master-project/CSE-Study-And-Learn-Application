package com.example.cse_study_and_learn_application.ui.home

import android.os.Bundle
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cse_study_and_learn_application.R
import com.example.cse_study_and_learn_application.databinding.FragmentHomeBinding
import com.example.cse_study_and_learn_application.databinding.FragmentSubjectContentsBinding
import com.example.cse_study_and_learn_application.model.Subject
import com.example.cse_study_and_learn_application.model.SubjectContent


/**
 * Subject contents fragment
 *
 * @constructor Create empty Subject contents fragment
 * @author kjy
 * @since 2024-03-09
 */
class SubjectContentsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var _binding: FragmentSubjectContentsBinding? = null
    private val binding get() = _binding!!
    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentSubjectContentsBinding.inflate(inflater, container, false)
        homeViewModel = ViewModelProvider(requireActivity())[HomeViewModel::class.java]

        val subjectContents = List(30) {
            SubjectContent("No. ${it + 1} 큐 스택 아무거나")
        }

        val adapter = SubjectContentItemAdapter(subjectContents)
        binding.rvContent.adapter = adapter
        binding.rvContent.layoutManager = LinearLayoutManager(context)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Toolbar를 앱 바로 설정
        val toolbar = binding.toolbar
        toolbar.title = homeViewModel.subject.title

        activity?.apply {
            val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main)
            val layoutParams = navHostFragment?.view?.layoutParams as? ViewGroup.MarginLayoutParams
            layoutParams?.apply {
                topMargin = 0
            }
        }


    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SubjectContentsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() = SubjectContentsFragment()
    }



}