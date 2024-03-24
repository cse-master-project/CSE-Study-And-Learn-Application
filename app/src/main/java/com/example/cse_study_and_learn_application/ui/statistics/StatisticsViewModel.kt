package com.example.cse_study_and_learn_application.ui.statistics

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.cse_study_and_learn_application.model.CategorySuccessRatio

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

    private val _testRatios = mutableListOf(CategorySuccessRatio("전체", 85),
        CategorySuccessRatio("알고리즘", 85) ,CategorySuccessRatio("자료구조", 85) ,CategorySuccessRatio("이산수학", 85) )


    val testRatio get() = _testRatios
}