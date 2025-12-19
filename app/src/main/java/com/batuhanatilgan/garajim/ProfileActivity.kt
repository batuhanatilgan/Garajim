package com.batuhanatilgan.garajim

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.batuhanatilgan.garajim.databinding.ActivityProfileBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        binding.btnBackProfile.setOnClickListener { finish() }
        binding.btnLoginLogout.setOnClickListener {
        }
        loadStats()
    }

    private fun loadStats() {
        lifecycleScope.launch(Dispatchers.IO) {
            val db = AppDatabase.getDatabase(this@ProfileActivity)
            // Veritabanından sayıları çek
            val cars = db.carDao().getAllCars()
            var totalMaintenanceCount = 0
            for (car in cars) {
                val maintenances = db.maintenanceDao().getMaintenancesByCarId(car.id)
                totalMaintenanceCount += maintenances.size
            }
            val sharedPref = getSharedPreferences("GarajimUser", MODE_PRIVATE)
            val savedName = sharedPref.getString("userName", "Misafir Kullanıcı")
            val savedEmail = sharedPref.getString("userEmail", "Giriş Yok")

            withContext(Dispatchers.Main) {
                binding.txtTotalCars.text = cars.size.toString()
                binding.txtTotalMaintenance.text = totalMaintenanceCount.toString()

                // İsmi ve Maili ekrana yaz
                binding.txtProfileName.text = savedName
                binding.txtProfileEmail.text = savedEmail
            }
        }
    }
}