package com.batuhanatilgan.garajim

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface FuelDao {
    @Insert
    suspend fun insertFuel(fuel: Fuel)

    // Yakıtları KM'ye göre tersten sırala (En son alınan en üstte)
    @Query("SELECT * FROM fuels WHERE carId = :aracId ORDER BY id DESC")
    suspend fun getFuelsByCarId(aracId: Int): List<Fuel>

    // Toplam harcanan parayı bulmak için
    @Query("SELECT SUM(toplamTutar) FROM fuels WHERE carId = :aracId")
    suspend fun getTotalCost(aracId: Int): Double?
}