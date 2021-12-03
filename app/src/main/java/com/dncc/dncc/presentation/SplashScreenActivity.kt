package com.dncc.dncc.presentation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.dncc.dncc.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import splitties.activities.start

@AndroidEntryPoint
class SplashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        start<MainActivity>()
        finish()
    }
}
