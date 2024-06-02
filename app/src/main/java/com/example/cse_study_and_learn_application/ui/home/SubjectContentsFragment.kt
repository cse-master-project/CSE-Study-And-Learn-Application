package com.example.cse_study_and_learn_application.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cse_study_and_learn_application.R
import com.example.cse_study_and_learn_application.databinding.FragmentSubjectContentsBinding
import com.example.cse_study_and_learn_application.model.QuizContentCategory
import com.example.cse_study_and_learn_application.model.RandomQuiz
import com.example.cse_study_and_learn_application.ui.login.AccountAssistant
import com.example.cse_study_and_learn_application.ui.other.DialogQuestMessage
import com.example.cse_study_and_learn_application.ui.study.QuizActivity
import com.example.cse_study_and_learn_application.utils.QuizUtils
import com.example.cse_study_and_learn_application.utils.Subcategory
import kotlinx.coroutines.launch


/**
 * Subject contents fragment
 *
 * @constructor Create empty Subject contents fragment
 * @author kjy
 * @since 2024-03-09
 *
 *
 * 최근 주요 변경점
 * - adapter context 매개변수 추가
 */
class SubjectContentsFragment : Fragment(), OnClickListener {
    private var _binding: FragmentSubjectContentsBinding? = null
    private val binding get() = _binding!!
    private lateinit var homeViewModel: HomeViewModel


    private lateinit var adapter: SubjectContentItemAdapter
    private var quizSettingDialogFlag = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentSubjectContentsBinding.inflate(inflater, container, false)
        homeViewModel = ViewModelProvider(requireActivity())[HomeViewModel::class.java]

        val detailSubjects = homeViewModel.getCurrentDetailSubjects()
        adapter = if (detailSubjects.isEmpty()) {
            Toast.makeText(requireContext(), "조건에 일치하는 문제가 없습니다.", Toast.LENGTH_SHORT).show()
            SubjectContentItemAdapter(emptyList(), requireContext())
        } else {
            SubjectContentItemAdapter(detailSubjects.toList(), requireContext())
        }

        binding.rvContent.adapter = adapter
        binding.rvContent.layoutManager = LinearLayoutManager(context)

        binding.fabQuestionExe.setOnClickListener(this)
        binding.cdvExtendQuizSetting.setOnClickListener(this)
        binding.cbAllRandom.setOnClickListener(this)
        binding.rbAllSel.setOnClickListener(this)
        binding.rbCustomSel.setOnClickListener(this)
        binding.rbDefaultSel.setOnClickListener(this)

        // 소분류 (전체, 기본, 사용자 문제) 선택
        // 소분류 불러오기에 따른 중분류 선택하는 리사이클러뷰 바꾸기
        homeViewModel.detailSubjects.observe(viewLifecycleOwner) {
            var currentDetailSubjects = homeViewModel.getCurrentDetailSubjects()
            Log.d("test", "getCurrentDetailSubjects: ${currentDetailSubjects.toString()}")
            if (currentDetailSubjects.isNullOrEmpty()) {
               Toast.makeText(requireContext(), "조건에 일치하는 문제가 없습니다.", Toast.LENGTH_SHORT).show()
                currentDetailSubjects = mutableListOf()
                Log.d("test", "NULL인데")
            }
            adapter.changeDetailSubjects(currentDetailSubjects.toList())
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvTitle.text = homeViewModel.subject.title
        binding.ibBackPres.setOnClickListener {
            requireActivity().onBackPressed()
        }

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

    override fun onClick(v: View?) {
        when (v?.id) {
            // 화면 우측 하단 문제 풀기 버튼
            R.id.fab_question_exe -> {
                // 랜덤 선택 풀기 버튼이 클릭되어 있으면 다이얼로그로 맞는지 물어보고 문제 풀기 진행
                if (binding.cbAllRandom.isChecked) {
                    val dialogQuestMessage = DialogQuestMessage(requireContext(),
                        R.layout.dialog_quest_message_topdown,
                        "전체 문제 풀기가\n선택되었습니다.\n이대로 진행 하시겠습니까?").apply {
                        cardMarginInDp = 15f
                    }

                    dialogQuestMessage.setPositive {
                        Toast.makeText(context, "네 클릭", Toast.LENGTH_SHORT).show()
                        dialogQuestMessage.dismiss()
                        checkDetailQuizSend()
                    }

                    dialogQuestMessage.setNegative {
                        Toast.makeText(context, "아니요 클릭", Toast.LENGTH_SHORT).show()
                        quizSettingDialogFlag = true
                        binding.llDialogSetting.visibility = View.VISIBLE   // 퀴즈 설정 다이얼로그 표시
                        dialogQuestMessage.dismiss()
                    }

                    dialogQuestMessage.show()
                } else {
                    checkDetailQuizSend()
                }

            }

            // 퀴즈 관련해서 설정할 수 있는 다이얼로그 버튼
            R.id.cdv_extend_quiz_setting -> {
                quizSettingDialogFlag = !quizSettingDialogFlag
                if (quizSettingDialogFlag) {
                    binding.llDialogSetting.visibility = View.VISIBLE
                } else {
                    binding.llDialogSetting.visibility = View.INVISIBLE
                }
            }

            // 랜덤 선택 풀기
            R.id.cb_all_random -> {
                adapter.toggleCheckBoxVisibility()
            }

            R.id.rb_all_sel-> {
                homeViewModel.getQuizLoad(requireContext(), Subcategory.ALL)
                Log.d("test", "rb_all_sel click")

            }

            R.id.rb_custom_sel -> {
                Log.d("test", "rb_custom_sel click")
                homeViewModel.getQuizLoad(requireContext(), Subcategory.USER)
            }

            R.id.rb_default_sel -> {
                Log.d("test", "rb_default_sel click")
                homeViewModel.getQuizLoad(requireContext(), Subcategory.DEFAULT)
            }
        }
    }

    private fun checkDetailQuizSend() {
        val subject = homeViewModel.subject.title
        val detailsAdapter = binding.rvContent.adapter as SubjectContentItemAdapter
        val detailSubject = detailsAdapter.getSelectedItems()
        // Log.d("test", detailSubject.toString())

        if (detailSubject.isNotEmpty()) {
            val temporaryDetailSubject = arrayListOf<String>()

            detailSubject.forEach {
                temporaryDetailSubject.add(it.title)
            }

            Log.d("detail", temporaryDetailSubject.toString())

            val i = Intent(requireContext(), QuizActivity::class.java)
            i.putExtra("subject", subject)
            i.putExtra("detailSubject", temporaryDetailSubject.joinToString(","))

            startActivity(i)
        } else {
            Toast.makeText(requireContext(), "하나 이상 선택하세요.", Toast.LENGTH_SHORT).show()
        }

    }
}