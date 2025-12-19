package com.batuhanatilgan.garajim

import android.content.ContentValues
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

class PdfManager(private val context: Context) {

    suspend fun raporOlustur(aracId: Int) {
        withContext(Dispatchers.IO) {
            val db = AppDatabase.getDatabase(context)

            //Verileri Çek
            val arac = db.carDao().getAllCars().find { it.id == aracId } ?: return@withContext
            val bakimlar = db.maintenanceDao().getMaintenancesByCarId(aracId)
            val yakitlar = db.fuelDao().getFuelsByCarId(aracId)
            val lastikler = db.tireDao().getTireChangesByCarId(aracId)

            //PDF Sayfası Başlat (A4 Boyutu)
            val pdfDocument = PdfDocument()
            val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
            val page = pdfDocument.startPage(pageInfo)
            val canvas: Canvas = page.canvas
            val paint = Paint()

            //Başlık ve Araç Bilgileri Yaz
            paint.textSize = 20f
            paint.isFakeBoldText = true
            paint.color = Color.BLACK
            canvas.drawText("ARAÇ GEÇMİŞ RAPORU", 180f, 50f, paint)

            paint.textSize = 14f
            paint.isFakeBoldText = false
            canvas.drawText("Araç: ${arac.markaModel} (${arac.modelYili})", 50f, 90f, paint)
            canvas.drawText("Güncel KM: ${arac.kilometre}", 50f, 110f, paint)
            canvas.drawText("Rapor Tarihi: ${java.text.SimpleDateFormat("dd.MM.yyyy").format(java.util.Date())}", 50f, 130f, paint)

            paint.strokeWidth = 2f
            canvas.drawLine(50f, 140f, 545f, 140f, paint)
            var yPos = 170f
            paint.textSize = 16f
            paint.isFakeBoldText = true
            paint.color = Color.parseColor("#2196F3")
            canvas.drawText("SON BAKIMLAR", 50f, yPos, paint)
            yPos += 25f

            paint.textSize = 12f
            paint.color = Color.BLACK
            paint.isFakeBoldText = false

            if (bakimlar.isEmpty()) {
                canvas.drawText("- Kayıt yok -", 50f, yPos, paint)
                yPos += 20f
            } else {
                for (item in bakimlar.take(5)) {
                    canvas.drawText("${item.tarih} - ${item.kilometre} KM - ${item.yapilanIslemler} (${item.maliyet} TL)", 50f, yPos, paint)
                    yPos += 20f
                }
            }
            yPos += 10f
            paint.textSize = 16f
            paint.isFakeBoldText = true
            paint.color = Color.parseColor("#E91E63")
            canvas.drawText("SON YAKITLAR", 50f, yPos, paint)
            yPos += 25f

            paint.textSize = 12f
            paint.color = Color.BLACK
            paint.isFakeBoldText = false

            if (yakitlar.isEmpty()) {
                canvas.drawText("- Kayıt yok -", 50f, yPos, paint)
                yPos += 20f
            } else {
                for (item in yakitlar.take(5)) {
                    canvas.drawText("${item.tarih} - ${item.alinanLitre} Lt - ${item.toplamTutar} TL", 50f, yPos, paint)
                    yPos += 20f
                }
            }
            yPos += 10f
            paint.textSize = 16f
            paint.isFakeBoldText = true
            paint.color = Color.parseColor("#4CAF50") // Yeşil
            canvas.drawText("LASTİK GEÇMİŞİ", 50f, yPos, paint)
            yPos += 25f

            paint.textSize = 12f
            paint.color = Color.BLACK
            paint.isFakeBoldText = false

            if (lastikler.isEmpty()) {
                canvas.drawText("- Kayıt yok -", 50f, yPos, paint)
                yPos += 20f
            } else {
                for (item in lastikler.take(5)) {
                    canvas.drawText("${item.islemTarihi} - ${item.takilanTur} Lastik Takıldı (${item.notlar})", 50f, yPos, paint)
                    yPos += 20f
                }
            }
            pdfDocument.finishPage(page)
            dosyayiKaydet(pdfDocument, "Garajim_Rapor_${arac.markaModel}.pdf")
        }
    }

    private suspend fun dosyayiKaydet(document: PdfDocument, fileName: String) {
        withContext(Dispatchers.IO) {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    val resolver = context.contentResolver
                    val contentValues = ContentValues().apply {
                        put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                        put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                        put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                    }

                    val uri: Uri? = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
                    if (uri != null) {
                        val outputStream: OutputStream? = resolver.openOutputStream(uri)
                        document.writeTo(outputStream)
                        outputStream?.close()

                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "PDF İndirilenlere Kaydedildi! ✅", Toast.LENGTH_LONG).show()
                        }
                    }
                } else {
                    val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName)
                    document.writeTo(FileOutputStream(file))
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "PDF Kaydedildi: ${file.name}", Toast.LENGTH_LONG).show()
                    }
                }
                document.close()
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Hata: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}