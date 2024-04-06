package com.example.cse_study_and_learn_application.ui.home

import androidx.lifecycle.ViewModel
import com.example.cse_study_and_learn_application.connector.ConnectorRepository
import com.example.cse_study_and_learn_application.connector.QuizCategoryInterface
import com.example.cse_study_and_learn_application.model.QuizCategory
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

    private val exampleService = Retrofit.Builder()
        .baseUrl("http://yourserver.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(QuizCategoryInterface::class.java)


    private val repository = ConnectorRepository(exampleService)

    private lateinit var _selectedSubject: QuizCategory
    val subject get() = _selectedSubject


    fun setSubject(subject: QuizCategory) {
        _selectedSubject = subject
    }

    fun connectServerGetQuizCategory() {

    }
}