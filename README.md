🌤️ **Hava Durumu & Yapay Zeka Asistanı**

Bu proje, modern Android geliştirme standartları (Jetpack Compose, MVVM) kullanılarak geliştirilmiş, anlık hava durumu verilerini yapay zeka destekli tavsiyelerle birleştiren kapsamlı bir Android uygulamasıdır. Kullanıcılar şehir bazlı hava durumunu detaylı olarak incelerken, o günün hava koşullarına uygun, esprili ve akıllı asistan tavsiyeleri alırlar.

📱 Ekran Görüntüleri

✨ Temel Özellikler

Şehir Arama ve Seçim: Kullanıcı dostu arama arayüzü ile farklı şehirlerin hava durumuna kolayca erişim.

<img width="220" height="480" alt="WhatsApp Image 2026-10-25 at 23 21 21 (2)" src="https://github.com/user-attachments/assets/dee63c87-3bc0-450b-b1ae-2b74d6c1ffe3" />
<img width="220" height="480" alt="WhatsApp Image 2026-10-25 at 23 21 21 (4)" src="https://github.com/user-attachments/assets/dc0bdb85-9df7-4a1e-9c4e-08f599d2da4d" />






Detaylı Tahminler: OpenWeatherMap API kullanılarak anlık hava durumu, 5 günlük özet ve saatlik detaylı tahminler.

<img width="220" height="480" alt="WhatsApp Image 2026-10-25 at 23 21 21" src="https://github.com/user-attachments/assets/a02e6d87-ec9a-4cf8-b995-b1edcf425320" />
<img width="220" height="480" alt="WhatsApp Image 2026-10-25 at 23 21 21 (1)" src="https://github.com/user-attachments/assets/db351082-e891-4537-89c8-7ae501fbde3a" />


Yapay Zeka (AI) Asistanı: Groq API (Llama-3 modeli) entegrasyonu ile o anki sıcaklık ve hava koşullarına özel 1-2 cümlelik dinamik ve eğlenceli tavsiyeler (Robot animasyonlu şık bir UI kartı ile).

Modern UI/UX: XML kullanılmadan %100 Jetpack Compose ile yazılmış arayüz. Dikey kaydırılabilir (scrollable) ekran, yatay kaydırılabilir (LazyRow) saatlik/günlük kartlar, Material ikonlar ve gradient (degrade) arka planlar.

Durum Yönetimi: Yükleniyor (Loading), Başarılı (Success) ve Hata (Error) durumlarını yöneten tam reaktif UI yapısı.

🏗️ Proje Mimarisi ve Detayları

* Projede MVVM (Model-View-ViewModel) mimarisi kullanılmış ve sorumluluklar (Separation of Concerns) kesin çizgilerle ayrılmıştır:

* UI Katmanı (View): WeatherDetailScreen ve alt bileşenleri (WeatherCardBig, WeatherCardSmall, HourlyWeatherSection). Arayüz sadece State dinler.

* ViewModel Katmanı: WeatherDetailViewModel, arayüzün ihtiyaç duyduğu tüm verileri (UiState ve AI mesajı) yönetir. Coroutines kullanarak arka planda ağ isteklerini yapar ve başarılı/hatalı durumları StateFlow ile UI'a aktarır.

* Network & API Katmanı: Ağ istekleri için Retrofit kullanılmıştır.

* WeatherApiService: OpenWeatherMap'ten saatlik ve günlük hava durumu verilerini çeker.

* GrokApiService: Groq API'sine asenkron istek atarak Llama-3 modelinden JSON formatında yanıt alır.

* Model Katmanı: API'den gelen karmaşık JSON verileri (örn. ForecastResponse), UI'ın kolayca kullanabileceği temiz veri sınıflarına (WeatherForecast, HourlyForecast, GrokRequest) dönüştürülür (Mapping).

🛠️ Kullanılan Teknolojiler

* Dil: Kotlin

* Arayüz: Jetpack Compose, Material Design 3

* Mimari: MVVM, UiState Pattern

* Ağ İşlemleri: Retrofit, OkHttp, Gson

* Asenkron Programlama: Kotlin Coroutines & Flow

* Görsel Yükleme: Coil (Asenkron GIF ve resim yönetimi)

🚀 Kurulum ve Çalıştırma

Projeyi yerel ortamınızda çalıştırmak için aşağıdaki adımları izleyin:

1. Depoyu Klonlayın:

git clone https://github.com/KULLANICI_ADINIZ/hava-durumu-app.git


2. API Anahtarlarını Temin Edin:

Hava durumu verileri için OpenWeatherMap'ten API Key alın.

Yapay zeka tavsiyeleri için Groq Cloud'dan ücretsiz Llama-3 API Key alın.

3. Anahtarları Projeye Ekleyin:
Projenin kök dizininde bulunan local.properties dosyasına gidin (yoksa oluşturun) ve anahtarlarınızı ekleyin. (Bu dosya güvenlik amacıyla .gitignore'a dahildir).

WEATHER_API_KEY=sizin_openweathermap_anahtariniz
GROQ_API_KEY=sizin_groq_api_anahtariniz



4. Android Studio'da projeyi açın, Sync Now (Gradle Senkronizasyonu) işlemini tamamlayın ve çalıştırın!
