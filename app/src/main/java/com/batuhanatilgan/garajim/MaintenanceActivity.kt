package com.batuhanatilgan.garajim

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.batuhanatilgan.garajim.databinding.ActivityMaintenanceBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MaintenanceActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMaintenanceBinding
    private lateinit var adapter: MaintenanceAdapter
    private var currentCarId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMaintenanceBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnBack.setOnClickListener {
            finish()
        }
        currentCarId = intent.getIntExtra("ARAC_ID", 0)

        if (currentCarId == 0) {
            Toast.makeText(this, "Hata: Araç bilgisi alınamadı!", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        setupRecyclerView()
        loadData()
        binding.fabAddMaintenance.setOnClickListener {
            showAddDialog()
        }
    }

    private fun setupRecyclerView() {
        adapter = MaintenanceAdapter(emptyList())
        binding.rvMaintenanceList.layoutManager = LinearLayoutManager(this)
        binding.rvMaintenanceList.adapter = adapter
    }

    private fun loadData() {
        lifecycleScope.launch(Dispatchers.IO) {
            val db = AppDatabase.getDatabase(this@MaintenanceActivity)
            val list = db.maintenanceDao().getMaintenancesByCarId(currentCarId)

            withContext(Dispatchers.Main) {
                adapter.updateList(list)
            }
        }
    }

    private fun showAddDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_maintenance, null)

        val etDate = dialogView.findViewById<EditText>(R.id.etDate)
        val etKm = dialogView.findViewById<EditText>(R.id.etKm)
        val etOperations = dialogView.findViewById<EditText>(R.id.etOperations)
        val etNotes = dialogView.findViewById<EditText>(R.id.etNotes)
        val etCost = dialogView.findViewById<EditText>(R.id.etCost)

        AlertDialog.Builder(this)
            .setView(dialogView)
            .setPositiveButton("KAYDET") { _, _ ->
                val tarih = etDate.text.toString()
                val km = etKm.text.toString()
                val islemler = etOperations.text.toString()
                val notlar = etNotes.text.toString()
                val tutar = etCost.text.toString()

                if (tarih.isNotEmpty() && km.isNotEmpty() && islemler.isNotEmpty()) {
                    saveMaintenance(tarih, km, islemler, notlar, tutar)
                } else {
                    Toast.makeText(this, "Lütfen önemli alanları doldurun", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("İPTAL", null)
            .show()
    }

    private fun saveMaintenance(date: String, km: String, ops: String, notes: String, cost: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            val db = AppDatabase.getDatabase(this@MaintenanceActivity)

            val newMaintenance = Maintenance(
                carId = currentCarId,
                tarih = date,
                kilometre = km,
                yapilanIslemler = ops,
                notlar = notes,
                maliyet = cost
            )

            db.maintenanceDao().insertMaintenance(newMaintenance)
            loadData()

            withContext(Dispatchers.Main) {
                Toast.makeText(this@MaintenanceActivity, "Bakım eklendi!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}