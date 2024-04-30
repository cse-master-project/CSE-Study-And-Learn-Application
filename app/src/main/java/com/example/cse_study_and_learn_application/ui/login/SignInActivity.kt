package com.example.cse_study_and_learn_application.ui.login

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
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

                    val accessTokenResponse = ConnectorRepository().getAccessToken(grantType, clientId, clientSecret, authCode)

                } catch (e: Exception) {
                    Log.e("getAccessToken", "getAccessToken 호출 실패", e)
                }
            }


            if (authCode.isNullOrBlank()) {
                finish()
                Log.d("token", "토큰 없음?")
            } else {
                // 자동 로그인 추가
                AccountAssistant.setUserToken(this@SignInActivity, authCode)
                Log.d("token", authCode.toString())
                val testNickName = "a2a2g4"
                val connectorRepository = ConnectorRepository()

               lifecycleScope.launch {
                   try {
                       val responses = connectorRepository.getUserRegistration(authCode, testNickName)
                       if (responses) {
                           Log.d("test", "회원가입 성공")
                       } else {
                           Log.d("test", "회원가입 실패?")
                       }
                   } catch (e: Exception) {
                       Log.d("test", "getUserRegistration 서버 연결 실패")
                       e.printStackTrace()
                   }
               }

                setLoginSuccessful()

            }



            moveMainActivity()

        } catch (e: ApiException) {
            Log.e(MainActivity::class.java.simpleName, e.stackTraceToString())
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivitySignInBinding.inflate(layoutInflater)
        addListener()

        setContentView(_binding.root)

        // 로그인 성공한적 있으면 자동으로 매인 액티비티 이동
        if (isLoginSuccessful()) { // SharedPreferences에 저장된 로그인 성공 여부 확인
            Log.d("test", "isLoginSuccessful: 이미 로그인한적 있음")
            val testNickName = "a2a2g4"
            val connectorRepository = ConnectorRepository()

            lifecycleScope.launch {
                try {
                    val token = AccountAssistant.getUserToken(this@SignInActivity)
                    Log.d("test", "token: $token")
                    val responses = connectorRepository.getUserLogin(token)
                    if (responses) {
                        Log.d("test", "회원가입 성공")
                    } else {
                        Log.d("test", "회원가입 실패?")
                    }
                } catch (e: Exception) {
                    Log.d("test", "getUserLogin 서버 연결 실패")
                    e.printStackTrace()
                }
            }
            moveMainActivity()
        }
    }

    /**
     * Set login successful
     * 로그인 성공했다고 저장
     */
    private fun setLoginSuccessful() {
        val preferences = getSharedPreferences(AccountAssistant.PREFS_NAME, Context.MODE_PRIVATE)
        preferences.edit().apply {
            putBoolean(AccountAssistant.KEY_IS_LOGIN, true)
            apply()
        }
    }

    /**
     * Is login successful
     * 로그인 성공한적이 있는지 불러옴
     * @return
     */
    private fun isLoginSuccessful(): Boolean {
        val preferences = getSharedPreferences(AccountAssistant.PREFS_NAME, Context.MODE_PRIVATE)
        return preferences.getBoolean(AccountAssistant.KEY_IS_LOGIN, false)
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