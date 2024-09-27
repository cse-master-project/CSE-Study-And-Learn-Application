package com.cslu.cse_study_and_learn_application.ui.statistics

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.cslu.cse_study_and_learn_application.db.QuizDatabase
import com.cslu.cse_study_and_learn_application.db.QuizStats
import kotlinx.coroutines.launch

class QuizViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = QuizDatabase.getInstance(application).quizStatsDao

    fun insertOrUpdate(nickname: String, correctAnswers: Int, wrongAnswers: Int) {
        viewModelScope.launch {
            val stats = QuizStats(nickname = nickname, correctAnswers = correctAnswers, wrongAnswers = wrongAnswers)
            dao.insertOrUpdate(stats)
        }
    }

    fun getStatsByNickname(nickname: String, callback: (QuizStats?) -> Unit) {
        viewModelScope.launch {
            val stats = dao.getStatsByNickname(nickname)
            callback(stats)
        }
    }

    fun clear(nickname: String) {
        viewModelScope.launch {
            dao.clear(nickname)
        }
    }

    fun getOrInsertStats(nickname: String, correctAnswers: Int, wrongAnswers: Int, callback: (QuizStats) -> Unit) {
        viewModelScope.launch {
            var stats = dao.getStatsByNickname(nickname)
            if (stats == null) {
                stats = QuizStats(nickname = nickname, correctAnswers = correctAnswers, wrongAnswers = wrongAnswers)
                dao.insertOrUpdate(stats)
            }
            callback(stats)
        }
    }
}
