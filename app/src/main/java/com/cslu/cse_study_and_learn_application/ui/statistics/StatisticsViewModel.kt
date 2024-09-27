package com.cslu.cse_study_and_learn_application.ui.statistics

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cslu.cse_study_and_learn_application.connector.ConnectorRepository
import com.cslu.cse_study_and_learn_application.db.QuizStats
import com.cslu.cse_study_and_learn_application.model.CategorySuccessRatio
import com.cslu.cse_study_and_learn_application.model.UserQuizStatistics
import com.cslu.cse_study_and_learn_application.ui.login.AccountAssistant
import com.cslu.cse_study_and_learn_application.utils.convertToDoubleOrZero
import kotlinx.coroutines.launch

/**
 * Statistics view model
 *
 * @constructor Create empty Statistics view model
 *
 * @author kjy
 * @since 2024-03-05
 */
class StatisticsViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is statistics Fragment"
    }
    val text: LiveData<String> = _text

    private var _ratioList = mutableListOf(CategorySuccessRatio("전체", 85.0),
        CategorySuccessRatio("알고리즘", 85.0) ,CategorySuccessRatio("자료구조", 85.0) ,CategorySuccessRatio("이산수학", 85.0) )

    private var totalSolved = MutableLiveData(0)
    private var totalCorrect = MutableLiveData(0)
    private var totalIncorrect = MutableLiveData(0)

    val ratioList get() = _ratioList
    private val connectorRepository = ConnectorRepository()

    private val _userQuizStatistics = MutableLiveData<UserQuizStatistics>()
    val userQuizStatistics: LiveData<UserQuizStatistics> = _userQuizStatistics

    fun getUserQuizStatistics(context: Context) {
        viewModelScope.launch {
            try {
                val token = AccountAssistant.getServerAccessToken(context)
                val statistics = connectorRepository.getUserQuizStatistics(token)
                _userQuizStatistics.value = statistics

                // 서버 연결 성공시 리스트에 각 과목별 정답률 저장
                userQuizStatistics.value?.correctRateBySubject?.let {
                    _ratioList.clear()
                    _ratioList.add(CategorySuccessRatio("전체", convertToDoubleOrZero(statistics.totalCorrectRate)))
                    for (ratio in it) {
                        val ratioBySubject = CategorySuccessRatio(ratio.key, convertToDoubleOrZero(ratio.value))
                        _ratioList.add(ratioBySubject)
                    }
                }



            } catch (e: Exception) {
                // 예외 처리
                Log.e("test", "getUserQuizStatistics error: $e")
            }
        }
    }

    fun getQuizCount(context: Context, quizViewModel: QuizViewModel, callback: (QuizStats) -> Unit) {
        viewModelScope.launch {
            try {
                val token = AccountAssistant.getServerAccessToken(context)
                val userInfo = connectorRepository.getUserInfo(token)

                quizViewModel.getStatsByNickname(userInfo.nickname) { quizStats ->
                    callback(quizStats!!)
                }

            } catch (e: Exception) {
                Log.e("test", "getUserInfo: $e")
            }
        }
    }

}