package com.batuhanatilgan.garajim

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cars")
data class Car(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val markaModel: String,
    val modelYili: String,
    var kilometre: String,
    val yakitTuru: String = "Benzin",
    val sonBakimKm: String = "0",
    val muayeneTarihi: String? = null,
    val sigortaTarihi: String? = null,
    val aracTipi: String = "Binek"
)