package com.batuhanatilgan.garajim

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.batuhanatilgan.garajim.databinding.ActivityLegalBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit

class LegalActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLegalBinding
    private var currentCar: Car? = null
    private var currentCarId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLegalBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnBack.setOnClickListener {
            finish()
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Yasal Takip"

        currentCarId = intent.getIntExtra("ARAC_ID", 0)

        if (currentCarId == 0) {
            finish()
            return
        }

        loadCarData()

        binding.btnSelectMuayene.setOnClickListener {
            // Kullanıcıya "En son ne zaman muayene yaptırdın?" diye soruyoruz
            Toast.makeText(this, "Lütfen SON MUAYENE tarihini seçin", Toast.LENGTH_LONG).show()
            tarihSecVeHesapla(isMuayene = true)
        }

        binding.btnSelectSigorta.setOnClickListener {
            tarihSecVeHesapla(isMuayene = false)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    private fun loadCarData() {
        lifecycleScope.launch(Dispatchers.IO) {
            val db = AppDatabase.getDatabase(this@LegalActivity)
            val allCars = db.carDao().getAllCars()
            currentCar = allCars.find { it.id == currentCarId }

            withContext(Dispatchers.Main) {
                currentCar?.let { arac ->
                    if (!arac.muayeneTarihi.isNullOrEmpty()) {
                        binding.txtMuayeneDate.text = "Bitiş: ${arac.muayeneTarihi}"
                        kalanGunHesapla(arac.muayeneTarihi, binding.txtMuayeneCountDown)
                    }
                    if (!arac.sigortaTarihi.isNullOrEmpty()) {
                        binding.txtSigortaDate.text = "Bitiş: ${arac.sigortaTarihi}"
                        kalanGunHesapla(arac.sigortaTarihi, binding.txtSigortaCountDown)
                    }
                }
            }
        }
    }

    private fun tarihSecVeHesapla(isMuayene: Boolean) {
        val calendar = Calendar.getInstance()

        val datePicker = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->

                // Kullanıcının seçtiği "Yapılış Tarihi"
                val secilenTakvim = Calendar.getInstance()
                secilenTakvim.set(year, month, dayOfMonth)

                // --- AKILLI HESAPLAMA ---
                if (isMuayene) {
                    // Araç tipine göre yıl ekle
                    val aracTipi = currentCar?.aracTipi ?: "Binek"
                    if (aracTipi == "Ticari") {
                        secilenTakvim.add(Calendar.YEAR, 1) // Ticari ise 1 yıl ekle
                    } else {
                        secilenTakvim.add(Calendar.YEAR, 2) // Binek ise 2 yıl ekle
                    }
                } else {
                    // Sigorta her zaman 1 yıldır
                    secilenTakvim.add(Calendar.YEAR, 1)
                }

                // Yeni Bitiş Tarihini Formatla
                val yeniBitisTarihi = String.format("%02d.%02d.%04d",
                    secilenTakvim.get(Calendar.DAY_OF_MONTH),
                    secilenTakvim.get(Calendar.MONTH) + 1,
                    secilenTakvim.get(Calendar.YEAR)
                )

                // Kaydet
                updateDatabase(yeniBitisTarihi, isMuayene)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePicker.setTitle("Son Yapılan Tarihi Seçin")
        datePicker.show()
    }

    private fun updateDatabase(tarih: String, isMuayene: Boolean) {
        currentCar?.let { eskiArac ->
            val yeniArac = if (isMuayene) {
                eskiArac.copy(muayeneTarihi = tarih)
            } else {
                eskiArac.copy(sigortaTarihi = tarih)
            }

            lifecycleScope.launch(Dispatchers.IO) {
                val db = AppDatabase.getDatabase(this@LegalActivity)
                db.carDao().updateCar(yeniArac)
                withContext(Dispatchers.Main) {
                    loadCarData()
                    Toast.makeText(this@LegalActivity, "Yeni Bitiş Tarihi Hesaplandı!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun kalanGunHesapla(tarihStr: String, textView: android.widget.TextView) {
        try {
            val format = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
            val hedefTarih = format.parse(tarihStr)
            val bugun = java.util.Date()

            if (hedefTarih != null) {
                val farkMilisaniye = hedefTarih.time - bugun.time
                val kalanGun = TimeUnit.DAYS.convert(farkMilisaniye, TimeUnit.MILLISECONDS)

                if (kalanGun < 0) {
                    textView.text = "SÜRESİ GEÇTİ! (${kotlin.math.abs(kalanGun)} gün)"
                    textView.setTextColor(android.graphics.Color.RED)
                } else {
                    textView.text = "Kalan Süre: $kalanGun Gün"
                    textView.setTextColor(android.graphics.Color.parseColor("#E65100"))
                }
            }
        } catch (e: Exception) {
            textView.text = ""
        }
    }
}