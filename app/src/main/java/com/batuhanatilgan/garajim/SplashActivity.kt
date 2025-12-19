package com.batuhanatilgan.garajim

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        supportActionBar?.hide()

        Handler(Looper.getMainLooper()).postDelayed({
            checkStatus()
        }, 3000)
    }

    private fun checkStatus() {
        // Tanıtım kontrolü
        val prefs = getSharedPreferences("GarajimPrefs", Context.MODE_PRIVATE)
        val isFirstTime = prefs.getBoolean("isFirstTime", true)

        if (isFirstTime) {
            startActivity(Intent(this, OnboardingActivity::class.java))
            finish()
            return
        }
        val userPrefs = getSharedPreferences("GarajimUser", Context.MODE_PRIVATE)
        val isLoggedIn = userPrefs.getBoolean("isLoggedIn", false)

        if (isLoggedIn) {
            startActivity(Intent(this, MainActivity::class.java))
        } else {
            startActivity(Intent(this, LoginActivity::class.java))
        }
        finish()
    }
}