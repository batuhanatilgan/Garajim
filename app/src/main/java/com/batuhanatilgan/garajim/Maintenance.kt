package com.batuhanatilgan.garajim

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "maintenances")
data class Maintenance(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val carId: Int, // Bu bakım hangi araca ait?
    val tarih: String,
    val kilometre: String,
    val yapilanIslemler: String,
    val notlar: String,
    val maliyet: String
)