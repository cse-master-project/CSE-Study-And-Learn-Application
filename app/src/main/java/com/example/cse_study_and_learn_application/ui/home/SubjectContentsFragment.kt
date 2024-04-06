package com.example.cse_study_and_learn_application.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cse_study_and_learn_application.R
import com.example.cse_study_and_learn_application.databinding.FragmentSubjectContentsBinding
import com.example.cse_study_and_learn_application.model.QuizContentCategory
import com.example.cse_study_and_learn_application.ui.other.DialogQuestMessage


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
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

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

        val quizContentCategories = List(30) {
            QuizContentCategory("No. ${it + 1} 큐 스택 아무거나", false)
        }

        adapter = SubjectContentItemAdapter(quizContentCategories, requireContext())
        binding.rvContent.adapter = adapter
        binding.rvContent.layoutManager = LinearLayoutManager(context)

        binding.fabQuestionExe.setOnClickListener(this)
        binding.cdvExtendQuizSetting.setOnClickListener(this)
        binding.cbAllRandom.setOnClickListener(this)


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
                    }

                    dialogQuestMessage.setNegative {
                        Toast.makeText(context, "아니요 클릭", Toast.LENGTH_SHORT).show()
                        binding.llDialogSetting.visibility = View.VISIBLE   // 퀴즈 설정 다이얼로그 표시
                        dialogQuestMessage.dismiss()
                    }

                    dialogQuestMessage.show()
                } else {
                    // 체크가 되어 있는지 확인
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
        }
    }


}