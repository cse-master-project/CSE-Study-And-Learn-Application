package com.example.cse_study_and_learn_application.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.cse_study_and_learn_application.model.Subject

/**
 * Home view model
 *
 * @constructor Create empty Home view model
 *
 * @author kjy
 * @since 2024-03-05
 */
class HomeViewModel : ViewModel() {


    private lateinit var _selectedSubject: Subject
    val subject get() = _selectedSubject


    fun setSubject(subject: Subject) {
        _selectedSubject = subject
    }
}