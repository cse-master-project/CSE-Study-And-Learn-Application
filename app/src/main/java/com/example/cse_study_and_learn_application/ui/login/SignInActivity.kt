package com.example.cse_study_and_learn_application.ui.login

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.Keep
import androidx.lifecycle.lifecycleScope
import com.example.cse_study_and_learn_application.BuildConfig
import com.example.cse_study_and_learn_application.MainActivity
import com.example.cse_study_and_learn_application.connector.ConnectorRepository
import com.example.cse_study_and_learn_application.databinding.ActivitySignInBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Scope
import kotlinx.coroutines.launch

/**
 * Sign in activity
 * 기능은 미구현
 *
 * @constructor Create empty Sign in activity
 * @author JYH, KJY
 * @since 2024-03-17
 *
 */

@Keep
class SignInActivity : AppCompatActivity() {

    private lateinit var _binding: ActivitySignInBinding

//    private val context = this
//    private val coroutineScope = MainScope()

    private val googleSignInClient: GoogleSignInClient by lazy { getGoogleClient() }
    private val googleAuthLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        Log.d("result", result.data.toString())
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)

        try {
            val account = task.getResult(ApiException::class.java)

            val grantType = "authorization_code"
            val clientId = BuildConfig.server_client_id
            val clientSecret = BuildConfig.server_client_secret
            val authCode = account.serverAuthCode

            lifecycleScope.launch {
                try {
                    Log.d("test", "client ID: $clientId")
                    Log.d("test", "client Secret: $clientSecret")
                    Log.d("test", "serverAuthToken: $authCode")

                    // 여기 액세스 토큰 받아오는 구조 변경함
                    // 원래 구조는 비동기 데이터를 받아올 수 없음
                    // 액세스 토큰을 받으면 저장함
                    val connectorRepository = ConnectorRepository()
                    connectorRepository.getAccessToken(grantType, clientId, clientSecret, authCode) { accessToken, error ->
                        if (error != null) {
                            // Handle error
                            Log.e("accessTokenResponse", "accessTokenResponse 호출 실패", error)
                        } else {
                            // Use accessToken
                            Log.d("test", "accessToken response: $accessToken")
                            AccountAssistant.setAccessToken(this@SignInActivity, accessToken!!)

                            // 액세스 토큰으로 서버 회원가입
                            lifecycleScope.launch {
                                try {
                                    val registrationResponse = connectorRepository.getUserRegistration(accessToken, "테스트 121")
                                    if (registrationResponse) {
                                        Toast.makeText(this@SignInActivity, "회원가입 성공", Toast.LENGTH_SHORT).show()
                                        moveMainActivity()  // 메인 액티비티로 이동
                                    }

                                } catch (e: Exception) {
                                    Log.e("test", "registrationResponse 호출 실패", e)
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

        Log.d("test", "accessToken auto login test: $accessToken")
        if (accessToken.isNotBlank()) {
            val connectorRepository = ConnectorRepository()
            lifecycleScope.launch {
                try {
                    val serverAccessToken = connectorRepository.getUserLogin(accessToken)
                    Log.d("test", "serverAccessToken auto login test: $serverAccessToken")
                    AccountAssistant.setServerAccessToken(this@SignInActivity, serverAccessToken)
                    Toast.makeText(this@SignInActivity, "로그인 성공!", Toast.LENGTH_SHORT).show()
                    moveMainActivity()  // Access token이 저장되어 있으면 바로 메인 액티비티로 넘어감
                } catch (e: Exception) {
                    Log.d("test", "로그인 실패 $e")
                }
            }
        }
    }


    private fun addListener() {
        _binding.btnSignIn.setOnClickListener {
            requestGoogleLogin()
        }
    }

    private fun requestGoogleLogin() {
/*        try {
            val credentialManager = CredentialManager.create(context)

            val rawNonce = UUID.randomUUID().toString()
            val bytes = rawNonce.toByteArray()
            val md = MessageDigest.getInstance("SHA-256")
            val digest = md.digest(bytes)
            val hashedNonce = digest.fold("") { str, it -> str + "%02x".format(it) }

            val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(R.string.google_login_server_client_id.toString())
                .setNonce(hashedNonce)
                .build()

            val request: androidx.credentials.GetCredentialRequest =
                androidx.credentials.GetCredentialRequest.Builder()
                    .addCredentialOption(googleIdOption)
                    .build()

            coroutineScope.launch {
                val result = credentialManager.getCredential(
                    request = request,
                    context = context,
                )

                val credential = result.credential

                val googleIdTokenCredential = GoogleIdTokenCredential
                    .createFrom(credential.data)

                val googleIdToken = googleIdTokenCredential.idToken

                Log.i("a", googleIdToken)

                Toast.makeText(context, "Sign In!", Toast.LENGTH_SHORT).show()
            }
        } catch (e: GetCredentialException) {
            Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
        } catch (e: GoogleIdTokenParsingException) {
            Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
        }*/

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
        this.run {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}