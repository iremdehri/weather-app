package com.kampplus.hava.ui.screens

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.request.ImageRequest
import com.kampplus.hava.R
import com.kampplus.hava.model.WeatherForecast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Brush
import com.kampplus.hava.theme.LightBlue
import kotlinx.coroutines.delay
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.Thunderstorm
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.ui.draw.rotate
import com.kampplus.hava.model.HourlyForecast

@Composable
fun WeatherDetailScreen(forecasts: List<WeatherForecast>, hourlyForecasts: List<HourlyForecast>, aiMessage: String) {
    val todayWeather = forecasts.firstOrNull { it.isToday } ?: return
    val nextDays = forecasts.filter { !it.isToday }

    // Kaydırma (Scroll) durumunu hatırlayan değişken
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(LightBlue, Color.White)
                )
            )
            .verticalScroll(scrollState) // SAYFAYI KAYDIRILABİLİR YAPAN SİHİRLİ KOD BURASI
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 1. Üst Kısım: Gün ve Tarih Başlığı
        Text(
            text = todayWeather.dayName,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2D2D2D)
        )
        Text(
            text = todayWeather.date,
            fontSize = 16.sp,
            color = Color.DarkGray
        )

        Spacer(modifier = Modifier.height(32.dp))

        // 2. Bugünün Büyük Hava Durumu Kartı
        WeatherCardBig(forecast = todayWeather)

        Spacer(modifier = Modifier.height(48.dp))

        // 3. Yapay Zeka Tavsiye Baloncuğu
        AiInsightCard(insightText = aiMessage)

        Spacer(modifier = Modifier.height(32.dp))

        // 4. Gelecek 5 Gün (Artık saatlik detaylardan ÖNCE görünecek)
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(nextDays) { day ->
                WeatherCardSmall(forecast = day)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 5. Saatlik Detaylar (Artık günlerden SONRA, en altta görünecek)
        HourlyWeatherSection(hourlyData = hourlyForecasts)

        // Sayfanın en altında biraz boşluk kalsın diye
        Spacer(modifier = Modifier.height(32.dp))
    }
}

// BÜYÜK KART BİLEŞENİ (Bugün İçin)
@Composable
fun WeatherCardBig(forecast: WeatherForecast) {
    val context = LocalContext.current

    // Compose için GIF Oynatıcı Kurulumu
    val imageLoader = ImageLoader.Builder(context)
        .components {
            if (Build.VERSION.SDK_INT >= 28) {
                add(ImageDecoderDecoder.Factory())
            } else {
                add(GifDecoder.Factory())
            }
        }.build()

    // API'den gelen koda göre gösterilecek GIF'i seçiyoruz
    // (Bu kısımdaki R.drawable.anim_... dosyalarını res/drawable klasörüne eklemelisin)
    val gifResId = when (forecast.iconCode.take(2)) {
        "01" -> R.drawable.anim_sun
        "02", "03", "04" -> R.drawable.anim_clouds
        "09", "10" -> R.drawable.anim_rain
        "11" -> R.drawable.anim_storm
        "13" -> R.drawable.anim_snow
        "50" -> R.drawable.anim_wind_sock
        else -> R.drawable.anim_clouds
    }

    Box(
        modifier = Modifier
            .width(280.dp)
            // Yuvarlağın kartın altına taşması için Box'a alt boşluk veriyoruz
            .padding(bottom = 36.dp)
    ) {
        Card(
            shape = RoundedCornerShape(32.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(340.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Bugün Hava", color = Color.Gray, fontSize = 16.sp)
                Text(
                    text = forecast.condition, // Örn: "Sağanak Yağmurlu"
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 20.sp,
                    color = Color(0xFF2D2D2D)
                )

                Spacer(modifier = Modifier.height(16.dp)) // Boşluğu biraz kıstık (GIF için yer)

                // HAREKETLİ GIF BURADA GÖSTERİLİYOR
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(gifResId)
                        .build(),
                    imageLoader = imageLoader,
                    contentDescription = "Hava Durumu Animasyonu",
                    modifier = Modifier.size(160.dp)
                )
            }
        }

        // KARTIN DIŞINA TAŞAN DERECE YUVARLAĞI
        Surface(
            shape = CircleShape,
            color = Color.White,
            shadowElevation = 8.dp,
            modifier = Modifier
                .size(72.dp)
                .align(Alignment.BottomCenter)
                .offset(y = 36.dp) // Kendi yüksekliğinin yarısı kadar aşağı kaydır
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = "${forecast.temp}°",
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp,
                    color = Color.Black
                )
            }
        }
    }
}

// KÜÇÜK KART BİLEŞENİ (5 Günlük Liste İçin)
@Composable
fun WeatherCardSmall(forecast: WeatherForecast) {
    // API'den gelen koda göre gösterilecek ikon ve RENGİ belirliyoruz
    val (iconVector, iconTint) = when (forecast.iconCode.take(2)) {
        "01" -> Pair(Icons.Default.WbSunny, Color(0xFFFFD700)) // Tam parlak sarı güneş
        "02", "03", "04" -> Pair(Icons.Default.Cloud, Color(0xFFB0BEC5)) // Gri bulut
        "09", "10" -> Pair(Icons.Default.WaterDrop, Color(0xFF4FC3F7)) // Mavi yağmur damlası
        "11" -> Pair(Icons.Default.Thunderstorm, Color(0xFF546E7A)) // Koyu gri fırtına
        "13" -> Pair(Icons.Default.AcUnit, Color(0xFF81D4FA)) // Açık mavi kar
        "50" -> Pair(Icons.Default.Air, Color.Gray) // Sis/Rüzgar
        else -> Pair(Icons.Default.Cloud, Color.Gray)
    }

    Box(
        modifier = Modifier
            .width(108.dp)
            .padding(bottom = 24.dp)
    ) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = forecast.dayName, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(text = forecast.condition, color = Color.Gray, fontSize = 12.sp)

                Spacer(modifier = Modifier.weight(1f))

                // YENİ EKLENEN: Tamamen bizim kontrolümüzde olan Material İkon
                Icon(
                    imageVector = iconVector,
                    contentDescription = "Hava Durumu İkonu",
                    tint = iconTint,
                    modifier = Modifier.size(56.dp)
                )

                Spacer(modifier = Modifier.weight(1f))
            }
        }

        // KÜÇÜK KARTIN DERECE YUVARLAĞI
        Surface(
            shape = CircleShape,
            color = Color.White,
            shadowElevation = 4.dp,
            modifier = Modifier
                .size(48.dp)
                .align(Alignment.BottomCenter)
                .offset(y = 24.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = "${forecast.temp}°",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.Black
                )
            }
        }
    }
}

@Composable
fun AiInsightCard(insightText: String) {
    var isExpanded by remember { mutableStateOf(false) }
    var displayedText by remember { mutableStateOf("Sana bir mesajım var ✨") }
    val context = LocalContext.current

    val imageLoader = ImageLoader.Builder(context)
        .components {
            if (Build.VERSION.SDK_INT >= 28) add(ImageDecoderDecoder.Factory())
            else add(GifDecoder.Factory())
        }.build()

    // Baloncuğa tıklanınca ve API'den cevap gelmişse daktilo efektini başlat
    LaunchedEffect(isExpanded, insightText) {
        if (isExpanded && insightText != "Sana bir mesajım var ✨" && insightText.isNotBlank()) {
            displayedText = ""
            insightText.forEachIndexed { index, char ->
                delay(30)
                displayedText += char
            }
        } else {
            displayedText = "Sana bir mesajım var ✨"
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                isExpanded = !isExpanded
            },
        verticalAlignment = Alignment.Top
    ) {

        AsyncImage(
            model = ImageRequest.Builder(context).data(R.drawable.anim_chatbot).build(),
            imageLoader = imageLoader,
            contentDescription = "Robot Asistan",
            modifier = Modifier.size(64.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Surface(
            shape = RoundedCornerShape(
                topStart = 0.dp,    // Konuşma baloncuğu sivrisi
                topEnd = 24.dp,
                bottomEnd = 24.dp,
                bottomStart = 24.dp
            ),
            color = Color(0xFFEBF2FC),
            shadowElevation = 2.dp,
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = displayedText,
                fontSize = 14.sp,
                color = Color(0xFF3D4B5C),
                lineHeight = 22.sp,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Composable
fun HourlyWeatherSection(hourlyData: List<HourlyForecast>) {
    Column(modifier = Modifier.fillMaxWidth().padding(top = 24.dp)) {
        Text(
            text = "Saatlik Detaylar",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(hourlyData) { data ->
                HourlyWeatherItem(data)
            }
        }
    }
}

@Composable
fun HourlyWeatherItem(data: HourlyForecast) {
    Column(
        modifier = Modifier
            .background(Color.White.copy(alpha = 0.6f), shape = RoundedCornerShape(12.dp))
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = data.time, style = MaterialTheme.typography.labelMedium) // Örn: 15:00
        Text(text = "${data.temp}°", style = MaterialTheme.typography.titleLarge)

        Spacer(modifier = Modifier.height(8.dp))

        // Rüzgar ikonu ve hızı
        Icon(
            imageVector = Icons.Default.Air,
            contentDescription = "Rüzgar",
            tint = Color.Gray,
            modifier = Modifier.size(20.dp)
        )
        Text(text = "${data.windSpeed} km/s", style = MaterialTheme.typography.bodySmall)

        // Rüzgar yönü (API'den gelen dereceye göre ikonu döndürebilirsin)
        Icon(
            imageVector = Icons.Default.Navigation, // Ok ikonu
            contentDescription = "Rüzgar Yönü",
            tint = LightBlue,
            modifier = Modifier
                .size(16.dp)
                .rotate(data.windDegree.toFloat()) // Rüzgar yönüne göre oku döndürür
        )
    }
}
