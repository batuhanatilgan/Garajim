package com.batuhanatilgan.garajim

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface CarDao {
    // Aracı veritabanına ekle
    @Insert
    suspend fun insertCar(car: Car)

    // Tüm araçları getir
    @Query("SELECT * FROM cars")
    suspend fun getAllCars(): List<Car>

    // Araç sayısını öğren
    @Query("SELECT COUNT(*) FROM cars")
    suspend fun getCarCount(): Int

    @androidx.room.Update
    suspend fun updateCar(car: Car)
}