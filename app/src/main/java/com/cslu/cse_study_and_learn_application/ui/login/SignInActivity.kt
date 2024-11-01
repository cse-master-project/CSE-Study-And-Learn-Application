package com.cslu.cse_study_and_learn_application.ui.login

import android.content.Intent
import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.Gravity
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.Keep
import androidx.lifecycle.lifecycleScope
import com.cslu.cse_study_and_learn_application.MainActivity
import com.cslu.cse_study_and_learn_application.connector.ConnectorRepository
import com.cslu.cse_study_and_learn_application.databinding.ActivitySignInBinding
import com.cslu.cse_study_and_learn_application.ui.other.DesignToast
import com.cslu.cse_study_and_learn_application.ui.statistics.QuizViewModel
import com.cslu.cse_study_and_learn_application.utils.Lg
import com.cslu.cse_study_and_learn_application.BuildConfig
import com.cslu.cse_study_and_learn_application.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
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

    private val connectorRepository: ConnectorRepository = ConnectorRepository()
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

            lifecycleScope.launch {
                try {
                    connectorRepository.getAccessToken(grantType, clientId, clientSecret, authCode) { accessToken, error ->
                        if (error != null) {
                            handleError("Failed to get access token: ${error.message}")
                        } else {
                            try {
                                AccountAssistant.setAccessToken(this@SignInActivity, accessToken!!)
                                lifecycleScope.launch {
                                    try {
                                        val isSignedUser = connectorRepository.isSignedUser(accessToken)
                                        if (isSignedUser) {
                                            val serverAccessToken = connectorRepository.getUserLogin(accessToken)
                                            AccountAssistant.setServerAccessToken(this@SignInActivity, serverAccessToken)
                                            moveMainActivity()
                                        } else {
                                            showSignUpDialog(accessToken)
                                        }
                                    } catch (e: Exception) {
                                        handleError("Failed to determine signed user: ${e.message}")
                                    }
                                }
                            } catch (e: Exception) {
                                handleError("Failed to set access token: ${e.message}")
                            }
                        }
                    }
                } catch (e: Exception) {
                    handleError("Unexpected error during token fetching: ${e.message}")
                } finally {
                    // 로그인 처리 후 버튼 활성화
                    _binding.btnSignIn.isEnabled = true
                }
            }
        } catch (e: ApiException) {
            handleError("Google sign-in failed: ${e.localizedMessage}")
            // 오류 발생 시 버튼 활성화
            _binding.btnSignIn.isEnabled = true
        }
    }

    private fun showSignUpDialog(accessToken: String) {
        val builder = MaterialAlertDialogBuilder(this@SignInActivity)
        val dialogLayout = layoutInflater.inflate(R.layout.dialog_signup, null)
        val editText = dialogLayout.findViewById<EditText>(R.id.et_nickname)
        val dialog = with(builder) {
            setTitle(Html.fromHtml("<b>회원 가입<b>", Html.FROM_HTML_MODE_LEGACY))
            setView(dialogLayout)
            setPositiveButton("확인") { _, _ ->
                lifecycleScope.launch {
                    try {
                        val registrationResponse = connectorRepository.getUserRegistration(
                            token = accessToken,
                            nickname = editText.text.toString()
                        )
                        AccountAssistant.setServerAccessToken(this@SignInActivity, registrationResponse)
                        DesignToast.makeText(this@SignInActivity, DesignToast.LayoutDesign.SUCCESS, "회원가입을 성공하였습니다.").show()
                        moveMainActivity()
                    } catch (e: Exception) {
                        handleError("Registration failure: ${e.message}")
                    }
                }
            }
            setNegativeButton("취소", null)
            show()
        }
        dialog.window?.let { window ->
            val params = window.attributes
            params.height = (350 * Resources.getSystem().displayMetrics.density).toInt()
            window.attributes = params
        }
    }

    private fun handleError(message: String) {
        Log.e("SignInActivity", message)
        Toast.makeText(this@SignInActivity, "로그인에 문제가 발생했습니다.", Toast.LENGTH_SHORT).show()
        // 오류 발생 시 버튼 활성화
        _binding.btnSignIn.isEnabled = true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivitySignInBinding.inflate(layoutInflater)

        Lg.d("test", "asdf",BuildConfig.server_client_id)

        // setGoogleButtonText(_binding.btnSignIn,"Google 계정으로 로그인")
        _binding.btnSignIn.text = "Google 계정으로 로그인"

        addListener()

        setContentView(_binding.root)

        val accessToken = AccountAssistant.getAccessToken(this@SignInActivity)  // 구글 액세스 토큰
        Lg.i("test", SignInActivity::class.java.name, "accessToken: $accessToken")

        if (accessToken.isNotBlank()) {
            val connectorRepository = ConnectorRepository()

            lifecycleScope.launch {
                try {
                    val serverAccessToken = connectorRepository.getUserLogin(accessToken)
                    Log.i("Server Response", "Get User Login: $serverAccessToken")
                    AccountAssistant.setServerAccessToken(this@SignInActivity, serverAccessToken)
                    Lg.i("test", SignInActivity::class.java.name, "serverAccessToken: $serverAccessToken")
                    moveMainActivity()

                } catch (e: Exception) {
                    Log.e("SignInActivity", "로그인 실패", e)
                    // 구글 액세스 토큰 기간이 지났으면 회원가입이 되어있더라도 로그인에 실패할 수 있음
                    requestGoogleLogin()
                }
            }
            // Log.i("Server Response", "serverAccessToken: ${AccountAssistant.getServerAccessToken(this)}")
        }
    }

    private fun addListener() {
        _binding.btnSignIn.setOnClickListener {
            // 로그인 시작 시 버튼 비활성화
            _binding.btnSignIn.isEnabled = false
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

    private fun setGoogleButtonText(loginButton: SignInButton, buttonText: String){
        var i = 0
        while (i < loginButton.childCount){
            var v = loginButton.getChildAt(i)
            if (v is TextView) {
                var tv = v
                tv.text = buttonText
                tv.gravity = Gravity.CENTER
                return
            }
            i++

        }
    }
}
