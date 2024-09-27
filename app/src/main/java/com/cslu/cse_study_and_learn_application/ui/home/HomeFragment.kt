package com.cslu.cse_study_and_learn_application.ui.home

import android.content.Intent
import android.content.res.XmlResourceParser
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageView
import android.widget.ListView
import android.widget.PopupWindow
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.cslu.cse_study_and_learn_application.MainViewModel
import com.cslu.cse_study_and_learn_application.R
import com.cslu.cse_study_and_learn_application.databinding.FragmentHomeBinding
import com.cslu.cse_study_and_learn_application.model.QuizCategory
import com.cslu.cse_study_and_learn_application.ui.other.DesignToast
import com.cslu.cse_study_and_learn_application.ui.other.DialogQuizSelect
import com.cslu.cse_study_and_learn_application.ui.study.QuizActivity
import com.cslu.cse_study_and_learn_application.utils.HighlightHelper
import com.cslu.cse_study_and_learn_application.utils.HighlightItem
import com.cslu.cse_study_and_learn_application.utils.HighlightPosition
import com.cslu.cse_study_and_learn_application.utils.Lg
import com.cslu.cse_study_and_learn_application.utils.dpToPx


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

    private lateinit var mainViewModel: MainViewModel

    private lateinit var adapter: SubjectItemAdapter
    private lateinit var searchViewAdapter: ArrayAdapter<String>
    private lateinit var temporalSubjects: MutableList<QuizCategory>



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        homeViewModel = ViewModelProvider(requireActivity())[HomeViewModel::class.java]
        mainViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]

        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        setupRecyclerView()
        setupSearchView()

        homeViewModel.setCategoryThumbnails(requireContext())   // 썸네일 리스트 초기화

        homeViewModel.quizSubjectCategories.observe(viewLifecycleOwner) { categories ->
            if (categories.isNotEmpty()) {
                // 문제 카테고리 정보 사용
                adapter.updateItem(categories)
            } else {
                // 카테고리 정보 가져오기 실패 처리
                // 오류 메시지 표시 등의 동작 수행
                DesignToast.makeText(requireContext(), DesignToast.LayoutDesign.ERROR, "과목을 불러오는데 실패하였습니다.").show()
                //Toast.makeText(requireContext(), "과목을 불러오는데 실패했습니다.", Toast.LENGTH_SHORT).show()
            }
        }

        val parentLocation = IntArray(2)
        (binding.rvSubjects.parent as View).getLocationOnScreen(parentLocation)
        Lg.d("test", HomeFragment::class.java.simpleName,
            "ParentView location: X: ${parentLocation[0]}, Y: ${parentLocation[1]}")


        val highlightHelper = HighlightHelper(
            requireContext(),
            this,
            listOf(
                HighlightItem(
                    R.id.search_view,
                    "여기서 찾고자 하는 과목을 검색할 수 있습니다.",
                    showPosition = HighlightPosition.UI_BOTTOM
                ),
                HighlightItem(
                    R.id.rv_subjects,
                    "여기를 클릭해서 원하는 과목을 선택해서 랜덤으로 문제를 풀 수 있습니다.",
                    showPosition = HighlightPosition.UI_BOTTOM,
                    position = 0,
                    scaleFactor = 1.1f
                ),
                HighlightItem(
                    R.id.rv_subjects,
                    "여기를 클릭해서 원하는 과목을 하나를 지정해서 문제를 풀 수 있습니다.",
                    showPosition = HighlightPosition.UI_BOTTOM,
                    position = 1,
                    scaleFactor = 1.1f
                ),
            ),
            debugMode = false,
            bubblePadding = requireContext().dpToPx(20),
            screenName = HomeFragment::class.java.name
        )

        binding.root.post {
            highlightHelper.showHighlights()
        }

        return binding.root
    }

    private fun setupSearchView() {
        val searchView = binding.searchView

        // SearchView 텍스트 색상 및 힌트 색상 설정
        val searchEditText = searchView.findViewById<EditText>(androidx.appcompat.R.id.search_src_text)
        searchEditText.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.black))
        searchEditText.setHintTextColor(ContextCompat.getColor(requireContext(), R.color.light_blue_300))

        // 초기 상태에서 힌트 텍스트 설정
        binding.searchViewHint.visibility = View.VISIBLE

        // 돋보기 아이콘 클릭 시 포커스 설정
        searchView.setOnSearchClickListener {
            binding.searchViewHint.visibility = View.INVISIBLE
            searchView.onActionViewExpanded()  // SearchView를 확장 상태로 설정
            searchEditText.requestFocus()  // EditText에 포커스를 설정
        }

        // 포커스를 잃었을 때 초기 상태로 돌아가도록 설정
        searchView.setOnCloseListener {
            binding.searchViewHint.visibility = View.VISIBLE
            false
        }

        val subjects = temporalSubjects.map { it.title }
        searchViewAdapter = ArrayAdapter(requireContext(), R.layout.item_search, R.id.tv_suggestion, subjects)

        val searchListView = ListView(requireContext()).apply {
            adapter = searchViewAdapter
            divider = null // 구분선 제거
        }

        val popupWindow = PopupWindow(searchListView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true).apply {
            setBackgroundDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.bg_dropdown))
            isOutsideTouchable = true
            isFocusable = false // 팝업이 포커스를 가지지 않도록 설정
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { searchAndScrollToItem(it) }
                popupWindow.dismiss()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrEmpty()) {
                    popupWindow.dismiss()
                } else {
                    searchViewAdapter.filter.filter(newText)
                    if (!popupWindow.isShowing) {
                        popupWindow.showAsDropDown(searchView)
                    }
                }
                return true
            }
        })

        searchListView.setOnItemClickListener { parent, view, position, id ->
            val selectedItem = parent.getItemAtPosition(position) as String
            searchView.setQuery(selectedItem, false)
            searchAndScrollToItem(selectedItem)
            popupWindow.dismiss()
        }

        // X 버튼 클릭 시 검색 취소 기능 추가
        val closeButton = searchView.findViewById<ImageView>(androidx.appcompat.R.id.search_close_btn)
        closeButton.setOnClickListener {
            searchView.setQuery("", false)
            searchView.clearFocus()
            binding.searchViewHint.visibility = View.VISIBLE
            // SearchView 초기 상태로 되돌리기
            searchView.onActionViewCollapsed()
        }

        // TextView 클릭 리스너 추가
        binding.searchViewHint.setOnClickListener {
            binding.searchViewHint.visibility = View.INVISIBLE
            searchView.onActionViewExpanded()  // SearchView를 확장 상태로 설정
            searchEditText.requestFocus()  // EditText에 포커스를 설정
        }
    }
    private fun searchAndScrollToItem(query: String) {
        val position = homeViewModel.quizSubjectCategories.value?.indexOfFirst {
            it.title.contains(query, ignoreCase = true)
        } ?: -1

        if (position != -1) {
            binding.rvSubjects.post {
                val viewHolder = binding.rvSubjects.findViewHolderForAdapterPosition(position)
                viewHolder?.itemView?.let {
                    val y = it.top
                    binding.nestedScrollView.smoothScrollTo(0, y)
                }
            }
        } else {
            DesignToast.makeText(requireContext(), DesignToast.LayoutDesign.INFO, "해당 과목을 찾을 수 없습니다.").show()
        }
    }





    override fun onResume() {
        super.onResume()
        // 액티비티 레벨의 앱바를 다시 보여주고 마진 적용
        (activity as AppCompatActivity).let {
            it.supportActionBar?.apply {
                show()
                val navHostFragment =
                    it.supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main)
                val layoutParams =
                    navHostFragment?.view?.layoutParams as ViewGroup.MarginLayoutParams
                layoutParams.topMargin = mainViewModel.appBarHeight
            }
        }

        homeViewModel.getQuizSubjects(requireContext()) // 과목 불러오는 코드
        // homeViewModel.getQuizLoad(requireContext(), Subcategory.ALL)
    }


    private fun setupRecyclerView() {
        // 임시 Subject 데이터 생성
        temporalSubjects = mutableListOf<QuizCategory>()
        val parser: XmlResourceParser = requireContext().resources.getXml(R.xml.thumbnails)

        val imagePath = "subj_all_random.jpg"

        val subject =
            QuizCategory(-1, "여러 과목 문제 풀기", "images/subjects/$imagePath", "과목을 고르세요", "⭐")
        temporalSubjects.add(subject)

//        try {
//            var eventType = parser.eventType
//            var id = 1
//            while (eventType != XmlPullParser.END_DOCUMENT) {
//                if (eventType == XmlPullParser.START_TAG && parser.name == "subject") {
//                    val name = parser.getAttributeValue(null, "name")
//                    parser.next()
//                    val imgbg = parser.text.trim()
//                    val subj = QuizCategory(
//                        id,
//                        name,
//                        "images/subjects/$imgbg",
//                        "0문제 / 30문제",
//                        if (name.hashCode() % 2 == 0) "💡" else "⭐"
//                    )
//                    temporalSubjects.add(subj)
//                    id += 1
//
//                }
//
//                eventType = parser.next()
//            }
//        } catch (e: XmlPullParserException) {
//            e.printStackTrace()
//        } catch (e: IOException) {
//            e.printStackTrace()
//        } finally {
//            parser.close()
//        }


        homeViewModel.setInitSubjectCategories(temporalSubjects)    // 테스트용 init
        homeViewModel.quizSubjectCategories.value?.let {
            // 어댑터 생성 및 설정
            adapter = SubjectItemAdapter(it, this)
            binding.rvSubjects.adapter = adapter


            // RecyclerView에 LayoutManager 설정
            binding.rvSubjects.layoutManager = GridLayoutManager(context, 2)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onSubjectItemClick(subject: QuizCategory) {
        homeViewModel.setSubject(subject)

        if (subject.title == "여러 과목 문제 풀기") {
            val context = requireContext()
            val dialog = DialogQuizSelect(requireActivity())

            dialog.setNegative { dialog.dismiss() }
            dialog.setPositive {
                val selectedSubjects = homeViewModel.flexboxSelectedSubjects.value // [컴퓨터 개론, computer ... ]
                if (selectedSubjects.isNullOrEmpty()) {
                    // Toast.makeText(requireContext(), "과목을 하나 이상 선택하세요.", Toast.LENGTH_SHORT).show()
                    DesignToast.makeText(requireContext(), DesignToast.LayoutDesign.INFO, "과목을 하나 이상 선택하세요.").show()
                } else {
                    // selectedSubjects <- [컴퓨터 개론, computer ... ]
                    // 문제 푸는 화면으로 넘어가는 코드 작성
                    val i = Intent(requireContext(), QuizActivity::class.java)
                    i.putExtra("isRandom", true)
                    i.putStringArrayListExtra("subjectList", ArrayList(selectedSubjects))
                    i.putExtra("hasUserQuiz", dialog.swUserQuiz.isChecked)
                    i.putExtra("hasDefaultQuiz", dialog.swDefaultQuiz.isChecked)
                    i.putExtra("hasSolvedQuiz", dialog.swSolvedQuiz.isChecked)
                    // Lg.d("test",HomeFragment::class.java.name, dialog.swDefaultQuiz.isChecked.toString())
                    // Lg.d("test", HomeFragment::class.java.name, "subjects: ${ArrayList(selectedSubjects)}")

                    startActivity(i)
                    dialog.dismiss()
                }
            }

            val subjects = homeViewModel.quizSubjects.value
            dialog.setOnShowListener {
                val spinner = dialog.getSpinner()
                if (subjects.isNullOrEmpty()) {
                    DesignToast.makeText(requireContext(), DesignToast.LayoutDesign.ERROR, "과목을 불러오는데 실패하였습니다.").show()
                    //Toast.makeText(context, "과목을 불러오는데 실패했습니다.", Toast.LENGTH_SHORT).show()
                    val subjectStringArray = temporalSubjects.map { it.title }.drop(1).toTypedArray()
                    val spinnerAdapter = ArrayAdapter(requireActivity(), R.layout.tv_spinner_quiz_select, subjectStringArray)
                    spinner.adapter = spinnerAdapter
                } else {
                    val subjectStringArray = subjects.map { it.subject }.toTypedArray()
                    val spinnerAdapter = ArrayAdapter(requireActivity(), R.layout.tv_spinner_quiz_select, subjectStringArray)
                    spinner.adapter = spinnerAdapter
                }
            }

            dialog.show()
        } else {
            (activity as AppCompatActivity).let {
                it.supportActionBar?.apply {
                    hide()
                }
            }
            val action = HomeFragmentDirections.actionNavigationHomeToSubjectContentsFragment()
            findNavController().navigate(action)
        }
    }

}

interface OnSubjectItemClickListener {
    fun onSubjectItemClick(subject: QuizCategory)
}








