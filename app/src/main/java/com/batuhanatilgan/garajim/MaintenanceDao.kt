package com.batuhanatilgan.garajim

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface MaintenanceDao {

    // Yeni bir bakım kaydı ekle
    @Insert
    suspend fun insertMaintenance(maintenance: Maintenance)
    @Query("SELECT * FROM maintenances WHERE carId = :aracId ORDER BY id DESC")
    suspend fun getMaintenancesByCarId(aracId: Int): List<Maintenance>
    @Query("DELETE FROM maintenances WHERE id = :bakimId")
    suspend fun deleteMaintenance(bakimId: Int)
}