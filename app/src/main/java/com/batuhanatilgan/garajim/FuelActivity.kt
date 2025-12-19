package com.batuhanatilgan.garajim

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.batuhanatilgan.garajim.databinding.ActivityFuelBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar

class FuelActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFuelBinding
    private lateinit var adapter: FuelAdapter
    private var currentCarId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFuelBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Hangi araç
        currentCarId = intent.getIntExtra("ARAC_ID", 0)
        if (currentCarId == 0) {
            finish()
            return
        }

        setupRecyclerView()
        loadData()
        binding.btnBack.setOnClickListener { finish() }
        binding.fabAddFuel.setOnClickListener {
            showAddFuelDialog()
        }
    }

    private fun setupRecyclerView() {
        adapter = FuelAdapter(emptyList())
        binding.rvFuelList.layoutManager = LinearLayoutManager(this)
        binding.rvFuelList.adapter = adapter
    }

    private fun loadData() {
        lifecycleScope.launch(Dispatchers.IO) {
            val db = AppDatabase.getDatabase(this@FuelActivity)
            val dao = db.fuelDao()

            // 1. Listeyi çek
            val fuelList = dao.getFuelsByCarId(currentCarId)

            // 2. Toplam harcamayı hesapla
            val totalCost = dao.getTotalCost(currentCarId) ?: 0.0

            withContext(Dispatchers.Main) {
                adapter.updateList(fuelList)

                // Toplam tutarı yazdır
                binding.txtTotalSpent.text = "%.2f TL".format(totalCost)
            }
        }
    }

    private fun showAddFuelDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_fuel, null)

        val etDate = dialogView.findViewById<EditText>(R.id.etFuelDate)
        val etLiter = dialogView.findViewById<EditText>(R.id.etLiter)
        val etTotalCost = dialogView.findViewById<EditText>(R.id.etTotalCost)
        val etCurrentKm = dialogView.findViewById<EditText>(R.id.etCurrentKm)

        // Tarih kutusuna tıklayınca takvim açılsın
        etDate.setOnClickListener {
            val takvim = Calendar.getInstance()
            DatePickerDialog(this, { _, year, month, day ->
                val secilenTarih = String.format("%02d.%02d.%04d", day, month + 1, year)
                etDate.setText(secilenTarih)
            }, takvim.get(Calendar.YEAR), takvim.get(Calendar.MONTH), takvim.get(Calendar.DAY_OF_MONTH)).show()
        }

        AlertDialog.Builder(this)
            .setView(dialogView)
            .setPositiveButton("KAYDET") { _, _ ->
                val tarih = etDate.text.toString()
                val litreStr = etLiter.text.toString()
                val tutarStr = etTotalCost.text.toString()
                val km = etCurrentKm.text.toString()

                if (tarih.isNotEmpty() && litreStr.isNotEmpty() && tutarStr.isNotEmpty()) {
                    saveFuel(tarih, litreStr, tutarStr, km)
                } else {
                    Toast.makeText(this, "Eksik bilgi girdiniz", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("İPTAL", null)
            .show()
    }

    private fun saveFuel(tarih: String, litreStr: String, tutarStr: String, km: String) {
        // Matematiksel Hesaplamalar
        val litre = litreStr.toDoubleOrNull() ?: 0.0
        val tutar = tutarStr.toDoubleOrNull() ?: 0.0

        // Birim fiyatı otomatik bul: Tutar / Litre
        val birimFiyat = if (litre > 0) tutar / litre else 0.0
        val birimFiyatStr = "%.2f".format(birimFiyat)

        lifecycleScope.launch(Dispatchers.IO) {
            val db = AppDatabase.getDatabase(this@FuelActivity)
            val newFuel = Fuel(
                carId = currentCarId,
                tarih = tarih,
                alinanLitre = litreStr,
                toplamTutar = tutarStr,
                litreFiyati = birimFiyatStr,
                oAnkiKm = km
            )

            db.fuelDao().insertFuel(newFuel)
            loadData() // Ekranı yenile

            withContext(Dispatchers.Main) {
                Toast.makeText(this@FuelActivity, "Yakıt Eklendi!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}