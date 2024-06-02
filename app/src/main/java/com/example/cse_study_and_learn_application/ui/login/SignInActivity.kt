package com.example.cse_study_and_learn_application.ui.login

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.Keep
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.cse_study_and_learn_application.BuildConfig
import com.example.cse_study_and_learn_application.MainActivity
import com.example.cse_study_and_learn_application.connector.ConnectorRepository
import com.example.cse_study_and_learn_application.databinding.ActivitySignInBinding
import com.example.cse_study_and_learn_application.ui.setting.SettingViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Scope
import kotlinx.coroutines.launch

/**
 * Sign in activity
 *
 * @constructor 로그인을 담당하는 액티비티
 */
@Keep
class SignInActivity : AppCompatActivity() {

    private lateinit var _binding: ActivitySignInBinding
    private lateinit var settingViewModel: SettingViewModel

    private val googleSignInClient: GoogleSignInClient by lazy { getGoogleClient() }
    private val googleAuthLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)

        try {
            val account = task.getResult(ApiException::class.java)
            val email = account.email

            AccountAssistant.setUserEmail(this, email!!)
            Log.d("test", "User Email: $email")

            val grantType = "authorization_code"
            val clientId = BuildConfig.server_client_id
            val clientSecret = BuildConfig.server_client_secret
            val authCode = account.serverAuthCode

            lifecycleScope.launch {
                try {
                    val connectorRepository = ConnectorRepository()
                    connectorRepository.getAccessToken(grantType, clientId, clientSecret, authCode) { accessToken, error ->
                        if (error != null) {
                            Log.e("accessTokenResponse", "accessTokenResponse 호출 실패", error)
                        } else {
                            Log.d("test", "accessToken response: $accessToken")
                            AccountAssistant.setAccessToken(this@SignInActivity, accessToken!!)

                            lifecycleScope.launch {
                                try {
                                    val registrationResponse = connectorRepository.getUserRegistration(accessToken, "테스트 121")
                                    if (registrationResponse) {
                                        Toast.makeText(this@SignInActivity, "회원가입 성공", Toast.LENGTH_SHORT).show()
                                        moveMainActivity()
                                    }
                                } catch (e: Exception) {
                                    Log.e("test", "registrationResponse 호출 실패", e)
                                    moveMainActivity()
                                }
                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.e("accessTokenResponse", "accessTokenResponse 호출 실패", e)
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

        val accessToken = AccountAssistant.getAccessToken(this@SignInActivity)

        if (accessToken.isNotBlank()) {
            val connectorRepository = ConnectorRepository()

            lifecycleScope.launch {
                try {
                    val serverAccessToken = connectorRepository.getUserLogin(accessToken)

                    AccountAssistant.setServerAccessToken(this@SignInActivity, serverAccessToken)
                    Toast.makeText(this@SignInActivity, "로그인 성공!", Toast.LENGTH_SHORT).show()
                    moveMainActivity()
                } catch (e: Exception) {
                    Log.d("test", "로그인 실패 $e")
                }
            }
            Log.d("token", "serverAccessToken auto login test: ${AccountAssistant.getServerAccessToken(this)}")
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
