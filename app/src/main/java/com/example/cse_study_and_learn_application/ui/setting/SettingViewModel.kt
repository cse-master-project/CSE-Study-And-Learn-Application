package com.example.cse_study_and_learn_application.ui.setting

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * Setting view model
 *
 * @constructor Create empty Setting view model
 *
 * @author kjy
 * @since 2024-03-05
 */
class SettingViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is setting Fragment"
    }
    val text: LiveData<String> = _text
}