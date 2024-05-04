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
import com.example.cse_study_and_learn_application.model.QuizCategory
import com.example.cse_study_and_learn_application.model.QuizSubject
import com.example.cse_study_and_learn_application.model.UserQuizRequest
import com.example.cse_study_and_learn_application.model.UserQuizResponse
import com.example.cse_study_and_learn_application.ui.login.AccountAssistant
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
    val quizSubjects: LiveData<List<QuizSubject>> = _quizSubjects

    private val _quizSubjectCategories = MutableLiveData<MutableList<QuizCategory>>()
    val quizSubjectCategories: LiveData<MutableList<QuizCategory>> = _quizSubjectCategories

    private var subjectThumbnailMap = mutableMapOf<String, String>()

    fun connectServerGetUserQuizzes() {
        viewModelScope.launch {
            val userQuizRequest = UserQuizRequest(page = 2, size = 2, sort = listOf("string"))
            try {
                val token = AccountAssistant.getAuthCode(context)
                val responses = connectorRepository.getUserQuizzes(token, userQuizRequest)
                _userQuizResponses.value = responses
            } catch (e: Exception) {
                Log.d("test", "connectServerGetUserQuizzes 서버 연결 실패")
                e.printStackTrace()
            }
        }
    }

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
                val token = AccountAssistant.getUserToken(context)
                val subjects = connectorRepository.getQuizSubjects(token)

                // 문제 선택 풀기 칸 추가
                val imagePath = "subj_all_random.jpg"

                val newCategories = mutableListOf<QuizCategory>()
                newCategories.add(QuizCategory(-1, "문제 선택 풀기", "images/subjects/$imagePath", "눌러서 문제를 고르세요", "⭐"))

                for (subject in subjects) {
                    val id = subject.subjectId
                    val title = subject.subject
                    val bg = if (subjectThumbnailMap[title].isNullOrBlank()) "오류" else subjectThumbnailMap[title]
                    val sub = QuizCategory(id, title, bg!!, subject.detailSubject.size.toString(), if (id % 2 == 0) "⭐" else "💡")
                    newCategories.add(sub)  // 카테고리 추가
                }
                _quizSubjectCategories.value = newCategories
                _quizSubjects.value = subjects

            } catch (e: Exception) {
                // 예외 처리
                _quizSubjects.value = emptyList()
            }
        }
    }


    fun setSubject(subject: QuizCategory) {
        _selectedSubject = subject
    }

}