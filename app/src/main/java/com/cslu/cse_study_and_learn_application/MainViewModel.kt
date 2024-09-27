package com.cslu.cse_study_and_learn_application

import androidx.lifecycle.ViewModel

class MainViewModel: ViewModel() {

    private var _appBarHeight = 0
    val appBarHeight get() = _appBarHeight

    fun setAppBarHeight(height: Int) {
        _appBarHeight = height
    }


}