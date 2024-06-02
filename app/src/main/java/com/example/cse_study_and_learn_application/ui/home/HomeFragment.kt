package com.example.cse_study_and_learn_application.ui.home

import android.content.res.XmlResourceParser
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.example.cse_study_and_learn_application.MainViewModel
import com.example.cse_study_and_learn_application.R
import com.example.cse_study_and_learn_application.databinding.FragmentHomeBinding
import com.example.cse_study_and_learn_application.model.QuizCategory
import com.example.cse_study_and_learn_application.utils.Subcategory
import kotlinx.coroutines.launch
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException


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
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        homeViewModel = ViewModelProvider(requireActivity())[HomeViewModel::class.java]
        mainViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]

        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        setupRecyclerView()

        val gifPath = "file:///android_asset/images/gnu/gnu_hi.gif"
        Glide.with(requireActivity())
            .asGif()
            .load(gifPath)
            .into(binding.ivGnuChar)

        homeViewModel.setCategoryThumbnails(requireContext())   // 썸네일 리스트 초기화

        homeViewModel.quizSubjectCategories.observe(viewLifecycleOwner) { categories ->
            if (categories.isNotEmpty()) {
                // 문제 카테고리 정보 사용
                adapter.updateItem(categories)

            } else {
                // 카테고리 정보 가져오기 실패 처리
                // 오류 메시지 표시 등의 동작 수행
                Toast.makeText(requireContext(), "과목을 불러오는데 실패했습니다.", Toast.LENGTH_SHORT).show()
            }
        }

        return binding.root
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

        homeViewModel.getQuizSubjects(requireContext()) // 과목 불러오는 코드
        // homeViewModel.getQuizLoad(requireContext(), Subcategory.ALL)
    }


    private fun setupRecyclerView() {
        // 임시 Subject 데이터 생성
        val subjects = mutableListOf<QuizCategory>()
        val parser: XmlResourceParser = requireContext().resources.getXml(R.xml.thumbnails)

        val imagePath = "subj_all_random.jpg"

        val subject = QuizCategory(-1, "문제 선택 풀기", "images/subjects/$imagePath", "눌러서 문제를 고르세요", "⭐")
        subjects.add(subject)

        try {
            var eventType = parser.eventType
            var id = 1
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG && parser.name == "subject") {
                    val name = parser.getAttributeValue(null, "name")
                    parser.next()
                    val imgbg = parser.text.trim()
                    val subj = QuizCategory(id, name, "images/subjects/$imgbg", "0문제 / 30문제", if (name.hashCode() % 2 == 0) "💡" else "⭐")
                    subjects.add(subj)
                    id += 1

                }

                eventType = parser.next()
            }
        } catch (e: XmlPullParserException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            parser.close()
        }


        homeViewModel.setInitSubjectCategories(subjects)    // 테스트용 init
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
        // Toast.makeText(context, subject.title, Toast.LENGTH_SHORT).show()
        homeViewModel.setSubject(subject)

        if (subject.title == "문제 선택 풀기") {
            Toast.makeText(requireContext(), "문제 선택 풀기 구현 예정", Toast.LENGTH_SHORT).show()
            return
        }

        // 과목 아이템 클릭하면 액티비티 레벨의 앱바의 크기를 저장하고 숨김
        (activity as AppCompatActivity).let {
            it.supportActionBar?.apply {
                hide()
            }
        }

        val action = HomeFragmentDirections.actionNavigationHomeToSubjectContentsFragment()
        findNavController().navigate(action)
    }
}


interface OnSubjectItemClickListener {
    fun onSubjectItemClick(subject: QuizCategory)
}
