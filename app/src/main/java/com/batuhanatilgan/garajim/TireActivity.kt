package com.batuhanatilgan.garajim

import android.app.DatePickerDialog
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.batuhanatilgan.garajim.databinding.ActivityTireBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar

class TireActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTireBinding
    private lateinit var adapter: TireAdapter
    private var currentCarId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTireBinding.inflate(layoutInflater)
        setContentView(binding.root)

        currentCarId = intent.getIntExtra("ARAC_ID", 0)
        if (currentCarId == 0) { finish(); return }

        binding.btnBack.setOnClickListener { finish() }

        setupRecyclerView()

        loadData()

        binding.fabAddTire.setOnClickListener {
            showAddDialog()
        }
    }

    private fun setupRecyclerView() {
        adapter = TireAdapter(emptyList())
        binding.rvTireList.layoutManager = LinearLayoutManager(this)
        binding.rvTireList.adapter = adapter
    }

    private fun loadData() {
        lifecycleScope.launch(Dispatchers.IO) {
            val db = AppDatabase.getDatabase(this@TireActivity)
            val dao = db.tireDao()

            // Geçmişi çek
            val history = dao.getTireChangesByCarId(currentCarId)
            // Şu an üzerinde ne var?
            val lastChange = dao.getLastTireChange(currentCarId)
            val currentType = lastChange?.takilanTur ?: "Bilinmiyor"

            withContext(Dispatchers.Main) {
                adapter.updateList(history)
                binding.txtCurrentTire.text = if(currentType == "Bilinmiyor") "Lastik Bilgisi Yok" else "$currentType Lastik"
                // MEVSİM KONTROLÜ YAP
                checkSeasonAndWarn(currentType)
            }
        }
    }

    private fun checkSeasonAndWarn(currentType: String) {
        val calendar = Calendar.getInstance()
        val month = calendar.get(Calendar.MONTH) // 0=Ocak, 11=Aralık
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        // KIŞ DÖNEMİ: 15 Kasım (10. ay) - 15 Nisan (3. ay) arası
        // Kasım(10) > 15, Aralık(11), Ocak(0), Şubat(1), Mart(2), Nisan(3) < 15

        var isWinterSeason = false

        if (month == 10 && day >= 15) isWinterSeason = true // 15 Kasım sonrası
        else if (month == 11 || month == 0 || month == 1 || month == 2) isWinterSeason = true
        else if (month == 3 && day <= 15) isWinterSeason = true // 15 Nisan öncesi

        if (currentType == "Bilinmiyor") {
            setCardStatus(Color.GRAY, "Lütfen üzerindeki lastiği kaydedin.")
            return
        }

        if (isWinterSeason) {
            // Kış sezonundayız, lastik Kışlık olmalı
            if (currentType == "Kışlık") {
                setCardStatus(Color.parseColor("#4CAF50"), "Mevsime Uygun (Kış Dönemi) ✅") // Yeşil
            } else {
                setCardStatus(Color.parseColor("#D32F2F"), "DİKKAT! Kış Lastiği Zorunlu! ❄️") // Kırmızı
            }
        } else {
            // Yaz sezonundayız
            if (currentType == "Yazlık") {
                setCardStatus(Color.parseColor("#4CAF50"), "Mevsime Uygun (Yaz Dönemi) ✅") // Yeşil
            } else {
                setCardStatus(Color.parseColor("#FF9800"), "Yaz lastiğine geçmeniz önerilir. ☀️") // Turuncu (Zorunlu değil ama öneri)
            }
        }
    }

    private fun setCardStatus(color: Int, message: String) {
        binding.cardStatus.setCardBackgroundColor(color)
        binding.txtWarning.text = message
    }

    private fun showAddDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_tire, null)
        val etDate = dialogView.findViewById<EditText>(R.id.etTireDate)
        val rbWinter = dialogView.findViewById<RadioButton>(R.id.rbWinter)
        val etNotes = dialogView.findViewById<EditText>(R.id.etTireNotes)
        val etCost = dialogView.findViewById<EditText>(R.id.etTireCost)

        etDate.setOnClickListener {
            val c = Calendar.getInstance()
            DatePickerDialog(this, { _, y, m, d ->
                etDate.setText(String.format("%02d.%02d.%04d", d, m + 1, y))
            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show()
        }

        AlertDialog.Builder(this)
            .setView(dialogView)
            .setPositiveButton("KAYDET") { _, _ ->
                val date = etDate.text.toString()
                val type = if (rbWinter.isChecked) "Kışlık" else "Yazlık"
                val notes = etNotes.text.toString()
                val cost = etCost.text.toString()

                if (date.isNotEmpty()) {
                    saveTireChange(date, type, notes, cost)
                }
            }
            .setNegativeButton("İPTAL", null)
            .show()
    }

    private fun saveTireChange(date: String, type: String, notes: String, cost: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            val db = AppDatabase.getDatabase(this@TireActivity)
            val newItem = TireChange(
                carId = currentCarId,
                islemTarihi = date,
                takilanTur = type,
                notlar = notes,
                maliyet = cost
            )
            db.tireDao().insertTireChange(newItem)

            // Ekranı güncelle
            loadData()

            withContext(Dispatchers.Main) {
                Toast.makeText(this@TireActivity, "Kaydedildi", Toast.LENGTH_SHORT).show()
            }
        }
    }
}