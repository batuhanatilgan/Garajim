package com.batuhanatilgan.garajim

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.batuhanatilgan.garajim.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        binding.btnBackRegister.setOnClickListener { finish() }

        binding.btnRegisterConfirm.setOnClickListener {
            val name = binding.etNameSurname.text.toString().trim()
            val email = binding.etRegEmail.text.toString().trim()
            val password = binding.etRegPassword.text.toString().trim()

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Lütfen tüm alanları doldurun", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val sharedPref = getSharedPreferences("GarajimUser", Context.MODE_PRIVATE)
            val editor = sharedPref.edit()

            editor.putString("userName", name)
            editor.putString("userEmail", email)
            editor.putString("userPassword", password)
            editor.putBoolean("isLoggedIn", true)
            editor.apply()

            Toast.makeText(this, "Kayıt Başarılı! Hoş geldin $name", Toast.LENGTH_LONG).show()

            // Ana Sayfaya Git
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}