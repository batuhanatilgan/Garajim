package com.batuhanatilgan.garajim

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.batuhanatilgan.garajim.databinding.ActivityAddCarBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddCarActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddCarBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddCarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Geri butonu
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Yeni Araç Ekle"

        val db = AppDatabase.getDatabase(this)
        val carDao = db.carDao()

        binding.btnSaveCar.setOnClickListener {
            val marka = binding.etCarName.text.toString()
            val yil = binding.etCarYear.text.toString()
            val km = binding.etCarKm.text.toString()

            // Seçilen araç tipini al
            val aracTipi = if (binding.rbTicari.isChecked) "Ticari" else "Binek"

            if (marka.isNotEmpty() && yil.isNotEmpty() && km.isNotEmpty()) {
                lifecycleScope.launch(Dispatchers.IO) {
                    val yeniArac = Car(
                        markaModel = marka,
                        modelYili = yil,
                        kilometre = km,
                        aracTipi = aracTipi
                    )

                    carDao.insertCar(yeniArac)

                    withContext(Dispatchers.Main) {
                        Toast.makeText(applicationContext, "Araç Kaydedildi!", Toast.LENGTH_LONG).show()
                        val intent = Intent(this@AddCarActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                }
            } else {
                Toast.makeText(this, "Lütfen tüm alanları doldurun", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}