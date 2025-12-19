package com.batuhanatilgan.garajim

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.batuhanatilgan.garajim.databinding.ActivityOnboardingBinding

class OnboardingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnboardingBinding
    private val sliderDataList = ArrayList<OnboardingItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        //Gösterilecek verileri hazırla
        loadData()

        //Adaptörü kur ve ViewPager'a bağla
        val adapter = OnboardingAdapter(sliderDataList)
        binding.viewPager.adapter = adapter

        //Sayfa değişimlerini dinle
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                updateUI(position)
            }
        })

        //Buton tıklama olayı
        binding.btnNext.setOnClickListener {
            val currentItem = binding.viewPager.currentItem
            // Eğer son sayfadaysak -> Uygulamaya başla
            if (currentItem == sliderDataList.size - 1) {
                navigateToApp()
            } else {
                // Değilsek -> Bir sonraki sayfaya geç
                binding.viewPager.setCurrentItem(currentItem + 1, true)
            }
        }
    }

    private fun loadData() {
        //Bakım
        sliderDataList.add(
            OnboardingItem(
                R.drawable.img_onboard_maint,
                "Profesyonel Bakım Takibi",
                "Aracınızın servis geçmişini kilometre ve tarih detaylarıyla kaydedin. Zamanı geldiğinde bildirim alın, sürprizlerle karşılaşmayın."
            )
        )
        //Yakıt
        sliderDataList.add(
            OnboardingItem(
                R.drawable.img_onboard_fuel,
                "Yakıt ve Masraf Kontrolü",
                "Aldığınız her yakıtı ve yaptığınız harcamaları fiş fotoğraflarıyla birlikte saklayın. Aracınızın gerçek maliyetini görün."
            )
        )
        //Rapor & Yasal
        sliderDataList.add(
            OnboardingItem(
                R.drawable.img_onboard_report,
                "Yasal Süreçler & Raporlama",
                "Muayene, sigorta ve lastik değişim tarihlerini unutmayın. Tek tuşla aracınızın tüm geçmişini PDF raporu olarak alın."
            )
        )
    }

    private fun updateUI(position: Int) {
        binding.txtIndicator.text = "${position + 1} / ${sliderDataList.size}"
        val maviRenk = android.graphics.Color.parseColor("#2196F3")
        val morRenk = android.graphics.Color.parseColor("#673AB7")

        if (position == sliderDataList.size - 1) {
            // Son sayfa: BAŞLA
            binding.btnNext.text = "HEMEN BAŞLA"
            binding.btnNext.setBackgroundColor(maviRenk)
        } else {
            // Ara sayfalar: İLERİ
            binding.btnNext.text = "İLERİ"
            binding.btnNext.setBackgroundColor(morRenk)
        }
    }

    private fun navigateToApp() {
        // Tanıtımı gördü olarak işaretle
        val sharedPref = getSharedPreferences("GarajimPrefs", android.content.Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putBoolean("isFirstTime", false)
        editor.apply()
        // Tanıtımdan sonra ARTIK GİRİŞ EKRANINA gitsin
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}