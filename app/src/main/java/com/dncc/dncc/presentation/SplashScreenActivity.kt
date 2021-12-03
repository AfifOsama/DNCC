package com.dncc.dncc.presentation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import com.dncc.dncc.MainActivity
import com.dncc.dncc.presentation.login.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint
import splitties.activities.start

@AndroidEntryPoint
class SplashScreenActivity : AppCompatActivity() {

    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initiateObserver()
    }

    private fun initiateObserver() {
        viewModel.loginState.observe(this, {
            checkIntent(it)
        })
    }

    private fun checkIntent(isLogin: Boolean) {
        Log.d("SplashScreenActivity", "checkIntent: $isLogin")
        if (isLogin) {
            //if already login
            start<MainActivity>()
            finish()
        } else {
            start<MainActivity>()
            finish()
        }
    }
}
