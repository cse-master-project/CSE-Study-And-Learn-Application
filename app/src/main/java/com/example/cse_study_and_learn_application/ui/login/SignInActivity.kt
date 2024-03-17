package com.example.cse_study_and_learn_application.ui.login

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.GetPasswordOption
import androidx.credentials.PasswordCredential
import androidx.credentials.exceptions.GetCredentialException
import com.example.cse_study_and_learn_application.MainActivity
import com.example.cse_study_and_learn_application.R
import com.example.cse_study_and_learn_application.databinding.ActivitySignInBinding
import com.google.android.gms.fido.fido2.api.common.PublicKeyCredential
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

/**
 * Sign in activity
 * 기능은 미구현
 *
 * @constructor Create empty Sign in activity
 * @author JYH
 * @since 2024-03-17
 *
 */
class SignInActivity : AppCompatActivity() {

    private lateinit var _binding: ActivitySignInBinding

    private var responseJson: String? = ""
    private val mainScope = MainScope()

    private val getPasswordOption = GetPasswordOption()

    private var googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
        .setFilterByAuthorizedAccounts(false)
        .setAutoSelectEnabled(false)
        .setServerClientId(R.string.google_login_client_id.toString())
        .build()

    private val request: GetCredentialRequest = GetCredentialRequest.Builder()
        .addCredentialOption(googleIdOption)
        .addCredentialOption(getPasswordOption)
        .build()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivitySignInBinding.inflate(layoutInflater)
        addListener()
        setContentView(_binding.root)
    }

    private fun addListener() {
        _binding.btnSignIn.setOnClickListener {
            mainScope.launch {
                signIn()
            }
        }
    }


    private suspend fun signIn() {
        coroutineScope {
            launch {
                try {
                    val credentialManager: CredentialManager = CredentialManager.create(context = this@SignInActivity.applicationContext)
                    val response = credentialManager.getCredential(
                        context = this@SignInActivity,
                        request = request
                    )
                    handleSignIn(response)

                } catch (e: GetCredentialException) {
                    Log.e("signIn", "Received an invalid google id token response", e)
                }
            }
        }
    }

    private fun handleSignIn(result: GetCredentialResponse) {
        when(val credential = result.credential) {
            is PublicKeyCredential -> {
                responseJson = credential.authenticatorAttachment
            }
            is PasswordCredential -> {
                val userName = credential.id
                val password = credential.password
            }
            is CustomCredential -> {
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    try {
                        val googleIdTokenCredential = GoogleIdTokenCredential
                            .createFrom(credential.data)
                    } catch (e: GoogleIdTokenParsingException) {
                        Log.e("handleSignIn", "Unexpected type of credential", e)
                    }
                }
            }

            else -> {
                Log.e("handleSignIn", "Unexpected type of credential")
            }
        }
    }

    private fun moveMainActivity() {
        this.run {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}