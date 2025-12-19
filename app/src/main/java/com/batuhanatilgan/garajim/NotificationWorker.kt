package com.batuhanatilgan.garajim

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import java.util.Calendar

class NotificationWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val db = AppDatabase.getDatabase(applicationContext)
        val aracListesi = db.carDao().getAllCars()

        for (arac in aracListesi) {
            // 1. KONTROL: BAKIM (KM)
            val guncelKm = arac.kilometre.toIntOrNull() ?: 0
            val sonBakimKm = arac.sonBakimKm.toIntOrNull() ?: 0
            val fark = guncelKm - sonBakimKm

            if (fark >= 9000) {
                bildirimGonder(
                    arac.id * 10,
                    "Bakım Zamanı!",
                    "${arac.markaModel} aracınızın bakımı geldi. (Fark: $fark KM)"
                )
            }
            // 2. KONTROL: LASTİK (MEVSİM)
            kontrolLastikMevsimi(db, arac)
        }

        return Result.success()
    }

    private suspend fun kontrolLastikMevsimi(db: AppDatabase, arac: Car) {
        val lastChange = db.tireDao().getLastTireChange(arac.id)
        val takilanTur = lastChange?.takilanTur ?: "Bilinmiyor"

        val calendar = Calendar.getInstance()
        val month = calendar.get(Calendar.MONTH) // 0..11
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        // Kış Sezonu Kontrolü (15 Kasım - 15 Nisan)
        var isWinterSeason = false
        if (month == 10 && day >= 15) isWinterSeason = true // Kasım 15+
        else if (month == 11 || month == 0 || month == 1 || month == 2) isWinterSeason = true // Aralık-Mart
        else if (month == 3 && day <= 15) isWinterSeason = true // Nisan 15-

        // Eğer Kış sezonundaysak VE üzerindeki lastik "Kışlık" değilse -> UYARI VER
        if (isWinterSeason && takilanTur != "Kışlık") {
            bildirimGonder(
                arac.id * 10 + 1, // Farklı ID
                "Kış Lastiği Zorunlu! ❄️",
                "${arac.markaModel} aracınızda hala $takilanTur lastik var. Cezai işlem yememek için değiştirin!"
            )
        }
    }

    private fun bildirimGonder(notificationId: Int, baslik: String, icerik: String) {
        val context = applicationContext
        val channelId = "garajim_bildirim_kanali"
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Garajım Hatırlatıcı",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle(baslik)
            .setContentText(icerik)
            .setStyle(NotificationCompat.BigTextStyle().bigText(icerik))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(notificationId, notification)
    }
}