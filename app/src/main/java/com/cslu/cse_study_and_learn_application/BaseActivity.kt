package com.cslu.cse_study_and_learn_application;

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.cslu.cse_study_and_learn_application.ui.other.DesignToast

open class BaseActivity : AppCompatActivity() {

        override fun onCreate(savedInstanceState: Bundle?) {
                super.onCreate(savedInstanceState)

                if (!isNetworkAvailable()) {
                        showErrorMessage("인터넷 연결에 문제가 있습니다.")
                        disableUserInteraction()
                }
        }

        protected fun isNetworkAvailable(): Boolean {
                val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val network = connectivityManager.activeNetwork ?: return false
                val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
                return activeNetwork.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        }

        protected fun showErrorMessage(message: String) {
                DesignToast.makeText(this, DesignToast.LayoutDesign.INFO, message).show()
        }

        protected fun disableUserInteraction() {
                window.decorView.rootView.isEnabled = false
        }
}

