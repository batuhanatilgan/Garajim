package com.batuhanatilgan.garajim

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface TireDao {
    @Insert
    suspend fun insertTireChange(tireChange: TireChange)
    // Sadece bu araca ait değişimleri getir (Yeniden eskiye)
    @Query("SELECT * FROM tire_changes WHERE carId = :aracId ORDER BY id DESC")
    suspend fun getTireChangesByCarId(aracId: Int): List<TireChange>

    // En son yapılan değişimi getir
    @Query("SELECT * FROM tire_changes WHERE carId = :aracId ORDER BY id DESC LIMIT 1")
    suspend fun getLastTireChange(aracId: Int): TireChange?
}