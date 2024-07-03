package com.example.cse_study_and_learn_application.ui.home

import android.app.Application
import android.content.Context
import android.content.res.XmlResourceParser
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.cse_study_and_learn_application.R
import com.example.cse_study_and_learn_application.connector.ConnectorRepository
import com.example.cse_study_and_learn_application.model.Quiz
import com.example.cse_study_and_learn_application.model.QuizCategory
import com.example.cse_study_and_learn_application.model.QuizContentCategory
import com.example.cse_study_and_learn_application.model.QuizSubject
import com.example.cse_study_and_learn_application.model.UserQuizRequest
import com.example.cse_study_and_learn_application.model.UserQuizResponse
import com.example.cse_study_and_learn_application.ui.login.AccountAssistant
import com.example.cse_study_and_learn_application.utils.Subcategory
import kotlinx.coroutines.launch
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException

/**
 * Home view model
 *
 * @constructor Create empty Home view model
 *
 * @author kjy
 * @since 2024-03-05
 */
class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val context by lazy { application.applicationContext }
    private val connectorRepository = ConnectorRepository()
    private val _userQuizResponses = MutableLiveData<List<UserQuizResponse>>()
    val userQuizResponses: LiveData<List<UserQuizResponse>> get() = _userQuizResponses

    private lateinit var _selectedSubject: QuizCategory
    val subject get() = _selectedSubject

    private val _quizSubjects = MutableLiveData<List<QuizSubject>>()
    val quizSubjects: LiveData<List<QuizSubject>> = _quizSubjects       // 통신할때 쓰는 클래스

    private val _quizSubjectCategories = MutableLiveData<MutableList<QuizCategory>>()
    val quizSubjectCategories: LiveData<MutableList<QuizCategory>> = _quizSubjectCategories     // 내부적으로 사용하는 클래스

    private var subjectThumbnailMap = mutableMapOf<String, String>()
//
//    private val _quizResponseLiveData = MutableLiveData<QuizResponse>()
//    val quizLiveData: LiveData<QuizResponse> = _quizResponseLiveData

    private val _quizList = MutableLiveData<List<Quiz>>()
    val quizList: LiveData<List<Quiz>> = _quizList

    private val _detailSubjects = MutableLiveData<MutableMap<String, MutableSet<QuizContentCategory>>>()
    val detailSubjects: LiveData<MutableMap<String, MutableSet<QuizContentCategory>>> = _detailSubjects

    val flexboxSelectedSubjects: MutableLiveData<MutableList<String>> by lazy {
        MutableLiveData<MutableList<String>>(mutableListOf())
    }

    var currentDetailSubjectsList = mutableListOf<QuizContentCategory>()

    fun setCategoryThumbnails(context: Context) {
        val subjectThumbnailMap = mutableMapOf<String, String>()
        val parser: XmlResourceParser = context.resources.getXml(R.xml.thumbnails)
        try {
            var eventType = parser.eventType
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG && parser.name == "subject") {
                    val name = parser.getAttributeValue(null, "name")
                    parser.next()
                    val imagePath = parser.text.trim()
                    subjectThumbnailMap[name] = "images/subjects/$imagePath"
                }
                eventType = parser.next()
            }
            this.subjectThumbnailMap = subjectThumbnailMap

        } catch (e: XmlPullParserException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            parser.close()
        }
    }

    fun setInitSubjectCategories(contents: MutableList<QuizCategory>) {
        _quizSubjectCategories.value = contents
    }

    fun getQuizSubjects(context: Context) {
        viewModelScope.launch {
            try {
                val token = AccountAssistant.getServerAccessToken(context)
                val subjects = connectorRepository.getQuizSubjects(token)

                // 문제 선택 풀기 칸 추가
                val imagePath = "subj_all_random.jpg"

                val newCategories = mutableListOf<QuizCategory>()
                newCategories.add(QuizCategory(-1, "문제 선택 풀기", "images/subjects/$imagePath", "과목을 고르세요", "⭐"))

                for (subject in subjects) {
                    val id = subject.subjectId
                    val title = subject.subject
                    val bg = if (subjectThumbnailMap[title].isNullOrBlank()) "오류" else subjectThumbnailMap[title]
                    val sub = QuizCategory(id, title, bg!!, subject.detailSubject.size.toString() + " 목록", if (id % 2 == 0) "⭐" else "💡")
                    newCategories.add(sub)  // 카테고리 추가
                }
                _quizSubjectCategories.value = newCategories
                _quizSubjects.value = subjects

                // Log.d("test", "_quizSubjectCategories.value: ${_quizSubjectCategories.value}")
                // Log.d("tes", "getQuizSubjects 성공")
            } catch (e: Exception) {
                // 예외 처리
                _quizSubjects.value = emptyList()
                Log.e("tes", "getQuizSubjects error: $e")
            }
        }
    }


    fun setSubject(subject: QuizCategory) {
        _selectedSubject = subject
    }


    // 현재 선택한 대분류의 중분류 불러오기
    fun getCurrentDetailSubjects(): MutableList<QuizContentCategory> {
        val quizContentCategoryList = mutableListOf<QuizContentCategory>()
        val detailSubjects = _quizSubjects.value?.find { it.subjectId == _selectedSubject.id && it.subject == _selectedSubject.title }
        detailSubjects?.let {
            for (detailSubject in it.detailSubject) {
                quizContentCategoryList.add(QuizContentCategory(detailSubject, true))
            }
        }

        currentDetailSubjectsList = quizContentCategoryList
        return quizContentCategoryList
    }

    fun getSelectedDetailSubjects(): List<QuizContentCategory> {
        return currentDetailSubjectsList.filter { it.selected }
    }


    fun getQuizLoad(context: Context, quizType: Subcategory) {
        viewModelScope.launch {
            try {
                val token = AccountAssistant.getServerAccessToken(context)

                val userQuizResponse = UserQuizRequest(page = 0, size = 1000, sort = listOf("desc"))

                val quizResponse = when(quizType) {
                    Subcategory.ALL -> connectorRepository.getAllQuizzes(token, userQuizResponse)
                    Subcategory.USER -> connectorRepository.getUserQuizzes(token, userQuizResponse)
                    Subcategory.DEFAULT -> connectorRepository.getDefaultQuizzes(token, userQuizResponse)
                }

                _quizList.value = quizResponse.content
                val detailSubject = mutableMapOf<String, MutableSet<QuizContentCategory>>()
                for (quiz in _quizList.value!!) {
                    detailSubject.getOrPut(quiz.subject) { mutableSetOf() }.add(
                    QuizContentCategory(quiz.detailSubject, true))
                }

                _detailSubjects.value = detailSubject
                Log.d("test", detailSubjects.value!!.toString())

            } catch (e: Exception) {
                Log.e("test", "getAllQuizzes error: $e")
            }
        }
    }

    fun clickRecyclerItemCheck(title: String, selected: Boolean) {
        currentDetailSubjectsList.first { it.title == title}.selected = selected
    }

    fun setAllRandomCheck(result: Boolean) {
        if (result) {
            currentDetailSubjectsList.forEach { it.selected = true }
        } else {
            currentDetailSubjectsList.forEach { it.selected = false }
        }

    }




}