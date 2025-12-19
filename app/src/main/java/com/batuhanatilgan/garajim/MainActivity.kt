package com.batuhanatilgan.garajim

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.batuhanatilgan.garajim.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit
import android.text.InputType
import android.widget.FrameLayout
import androidx.appcompat.app.AlertDialog
import com.google.android.material.textfield.TextInputEditText

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    // Araç listesini tüm sınıfta kullanabilmek için burada tanımlıyoruz
    private var globalAracListesi: List<Car> = emptyList()
    private var secilenAracId: Int = 0 // O an ekranda hangi araç varsa onun ID'sini tutacak

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 101)
            }
        }
        setupButtons()
        periyodikKontroluBaslat()

    }

    override fun onResume() {
        super.onResume()
        verileriGetirVeListele()
    }

    private fun verileriGetirVeListele() {
        lifecycleScope.launch(Dispatchers.IO) {
            val db = AppDatabase.getDatabase(this@MainActivity)
            val carDao = db.carDao()

            // Tüm araçları çekip değişkene atıyoruz
            globalAracListesi = carDao.getAllCars()

            withContext(Dispatchers.Main) {
                if (globalAracListesi.isEmpty()) {
                    // Hiç araç yoksa ekleme ekranına git
                    val intent = Intent(this@MainActivity, AddCarActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    // Araçlar varsa Spinner'ı (Açılır Listeyi) kur
                    spinnerKurulumu()
                }
            }
        }
    }

    private fun spinnerKurulumu() {
        val aracIsimleri = globalAracListesi.map { it.markaModel }

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            aracIsimleri
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerCars.adapter = adapter

        binding.spinnerCars.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val secilenArac = globalAracListesi[position]
                ekraniGuncelle(secilenArac)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun ekraniGuncelle(arac: Car) {
        secilenAracId = arac.id

        binding.txtCarName.text = "${arac.markaModel} (${arac.modelYili})"
        binding.txtKm.text = "${arac.kilometre} KM"

        val kmSayisi = arac.kilometre.toIntOrNull() ?: 0
        // YENİ AKILLI KOD (Bir sonraki 10.000'e yuvarlar)
        val sonrakiBakim = ((kmSayisi / 10000) + 1) * 10000

        binding.txtMaintenanceInfo.text = "Sonraki Bakım: $sonrakiBakim KM"
    }

    private fun setupButtons() {
        // --- YENİ EKLENEN: Mavi Karta Tıklayınca KM Güncelle ---
        binding.cardCarInfo.setOnClickListener {
            if (secilenAracId != 0) {
                showUpdateKmDialog() // Birazdan ekleyeceğimiz fonksiyonu çağırıyor
            } else {
                Toast.makeText(this, "Araç seçili değil", Toast.LENGTH_SHORT).show()
            }
        }
        // -------------------------------------------------------

        //Yeni araç ekle (+) butonu
        binding.btnAddNewCar.setOnClickListener {
            val intent = Intent(this, AddCarActivity::class.java)
            startActivity(intent)
        }

        //Bakım Butonu
        binding.btnMaintenance.setOnClickListener {
            if (secilenAracId != 0) {
                val intent = Intent(this, MaintenanceActivity::class.java)
                intent.putExtra("ARAC_ID", secilenAracId)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Lütfen önce bir araç seçin", Toast.LENGTH_SHORT).show()
            }
        }
        binding.btnFuel.setOnClickListener {
            if (secilenAracId != 0) {
                val intent = Intent(this, FuelActivity::class.java)
                intent.putExtra("ARAC_ID", secilenAracId)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Lütfen önce bir araç seçin", Toast.LENGTH_SHORT).show()
            }
        }

        //Yasal İşlemler Butonu
        binding.btnLegal.setOnClickListener {
            if (secilenAracId != 0) {
                val intent = Intent(this, LegalActivity::class.java)
                intent.putExtra("ARAC_ID", secilenAracId)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Lütfen önce bir araç seçin", Toast.LENGTH_SHORT).show()
            }
        }
        binding.btnTire.setOnClickListener {
            if (secilenAracId != 0) {
                val intent = Intent(this, TireActivity::class.java)
                intent.putExtra("ARAC_ID", secilenAracId)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Lütfen önce bir araç seçin", Toast.LENGTH_SHORT).show()
            }
        }
        binding.btnPdfReport.setOnClickListener {
            if (secilenAracId != 0) {
                Toast.makeText(this, "Rapor hazırlanıyor...", Toast.LENGTH_SHORT).show()
                lifecycleScope.launch {
                    val manager = PdfManager(this@MainActivity)
                    manager.raporOlustur(secilenAracId)
                }
            } else {
                Toast.makeText(this, "Lütfen bir araç seçin", Toast.LENGTH_SHORT).show()
            }
        }
        binding.btnProfile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
    }
    private fun periyodikKontroluBaslat() {
        // Arka planda çalışacak işin ayarları (15 dakikada bir kontrol et)
        val workRequest = androidx.work.PeriodicWorkRequestBuilder<NotificationWorker>(
            15, TimeUnit.MINUTES
        ).build()

        // İşi sisteme kuyruğa ekle (KEEP diyerek, zaten varsa yenisini başlatma diyoruz)
        androidx.work.WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "BakimKontrolIsi",
            androidx.work.ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }

    //Km Güncelleme
    private fun showUpdateKmDialog() {
        val secilenArac = globalAracListesi.find { it.id == secilenAracId } ?: return

        val builder = AlertDialog.Builder(this)
        builder.setTitle("KM Güncelle")
        builder.setMessage("${secilenArac.markaModel} için yeni KM giriniz:")

        val input = TextInputEditText(this)
        input.inputType = InputType.TYPE_CLASS_NUMBER
        input.setText(secilenArac.kilometre) // Mevcut KM'yi kutuya yaz

        val container = FrameLayout(this)
        val params = FrameLayout.LayoutParams(
            android.view.ViewGroup.LayoutParams.MATCH_PARENT,
            android.view.ViewGroup.LayoutParams.WRAP_CONTENT
        )
        params.leftMargin = 60
        params.rightMargin = 60
        input.layoutParams = params
        container.addView(input)
        builder.setView(container)

        builder.setPositiveButton("GÜNCELLE") { _, _ ->
            val yeniKm = input.text.toString().trim()
            if (yeniKm.isNotEmpty()) {
                lifecycleScope.launch(Dispatchers.IO) {
                    val db = AppDatabase.getDatabase(this@MainActivity)

                    // Veriyi güncelle
                    secilenArac.kilometre = yeniKm
                    db.carDao().updateCar(secilenArac)

                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@MainActivity, "KM Güncellendi!", Toast.LENGTH_SHORT).show()
                        verileriGetirVeListele() // Ekranı yenile
                    }
                }
            }
        }
        builder.setNegativeButton("İptal", null)
        builder.show()
    }
}