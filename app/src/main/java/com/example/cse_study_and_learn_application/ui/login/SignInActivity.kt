package com.example.cse_study_and_learn_application.ui.login

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.Keep
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.example.cse_study_and_learn_application.BuildConfig
import com.example.cse_study_and_learn_application.MainActivity
import com.example.cse_study_and_learn_application.R
import com.example.cse_study_and_learn_application.connector.ConnectorRepository
import com.example.cse_study_and_learn_application.databinding.ActivitySignInBinding
import com.example.cse_study_and_learn_application.ui.setting.SettingViewModel
import com.example.cse_study_and_learn_application.ui.statistics.QuizViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Scope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch

/**
 * Sign in activity
 *
 * @constructor 로그인을 담당하는 액티비티
 *
 * 변경점 2024-06-30
 * - 이미 서버에 로그인 정보가 있는경우 닉네임을 받지 않고 바로 로그인되도록 변경
 */
@Keep
class SignInActivity : AppCompatActivity() {

    private lateinit var _binding: ActivitySignInBinding

    private val quizViewModel: QuizViewModel by viewModels()

    private val googleSignInClient: GoogleSignInClient by lazy { getGoogleClient() }
    private val googleAuthLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)

        try {
            val account = task.getResult(ApiException::class.java)
            val email = account.email

            AccountAssistant.setUserEmail(this, email!!)

            val grantType = "authorization_code"
            val clientId = BuildConfig.server_client_id
            val clientSecret = BuildConfig.server_client_secret
            val authCode = account.serverAuthCode

            val connectorRepository = ConnectorRepository()
            lifecycleScope.launch {
                try {
                    val connectorRepository = ConnectorRepository()
                    connectorRepository.getAccessToken(grantType, clientId, clientSecret, authCode) { accessToken, error ->
                        if (error != null) {
                            throw Exception("accessTokenResponse failure")
                        } else {
                            AccountAssistant.setAccessToken(this@SignInActivity, accessToken!!)
                            lifecycleScope.launch {
                                val isSignedUser = connectorRepository.isSignedUser(accessToken)
                                if (isSignedUser) {
                                    val serverAccessToken = connectorRepository.getUserLogin(accessToken)
                                    AccountAssistant.setServerAccessToken(this@SignInActivity, serverAccessToken)
                                    moveMainActivity()
                                } else {
                                    val builder = MaterialAlertDialogBuilder(this@SignInActivity)
                                    val dialogLayout = layoutInflater.inflate(R.layout.dialog_signup, null)
                                    val editText = dialogLayout.findViewById<EditText>(R.id.et_nickname)
                                    val dialog = with (builder) {
                                        setTitle(Html.fromHtml("<b>회원 가입<b>", Html.FROM_HTML_MODE_LEGACY))
                                        setView(dialogLayout)
                                        setPositiveButton("확인") { _, _ ->
                                            lifecycleScope.launch {
                                                val registrationResponse = connectorRepository.getUserRegistration(accessToken, editText.text.toString())
                                                if (registrationResponse) {
                                                    Toast.makeText(this@SignInActivity, "회원가입 성공", Toast.LENGTH_SHORT).show()
                                                    recreate()
                                                    moveMainActivity()
                                                } else {
                                                    throw Exception("registrationResponse failure")
                                                }
                                            }
                                        }
                                        setNegativeButton("취소", null)
                                        show()
                                    }
                                    dialog.window?.let { window ->
                                        val params = window.attributes
                                        params.height = (350* Resources.getSystem().displayMetrics.density).toInt()
                                        window.attributes = params
                                    }
                                }

                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.e("SignInActivity", "호출 실패", e)
                }
            }
        } catch (e: ApiException) {
            Log.e(MainActivity::class.java.simpleName, e.stackTraceToString())
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivitySignInBinding.inflate(layoutInflater)

        addListener()

        setContentView(_binding.root)

        val accessToken = AccountAssistant.getAccessToken(this@SignInActivity)  // 구글 액세스 토큰

        if (accessToken.isNotBlank()) {
            val connectorRepository = ConnectorRepository()

            lifecycleScope.launch {
                try {
                    val serverAccessToken = connectorRepository.getUserLogin(accessToken)
                    Log.i("Server Response", "Get User Login: $serverAccessToken")
                    AccountAssistant.setServerAccessToken(this@SignInActivity, serverAccessToken)

                    lifecycleScope.launch {
                        try {
                            val userInfo = connectorRepository.getUserInfo(AccountAssistant.getServerAccessToken(this@SignInActivity))   // 유저 정보 불러오기
                            // Log.d("test", "userInfo: $userInfo")
                            AccountAssistant.nickname = userInfo.nickname
                            quizViewModel.getOrInsertStats(userInfo.nickname, 0, 0) { quizStats ->
                                Log.d("test", "getOrInsertStats: $quizStats")
                            }
                            moveMainActivity()

                        } catch (e: Exception) {
                            Log.e("test", "getUserInfo: $e")
                        }
                    }

                } catch (e: Exception) {
                    Log.e("SignInActivity", "로그인 실패", e)
                    // 구글 액세스 토큰 기간이 지났으면 회원가입이 되어있더라도 로그인에 실패할 수 있음
                    requestGoogleLogin()
                }
            }
            Log.i("Server Response", "serverAccessToken: ${AccountAssistant.getServerAccessToken(this)}")
        }
    }

    private fun addListener() {
        _binding.btnSignIn.setOnClickListener {
            requestGoogleLogin()
        }
    }

    private fun requestGoogleLogin() {
        googleSignInClient.signOut()
        val signInIntent = googleSignInClient.signInIntent
        googleAuthLauncher.launch(signInIntent)
    }

    private fun getGoogleClient(): GoogleSignInClient {
        val googleSignInOption = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestScopes(Scope("openid"))
            .requestServerAuthCode(BuildConfig.server_client_id)
            .requestEmail()
            .build()

        return GoogleSignIn.getClient(this, googleSignInOption)
    }

    private fun moveMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
