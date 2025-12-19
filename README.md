# Garajım - Kişisel Araç Asistanı 🚗

![Language](https://img.shields.io/badge/Language-Kotlin-purple) ![Platform](https://img.shields.io/badge/Platform-Android-green) ![UI](https://img.shields.io/badge/Design-Material%20Design%203-blue)

**Garajım**, araç sahiplerinin periyodik bakımlarını, yakıt harcamalarını, lastik değişimlerini ve yasal süreçlerini (Sigorta/Muayene) tek bir yerden takip etmelerini sağlayan modern bir Android uygulamasıdır.

Kullanıcı dostu arayüzü ve çevrimdışı (offline) çalışma yeteneği ile internet bağlantısına ihtiyaç duymadan verilerinizi güvenle saklar.

---

## ✨ Özellikler

* **🛠 Periyodik Bakım Takibi:** Yapılan işlemleri, tarihi, kilometreyi ve maliyeti kaydedin. Yaklaşan bakımlar için otomatik hesaplama sistemi.
* **⛽ Yakıt Yönetimi:** Alınan yakıtı litre ve tutar bazında kaydedin, toplam harcamalarınızı anlık görün.
* **📅 Yasal Hatırlatıcılar:** Sigorta ve Muayene tarihleri için geri sayım sayacı. Zamanı geldiğinde sürpriz yaşamayın.
* **❄️ Lastik Yönetimi:** Yazlık ve kışlık lastik değişimlerini takip edin, mevsimsel uyarılar alın.
* **📊 PDF Raporlama:** Araç geçmişinizi tek tıkla profesyonel bir PDF raporu olarak dışa aktarın.
* **🎨 Modern Arayüz:** Material Design 3 standartlarında, kullanıcı deneyimi (UX) odaklı, Mavi-Turuncu tema tasarımı.
* **💾 Çevrimdışı Veritabanı:** Room Database mimarisi ile verileriniz cihazınızda yerel olarak saklanır.

---

## 🛠 Kullanılan Teknolojiler ve Mimari

Bu proje, modern Android geliştirme standartlarına uygun olarak **Kotlin** dili ile geliştirilmiştir.

* **Dil:** Kotlin
* **Tasarım (UI):** XML, Material Components, ConstraintLayout, CardView
* **Veritabanı:** Room Database (SQLite)
* **Asenkron İşlemler:** Kotlin Coroutines & Lifecycle Scope
* **Mimari Desen:** MVVM (Model-View-ViewModel) prensiplerine uygun yapı
* **Diğer Kütüphaneler:** iText (PDF oluşturma için)

---

## 🚀 Kurulum

Projeyi yerel makinenizde çalıştırmak için:

1.  Bu depoyu klonlayın:
    ```bash
    git clone [https://github.com/batuhanatilgan/Garajim.git](https://github.com/batuhanatilgan/Garajim.git)
    ```
2.  **Android Studio**'yu açın ve `Open Project` diyerek klasörü seçin.
3.  Gradle senkronizasyonunun bitmesini bekleyin.
4.  Emülatör veya fiziksel cihazınızı bağlayıp `Run` (▶️) tuşuna basın.

---

## 👨‍💻 Geliştirici

**Batuhan Atılgan**

* **GitHub:** [github.com/batuhanatilgan](https://github.com/batuhanatilgan)

---

### ⚠️ Not
Bu proje üniversite bitirme/dönem projesi kapsamında geliştirilmiştir ve açık kaynak olarak geliştirilmeye devam etmektedir.
