package com.example.cse_study_and_learn_application.ui.setting

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cse_study_and_learn_application.connector.ConnectorRepository
import com.example.cse_study_and_learn_application.ui.login.AccountAssistant
import kotlinx.coroutines.launch

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


    private val connectorRepository = ConnectorRepository()

    private val _logoutResult = MutableLiveData<Boolean>()
    val logoutResult: LiveData<Boolean> = _logoutResult

    private val _deactivateResult = MutableLiveData<Boolean>()
    val deactivateResult: LiveData<Boolean> = _deactivateResult

    private val _updateUserInfoResult = MutableLiveData<Boolean>()
    val updateUserInfoResult: LiveData<Boolean> = _updateUserInfoResult

    fun logout(context: Context) {
        viewModelScope.launch {
            try {
                val token = AccountAssistant.getAccessToken(context)
                val isSuccess = connectorRepository.logoutUser(token)
                if (isSuccess) {
                    // 로그아웃 성공 처리
                    // 로컬에 저장된 사용자 정보 및 토큰 삭제
                    AccountAssistant.getAccessToken(context)
                    _logoutResult.value = true
                } else {
                    // 로그아웃 실패 처리
                    _logoutResult.value = false
                }
            } catch (e: Exception) {
                // 예외 처리
                _logoutResult.value = false
            }
        }
    }

    fun deactivate(context: Context) {
        viewModelScope.launch {
            try {
                val token = AccountAssistant.getAccessToken(context)
                val isSuccess = connectorRepository.deactivateUser(token)
                if (isSuccess) {
                    // 회원탈퇴 성공 처리
                    // 로컬에 저장된 사용자 정보 및 토큰 삭제
                    AccountAssistant.getAccessToken(context)
                    _deactivateResult.value = true
                } else {
                    // 회원탈퇴 실패 처리
                    _deactivateResult.value = false
                }
            } catch (e: Exception) {
                // 예외 처리
                _deactivateResult.value = false
            }
        }
    }

    fun updateUserInfo(context: Context, nickname: String) {
        viewModelScope.launch {
            try {
                val token = AccountAssistant.getAccessToken(context)
                val isSuccess = connectorRepository.setUserNickname(token, nickname)
                if (isSuccess) {
                    // 회원정보 수정 성공 처리
                    _updateUserInfoResult.value = true
                } else {
                    // 회원정보 수정 실패 처리
                    _updateUserInfoResult.value = false
                }
            } catch (e: Exception) {
                // 예외 처리
                _updateUserInfoResult.value = false
            }
        }
    }

    fun closeEditFragment() {
        _updateUserInfoResult.value = false
    }
}