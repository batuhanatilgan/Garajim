package com.batuhanatilgan.garajim

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tire_changes")
data class TireChange(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val carId: Int,
    val islemTarihi: String,
    val takilanTur: String,
    val notlar: String,
    val maliyet: String
)