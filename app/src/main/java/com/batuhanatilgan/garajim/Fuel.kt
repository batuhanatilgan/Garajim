package com.batuhanatilgan.garajim

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "fuels")
data class Fuel(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val carId: Int,
    val alinanLitre: String,
    val litreFiyati: String,
    val toplamTutar: String,
    val oAnkiKm: String,
    val tarih: String
)