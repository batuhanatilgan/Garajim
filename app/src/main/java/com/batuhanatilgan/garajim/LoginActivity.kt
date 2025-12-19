package com.batuhanatilgan.garajim

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.batuhanatilgan.garajim.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        // Kayıt Ol'a basınca
        binding.txtRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        // Giriş Yap'a basınca
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            // Hafızadaki bilgileri çek
            val sharedPref = getSharedPreferences("GarajimUser", Context.MODE_PRIVATE)
            val savedEmail = sharedPref.getString("userEmail", null)
            val savedPassword = sharedPref.getString("userPassword", null)

            // Kontrol Et
            if (email == savedEmail && password == savedPassword) {
                // Giriş Başarılı -> Durumu kaydet
                sharedPref.edit().putBoolean("isLoggedIn", true).apply()

                Toast.makeText(this, "Hoş geldin!", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                if (email == "admin" && password == "123456") {
                    sharedPref.edit().putBoolean("isLoggedIn", true).apply()
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this, "Hatalı E-posta veya Şifre! (Önce Kayıt Olun)", Toast.LENGTH_LONG).show()
                }
            }
        }

        binding.btnGoogleLogin.setOnClickListener {
            Toast.makeText(this, "Demo sürümünde bu özellik kapalıdır.", Toast.LENGTH_SHORT).show()
        }
    }
}