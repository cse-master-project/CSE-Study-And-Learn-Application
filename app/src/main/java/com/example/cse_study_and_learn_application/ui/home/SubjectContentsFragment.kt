package com.example.cse_study_and_learn_application.ui.home

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cse_study_and_learn_application.R
import com.example.cse_study_and_learn_application.databinding.FragmentSubjectContentsBinding
import com.example.cse_study_and_learn_application.ui.other.DesignToast
import com.example.cse_study_and_learn_application.ui.other.DialogQuestMessage
import com.example.cse_study_and_learn_application.ui.study.QuizActivity
import com.example.cse_study_and_learn_application.utils.HighlightHelper
import com.example.cse_study_and_learn_application.utils.HighlightItem
import com.example.cse_study_and_learn_application.utils.HighlightPosition
import com.example.cse_study_and_learn_application.utils.HighlightView
import com.example.cse_study_and_learn_application.utils.Subcategory
import com.example.cse_study_and_learn_application.utils.dpToPx


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

    private var hasUserQuiz: Boolean = true
    private var hasDefaultQuiz: Boolean = true
    private var hasSolvedQuiz: Boolean = true

    private lateinit var highlightHelper: HighlightHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentSubjectContentsBinding.inflate(inflater, container, false)
        homeViewModel = ViewModelProvider(requireActivity())[HomeViewModel::class.java]

        binding.fabQuestionExe.setOnClickListener(this)
        binding.cdvExtendQuizSetting.setOnClickListener(this)
        binding.cbAllRandom.setOnClickListener(this)
        binding.rbAllSel.setOnClickListener(this)
        binding.rbCustomSel.setOnClickListener(this)
        binding.rbDefaultSel.setOnClickListener(this)
        binding.cbAlready.setOnClickListener(this)
        binding.llSettingOuter.setOnClickListener(this)
        binding.tvAllSelect.setOnClickListener(this)

        activity?.apply {
            val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main)
            val layoutParams = navHostFragment?.view?.layoutParams as? ViewGroup.MarginLayoutParams
            layoutParams?.apply {
                topMargin = 0
            }
        }

        adapter = SubjectContentItemAdapter(emptyList(), requireContext())
        adapter.setToggleCheckBoxListener(object: SubjectContentItemAdapter.ToggleCheckBoxListener{
            override fun clickToggle(title: String, selected: Boolean) {
                homeViewModel.clickRecyclerItemCheck(title, selected)
            }
        })

        adapter.setItemClickListener(object: SubjectContentItemAdapter.ItemClickListener {
            override fun click(title: String, selected: Boolean) {
                homeViewModel.clickRecyclerItemCheck(title, selected)
            }
        })

        binding.rvContent.adapter = adapter
        binding.rvContent.layoutManager = LinearLayoutManager(context)


        // 소분류 (전체, 기본, 사용자 문제) 선택
        // 소분류 불러오기에 따른 중분류 선택하는 리사이클러뷰 바꾸기
        // homeViewModel.detailSubjects.observe(viewLifecycleOwner) {
        //     var currentDetailSubjects = homeViewModel.getCurrentDetailSubjects()
        //     if (currentDetailSubjects.isEmpty()) {
        //        Toast.makeText(requireContext(), "조건에 일치하는 문제가 없습니다.", Toast.LENGTH_SHORT).show()
        //         currentDetailSubjects = mutableListOf()

        //     }
        //     adapter.changeDetailSubjects(currentDetailSubjects.toList())
        // }

        initToolbarListener()

        initSubjectContents()   // 이 메서드에서 퀴즈 중분류 구성하기

        highlightHelper = HighlightHelper(
            requireContext(),
            this,
            listOf(
                HighlightItem(R.id.cdv_extend_quiz_setting, "기본 제공 문제, 창작 문제 등 퀴즈 설정을 여기서 할 수 있습니다.", showPosition = HighlightPosition.UI_BOTTOM),
                HighlightItem(R.id.rv_content, "여기를 클릭해서 풀고자 하는 챕터를 고를 수 있습니다.",  showPosition = HighlightPosition.UI_BOTTOM  , position = 1, scaleFactor = 1.1f),
                HighlightItem(R.id.fab_question_exe, "문제 풀기를 시작하려면 여기를 클릭하세요.", showPosition = HighlightPosition.UI_TOP),
            ),
            debugMode = false,
            heightThreshold = requireContext().dpToPx(28),
            bubblePadding = requireContext().dpToPx(10),
            screenName = SubjectContentsFragment::class.java.name
        )

        binding.root.post {
            highlightHelper.showHighlights()
        }

        homeViewModel.isAllSelected.observe(viewLifecycleOwner) { isAllSelected ->
            if (isAllSelected) {
                binding.tvAllSelect.text = "전체 선택 해제"
            } else {
                binding.tvAllSelect.text = "전체 선택"
            }
        }

        return binding.root
    }





    private fun initToolbarListener() {
        binding.ibBackPres.setOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.ibNextSubj.setOnClickListener {
            changeQuizSubject("NEXT")
        }

        binding.ibPrevSubj.setOnClickListener {
            changeQuizSubject("PREV")
        }
    }

    private fun changeQuizSubject(type: String) {
        val quizSubjects = homeViewModel.quizSubjectCategories.value
        val currentSubject = homeViewModel.subject
        val currentSubjectIndex = quizSubjects?.indexOfFirst { it.title == currentSubject.title }

        if (currentSubjectIndex != null) {

            val nextSubject = when (type) {
                "NEXT" -> {
                    if (currentSubjectIndex == quizSubjects.size - 1) {
                        quizSubjects[0]
                    } else {
                        quizSubjects[currentSubjectIndex + 1]
                    }
                }
                "PREV" -> {
                    if (currentSubjectIndex == 0) {
                        quizSubjects[quizSubjects.size - 1]
                    } else {
                        quizSubjects[currentSubjectIndex - 1]
                    }
                }
                else -> {
                    currentSubject
                }
            }
            homeViewModel.setSubject(nextSubject)
            initSubjectContents()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initSubjectContents() {
        val currentSubject = homeViewModel.subject
        binding.tvTitle.text = currentSubject.title

        val detailSubjects = homeViewModel.getCurrentDetailSubjects()
        if (detailSubjects.isEmpty()) {
            //Toast.makeText(requireContext(), "조건에 일치하는 문제가 없습니다.", Toast.LENGTH_SHORT).show()

            DesignToast.makeText(requireContext(), DesignToast.LayoutDesign.ERROR, "조건에 일치하는 문제가 없습니다.").show()
            requireActivity().onBackPressed()
        }

        adapter.changeDetailSubjects(detailSubjects.toList())
        adapter.notifyDataSetChanged()

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
                        // Toast.makeText(context, "네 클릭", Toast.LENGTH_SHORT).show()
                        dialogQuestMessage.dismiss()
                        checkDetailQuizSend()
                    }

                    dialogQuestMessage.setNegative {
                        // Toast.makeText(context, "아니요 클릭", Toast.LENGTH_SHORT).show()
                        quizSettingDialogFlag = false
                        binding.llDialogSetting.visibility = View.VISIBLE   // 퀴즈 설정 다이얼로그 표시
                        binding.llSettingOuter.visibility = View.VISIBLE
                        dialogQuestMessage.dismiss()
                    }

                    dialogQuestMessage.show()
                } else {
                    checkDetailQuizSend()
                }

            }

            R.id.tv_all_select -> {
                if (adapter.toggleCheckBox) {
                    DesignToast.makeText(requireContext(), DesignToast.LayoutDesign.INFO, "먼저 랜덤 풀기 설정을 해제해야합니다.").show()
                    return
                }

                if (binding.tvAllSelect.text == "전체 선택") {
                    homeViewModel.setChangeCheckSetting(true)
                    adapter.setChangeCheck(true)
                } else {
                    homeViewModel.setChangeCheckSetting(false)
                    adapter.setChangeCheck(false)
                }
            }

            R.id.ll_setting_outer -> {
                quizSettingDialogFlag = !quizSettingDialogFlag
                if (quizSettingDialogFlag) {
                    binding.llDialogSetting.visibility = View.VISIBLE
                    binding.llSettingOuter.visibility = View.VISIBLE
                } else {
                    binding.llDialogSetting.visibility = View.INVISIBLE
                    binding.llSettingOuter.visibility = View.GONE
                }
            }

            // 퀴즈 관련해서 설정할 수 있는 다이얼로그 버튼
            R.id.cdv_extend_quiz_setting -> {
                quizSettingDialogFlag = !quizSettingDialogFlag
                if (quizSettingDialogFlag) {
                    binding.llDialogSetting.visibility = View.VISIBLE
                    binding.llSettingOuter.visibility = View.VISIBLE
                } else {
                    binding.llDialogSetting.visibility = View.INVISIBLE
                    binding.llSettingOuter.visibility = View.GONE
                }
            }

            // 랜덤 선택 풀기
            R.id.cb_all_random -> {
                val result = adapter.toggleCheckBoxVisibility()
                if (result) homeViewModel.setChangeCheckSetting(true)
            }

            R.id.cb_already -> {
                hasSolvedQuiz = binding.cbAlready.isChecked
            }

            R.id.rb_all_sel-> {
                homeViewModel.getQuizLoad(requireContext(), Subcategory.ALL)
                Log.d("test", "rb_all_sel click")
                hasUserQuiz = true
                hasDefaultQuiz = true
            }

            R.id.rb_custom_sel -> {
                Log.d("test", "rb_custom_sel click")
                homeViewModel.getQuizLoad(requireContext(), Subcategory.USER)
                hasUserQuiz = true
                hasDefaultQuiz = false
            }

            R.id.rb_default_sel -> {
                Log.d("test", "rb_default_sel click")
                homeViewModel.getQuizLoad(requireContext(), Subcategory.DEFAULT)
                hasUserQuiz = false
                hasDefaultQuiz = true
            }
        }
    }

    private fun checkDetailQuizSend() {
        val subject = homeViewModel.subject.title
        val detailSubject = homeViewModel.getSelectedDetailSubjects()
        // Log.d("test", "detailSubject: ${detailSubject.toString()}")

        if (detailSubject.isNotEmpty()) {
            val temporaryDetailSubject = arrayListOf<String>()

            detailSubject.forEach {
                temporaryDetailSubject.add(it.title)
            }

            // Log.d("detail", temporaryDetailSubject.toString())

            val i = Intent(requireContext(), QuizActivity::class.java)
            i.putExtra("isRandom", false)
            i.putExtra("subject", subject)
            i.putExtra("detailSubject", temporaryDetailSubject.joinToString(","))
            i.putExtra("hasUserQuiz", hasUserQuiz)
            i.putExtra("hasDefaultQuiz", hasDefaultQuiz)
            i.putExtra("hasSolvedQuiz", hasSolvedQuiz)
            // Log.d("test", "subject: $subject")

            startActivity(i)
        } else {
            // Toast.makeText(requireContext(), "하나 이상 선택하세요.", Toast.LENGTH_SHORT).show()
            DesignToast.makeText(requireContext(), DesignToast.LayoutDesign.INFO, "하나 이상 선택하세요.").show()

        }

    }
}
