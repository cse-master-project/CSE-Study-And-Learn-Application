package com.example.cse_study_and_learn_application.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cse_study_and_learn_application.connector.ConnectorRepository
import com.example.cse_study_and_learn_application.model.QuizCategory
import com.example.cse_study_and_learn_application.model.UserQuizRequest
import com.example.cse_study_and_learn_application.model.UserQuizResponse
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Home view model
 *
 * @constructor Create empty Home view model
 *
 * @author kjy
 * @since 2024-03-05
 */
class HomeViewModel : ViewModel() {

    private val connectorRepository = ConnectorRepository()
    private val _userQuizResponses = MutableLiveData<List<UserQuizResponse>>()
    val userQuizResponses: LiveData<List<UserQuizResponse>> get() = _userQuizResponses

    private lateinit var _selectedSubject: QuizCategory
    val subject get() = _selectedSubject

    fun connectServerGetUserQuizzes() {
        viewModelScope.launch {
            val userQuizRequest = UserQuizRequest(page = 2, size = 2, sort = listOf("string"))
            try {
                val responses = connectorRepository.getUserQuizzes(userQuizRequest)
                _userQuizResponses.value = responses
            } catch (e: Exception) {
                Log.d("test", "connectServerGetUserQuizzes 서버 연결 실패")
                e.printStackTrace()
            }
        }
    }


    fun setSubject(subject: QuizCategory) {
        _selectedSubject = subject
    }

}