package com.example.cse_study_and_learn_application.ui.statistics

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

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
}