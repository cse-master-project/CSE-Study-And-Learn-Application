package com.cslu.cse_study_and_learn_application.ui.setting

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cslu.cse_study_and_learn_application.connector.ConnectorRepository
import com.cslu.cse_study_and_learn_application.model.UserInfo
import com.cslu.cse_study_and_learn_application.ui.login.AccountAssistant
import com.cslu.cse_study_and_learn_application.utils.HighlightHelper
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

    private val _updateUserInfoResult = MutableLiveData(false)
    val updateUserInfoResult: LiveData<Boolean> = _updateUserInfoResult

    private var _userInfo = MutableLiveData<UserInfo>()
    val userInfo: LiveData<UserInfo> = _userInfo

    fun logout(context: Context) {
        viewModelScope.launch {
            try {
                val token = AccountAssistant.getServerAccessToken(context)
                val isSuccess = connectorRepository.logoutUser(token)
                if (isSuccess) {
                    // 로그아웃 성공 처리
                    AccountAssistant.clearAllPreferences(context)
                    HighlightHelper.clearHelpShown(context) // 도움말 초기화
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
                val token = AccountAssistant.getServerAccessToken(context)
                val isSuccess = connectorRepository.deactivateUser(token)
                if (isSuccess) {
                    // 회원탈퇴 성공 처리
                    // 로컬에 저장된 사용자 정보 및 토큰 삭제
                    AccountAssistant.clearAllPreferences(context)
                    HighlightHelper.clearHelpShown(context) // 도움말 초기화
                    _deactivateResult.value = true
                } else {
                    // 회원탈퇴 실패 처리
                    _deactivateResult.value = false
                }
            } catch (e: Exception) {
                // 예외 처리
                _deactivateResult.value = false
                // Log.d("test", "deactivate: $e")
            }
        }
    }


    fun updateUserInfo(context: Context, nickname: String): Boolean {
        viewModelScope.launch {
            try {
                val token = AccountAssistant.getServerAccessToken(context)
                val isSuccess = connectorRepository.setUserNickname(token, nickname)
                _updateUserInfoResult.value = isSuccess
            } catch (e: Exception) {
                _updateUserInfoResult.value = false
                e.printStackTrace()
            }
        }
        return _updateUserInfoResult.value!!
    }

    fun closeEditFragment() {
        _updateUserInfoResult.value = false
    }

    fun getUserInfo(context: Context) {
        viewModelScope.launch {
            try {
                val token = AccountAssistant.getServerAccessToken(context)
                val userInfo = connectorRepository.getUserInfo(token)
                // Log.d("test", userInfo.nickname)
                _userInfo.value = userInfo

            } catch (e: Exception) {
                Log.e("test", "getUserInfo: $e")
            }
        }
    }
}